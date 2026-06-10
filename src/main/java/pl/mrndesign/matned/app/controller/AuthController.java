package pl.mrndesign.matned.app.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.mrndesign.matned.app.service.auth.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public AuthenticatedUserResponse currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        if (authService.isUserSaved(authentication.getName())) {
            authService.saveNewUser(authentication);
        }
        var attributes = principalAttributes(authentication.getPrincipal());
        var displayName = firstString(attributes, "name", "given_name", "preferred_username", "email");
        var email = firstString(attributes, "email", "preferred_username", "upn");
        var authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return new AuthenticatedUserResponse(
                authentication.getName(),
                displayName == null ? authentication.getName() : displayName,
                email,
                authorities
        );
    }

    @GetMapping("/csrf")
    public CsrfToken csrfToken(CsrfToken csrfToken) {
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
            List<String> authorities
    ) {
    }
}