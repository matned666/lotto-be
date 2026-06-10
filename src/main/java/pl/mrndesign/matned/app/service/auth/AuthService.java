package pl.mrndesign.matned.app.service.auth;

import org.springframework.security.core.Authentication;

public interface AuthService {

    void saveNewUser(Authentication authentication);

    boolean isUserSaved(String name);

}
