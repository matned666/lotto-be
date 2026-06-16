package pl.mrndesign.matned.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mrndesign.matned.app.model.auth.AuthProviderType;
import pl.mrndesign.matned.app.model.auth.OAuthAccount;

import java.util.Optional;

public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {

	Optional<OAuthAccount> findByProviderAndProviderUserId(
			AuthProviderType provider,
			String providerUserId
	);

}
