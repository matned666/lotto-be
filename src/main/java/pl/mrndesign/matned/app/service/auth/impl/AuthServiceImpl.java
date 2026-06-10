package pl.mrndesign.matned.app.service.auth.impl;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.mrndesign.matned.app.model.auth.User;
import pl.mrndesign.matned.app.repository.OAuthAccountRepository;
import pl.mrndesign.matned.app.repository.UserRepository;
import pl.mrndesign.matned.app.service.auth.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OAuthAccountRepository oAuthAccountRepository;

    public AuthServiceImpl(UserRepository userRepository, OAuthAccountRepository oAuthAccountRepository) {
        this.userRepository = userRepository;
        this.oAuthAccountRepository = oAuthAccountRepository;
    }

    @Override
    public void saveNewUser(Authentication authentication) {
//        TODO
    }

    @Override
    public boolean isUserSaved(String name) {
        return userRepository.findByName(name); //TODO
    }
}
