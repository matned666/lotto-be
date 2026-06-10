package pl.mrndesign.matned.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mrndesign.matned.app.model.auth.OAuthAccount;

public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {

}
