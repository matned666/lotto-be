package pl.mrndesign.matned.app.dto.auth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import pl.mrndesign.matned.app.model.auth.User;

import java.util.Collection;
import java.util.Map;

@Getter
public class AppOAuth2UserPrincipal implements OAuth2User {

	private final User user;
	private final OAuth2User delegate;
	private final Collection<? extends GrantedAuthority> authorities;

	public AppOAuth2UserPrincipal(User user, OAuth2User delegate) {
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
}
