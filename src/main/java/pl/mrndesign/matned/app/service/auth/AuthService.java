package pl.mrndesign.matned.app.service.auth;

import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import pl.mrndesign.matned.app.dto.auth.AppOAuth2UserPrincipal;
import pl.mrndesign.matned.app.dto.auth.AppOidcUserPrincipal;
import pl.mrndesign.matned.app.model.auth.AuthProviderType;
import pl.mrndesign.matned.app.model.auth.Authority;
import pl.mrndesign.matned.app.model.auth.OAuthAccount;
import pl.mrndesign.matned.app.model.auth.User;
import pl.mrndesign.matned.app.model.config.Properties;
import pl.mrndesign.matned.app.model.config.PropertyType;
import pl.mrndesign.matned.app.repository.AuthorityRepository;
import pl.mrndesign.matned.app.repository.OAuthAccountRepository;
import pl.mrndesign.matned.app.repository.PropertiesRepository;
import pl.mrndesign.matned.app.repository.UserRepository;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class AuthService extends DefaultOAuth2UserService {

	private final OidcUserService oidcUserService = new OidcUserService();
    private final UserRepository userRepository;
    private final OAuthAccountRepository oauthAccountRepository;
	private final AuthorityRepository authorityRepository;
	private final PropertiesRepository propertiesRepository;

    public AuthService(UserRepository userRepository,
                       OAuthAccountRepository oauthAccountRepository,
                       AuthorityRepository authorityRepository,
                       PropertiesRepository propertiesRepository) {
        this.userRepository = userRepository;
        this.oauthAccountRepository = oauthAccountRepository;
	    this.authorityRepository = authorityRepository;
	    this.propertiesRepository = propertiesRepository;
    }

	@Override
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest request)
			throws OAuth2AuthenticationException {
		OAuth2User oauthUser = super.loadUser(request);
		User user = saveOrUpdateOAuthUser(
				request.getClientRegistration().getRegistrationId(),
				oauthUser.getAttributes()
		);
		return new AppOAuth2UserPrincipal(user, oauthUser);
	}

	@Transactional
	public OidcUser loadOidcUser(OidcUserRequest request)
			throws OAuth2AuthenticationException {
		OidcUser oidcUser = oidcUserService.loadUser(request);
		User user = saveOrUpdateOAuthUser(
				request.getClientRegistration().getRegistrationId(),
				oidcUser.getAttributes()
		);
		return new AppOidcUserPrincipal(user, oidcUser);
	}

	@Transactional
	public User getByAuth(Authentication authentication) {
		Object principal = authentication.getPrincipal();
		if (principal instanceof AppOAuth2UserPrincipal appUser) {
			return appUser.getUser();
		}
		if (principal instanceof AppOidcUserPrincipal appUser) {
			return appUser.getUser();
		}
		throw new IllegalStateException("Unsupported authentication principal");
	}

	private User saveOrUpdateOAuthUser(
			String registrationId,
			Map<String, Object> attributes
	) {
		AuthProviderType provider = AuthProviderType.valueOf(
				registrationId.toUpperCase()
		);

		String providerUserId = extractProviderUserId(provider, attributes);
		String extractedEmail = extractEmail(provider, attributes);

		String email = extractedEmail != null && !extractedEmail.isBlank()
				? extractedEmail
				: provider.name().toLowerCase()
				  + "_"
				  + providerUserId
				  + "@users.noreply.local";

		String name = extractName(provider, attributes);
		String avatarUrl = extractAvatarUrl(provider, attributes);

		AtomicBoolean newUser = new AtomicBoolean(true);
		OAuthAccount account = oauthAccountRepository
				.findByProviderAndProviderUserId(provider, providerUserId)
				.map(existingAccount -> {
					newUser.set(false);
					return existingAccount;
				})
				.orElseGet(() -> createAccount(
						provider,
						providerUserId,
						email,
						name,
						avatarUrl
				));

		User user = account.getUser();
		user.setName(name);
		user.setAvatarUrl(avatarUrl);
		user.setUpdatedAt(Instant.now());
		user.setEmail(email);

		User saved = userRepository.save(user);
		userRepository.flush();

		if(newUser.get()) {
			propertiesRepository.save(Properties.builder()
					.type(PropertyType.EMAIL)
					.name("prop.data.email")
					.value(user.getEmail())
					.user(saved)
					.enabled(true)
					.build());
		}

		return saved;
	}
	private OAuthAccount createAccount(
			AuthProviderType provider,
			String providerUserId,
			String email,
			String name,
			String avatarUrl
	) {
		Instant now = Instant.now();

		Authority userRole = authorityRepository
				.findByName("ROLE_USER")
				.orElseThrow(() -> new IllegalStateException("ROLE_USER not found"));

		User user = userRepository.findByEmail(email)
				.orElseGet(() -> {
					User newUser = User.builder()
							.email(email)
							.name(name)
							.avatarUrl(avatarUrl)
							.createdAt(now)
							.updatedAt(now)
							.build();

					newUser.getAuthorities().add(userRole);

					return userRepository.save(newUser);
				});

		OAuthAccount account = OAuthAccount.builder()
				.user(user)
				.provider(provider)
				.providerUserId(providerUserId)
				.build();

		return oauthAccountRepository.save(account);
	}

	private String extractProviderUserId(AuthProviderType provider, Map<String, Object> attrs) {
		return switch (provider) {
			case GOOGLE -> (String) attrs.get("sub");
			case GITHUB -> String.valueOf(attrs.get("id"));
		};
	}

	private String extractEmail(AuthProviderType provider, Map<String, Object> attrs) {
		return (String) attrs.get("email");
	}

	private String extractName(AuthProviderType provider, Map<String, Object> attrs) {
		return switch (provider) {
			case GOOGLE -> (String) attrs.get("name");
			case GITHUB -> (String) attrs.getOrDefault("name", attrs.get("login"));
		};
	}

	private String extractAvatarUrl(AuthProviderType provider, Map<String, Object> attrs) {
		return switch (provider) {
			case GOOGLE -> (String) attrs.get("picture");
			case GITHUB -> (String) attrs.get("avatar_url");
		};
	}
}

