package pl.mrndesign.matned.app.config;

import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pl.mrndesign.matned.app.service.auth.AuthService;

@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
@EnableMethodSecurity
public class SecurityConfiguration {

	private final AuthService authService;

	public SecurityConfiguration(AuthService authService) {
		this.authService = authService;
	}

	@Bean
	SecurityFilterChain apiSecurityFilterChain(
			HttpSecurity http,
			SecurityProperties securityProperties,
			ObjectProvider<ClientRegistrationRepository> clientRegistrations
	) throws Exception {
		http
				.csrf(csrf -> csrf
						.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
						.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
				.cors(Customizer.withDefaults())
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/error").permitAll()
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/auth/csrf").permitAll()
						.requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
						.anyRequest().access(new WebExpressionAuthorizationManager(
								"isAuthenticated() and (hasRole('USER') or hasRole('ADMIN')) and !hasRole('BLOCKED')"
						)))
				.logout(logout -> logout
						.logoutUrl("/api/auth/logout")
						.logoutSuccessUrl(securityProperties.logoutSuccessUrl())
						.invalidateHttpSession(true)
						.clearAuthentication(true)
						.deleteCookies("JSESSIONID"))
				.exceptionHandling(exceptions -> exceptions
						.defaultAuthenticationEntryPointFor(
								new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
								PathPatternRequestMatcher.withDefaults().matcher("/**")
						)
				);

		if (clientRegistrations.getIfAvailable() == null) {
			http.exceptionHandling(exceptions -> exceptions
					.authenticationEntryPoint(
							(request, response, exception) ->
									response.sendError(HttpServletResponse.SC_UNAUTHORIZED)

					));
		} else {
			http.oauth2Login(oauth2 -> oauth2
					.defaultSuccessUrl(securityProperties.loginSuccessUrl(), true)
					.userInfoEndpoint(userInfo -> userInfo
							.userService(this.authService)
							.oidcUserService(this.authService::loadOidcUser))
			);
		}

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource(SecurityProperties securityProperties) {
		var configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(securityProperties.allowedOriginPatterns());
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With", "X-XSRF-TOKEN"));
		configuration.setExposedHeaders(List.of("Location"));
		configuration.setAllowCredentials(true);

		var source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("https://github.com/login", configuration);
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
