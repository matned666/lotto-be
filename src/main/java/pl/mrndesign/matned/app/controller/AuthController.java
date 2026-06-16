package pl.mrndesign.matned.app.controller;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.mrndesign.matned.app.logging.LogSanitizer;
import pl.mrndesign.matned.app.model.auth.Authority;
import pl.mrndesign.matned.app.service.auth.AuthService;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@GetMapping("/me")
    public AuthenticatedUserResponse currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Rejected /api/auth/me request due to missing or unauthenticated principal.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        log.info("Processing /api/auth/me for subject={}", LogSanitizer.maskSubject(authentication.getName()));

		var user = authService.getByAuth(authentication);

        var displayName = user.getName();
        var email = user.getEmail();
        var authorities = user.getAuthorities();

        var response = new AuthenticatedUserResponse(
                authentication.getName(),
                displayName == null ? authentication.getName() : displayName,
                email,
                authorities.stream().map(Authority::getName).toList(),
		        user.getAvatarUrl()
        );
        log.info("Resolved authenticated user: subject={}, email={}, authorities={}",
                LogSanitizer.maskSubject(response.subject()),
                LogSanitizer.maskEmail(response.email()),
                response.authorities().size());
        return response;
    }

    @GetMapping("/csrf")
    public CsrfToken csrfToken(CsrfToken csrfToken) {
        log.debug("Issued CSRF token response.");
        return csrfToken;
    }

    private static Map<String, Object> principalAttributes(Object principal) {
        return principal instanceof OAuth2User oauth2User ? oauth2User.getAttributes() : Map.of();
    }

    private static String firstString(Map<String, Object> attributes, String... names) {
        for (var name : names) {
            var value = attributes.get(name);
            if (value instanceof String text && !text.isBlank()) {
                return text;
            }
        }
        return null;
    }

    public record AuthenticatedUserResponse(
            String subject,
            String displayName,
            String email,
            List<String> authorities,
            String avatar
    ) {
    }
}
