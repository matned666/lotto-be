package pl.mrndesign.matned.app.service.auth.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.mrndesign.matned.app.logging.LogSanitizer;
import pl.mrndesign.matned.app.model.auth.User;
import pl.mrndesign.matned.app.repository.OAuthAccountRepository;
import pl.mrndesign.matned.app.repository.UserRepository;
import pl.mrndesign.matned.app.service.auth.AuthService;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OAuthAccountRepository oAuthAccountRepository;

    public AuthServiceImpl(UserRepository userRepository, OAuthAccountRepository oAuthAccountRepository) {
        this.userRepository = userRepository;
        this.oAuthAccountRepository = oAuthAccountRepository;
    }

    @Override
    public void saveNewUser(Authentication authentication) {
        String subject = authentication == null ? null : authentication.getName();
        log.info("saveNewUser invoked for subject={}. Method is not implemented yet, skipping persistence.",
                LogSanitizer.maskSubject(subject));
    }

    @Override
    public boolean isUserSaved(String name) {
//        boolean exists = userRepository.findByName(name); //TODO
        log.info("Checked user presence for subject={}: exists={}", LogSanitizer.maskSubject(name), null);
        return true;
    }
}
