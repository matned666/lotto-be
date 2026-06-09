package pl.mrndesign.matned.app.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
        List<String> allowedOriginPatterns,
        String loginSuccessUrl,
        String logoutSuccessUrl
) {

    public SecurityProperties {
        allowedOriginPatterns = allowedOriginPatterns == null || allowedOriginPatterns.isEmpty()
                ? List.of("http://localhost:*", "http://127.0.0.1:*")
                : List.copyOf(allowedOriginPatterns);
        loginSuccessUrl = hasText(loginSuccessUrl) ? loginSuccessUrl : "/";
        logoutSuccessUrl = hasText(logoutSuccessUrl) ? logoutSuccessUrl : "/";
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}