package pl.mrndesign.matned.app.dto.auth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import pl.mrndesign.matned.app.model.auth.User;

import java.util.Collection;
import java.util.Map;

@Getter
public class AppOidcUserPrincipal implements OidcUser {

	private final User user;
	private final OidcUser delegate;
	private final Collection<? extends GrantedAuthority> authorities;

	public AppOidcUserPrincipal(User user, OidcUser delegate) {
		this.user = user;
		this.delegate = delegate;
		this.authorities = user.getAuthorities().stream()
				.map(authority -> new SimpleGrantedAuthority(authority.getName()))
				.toList();
	}

	@Override
	public Map<String, Object> getAttributes() {
		return delegate.getAttributes();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getName() {
		return delegate.getName();
	}

	@Override
	public Map<String, Object> getClaims() {
		return delegate.getClaims();
	}

	@Override
	public OidcUserInfo getUserInfo() {
		return delegate.getUserInfo();
	}

	@Override
	public OidcIdToken getIdToken() {
		return delegate.getIdToken();
	}
}