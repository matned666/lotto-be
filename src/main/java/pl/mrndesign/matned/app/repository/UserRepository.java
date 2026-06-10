package pl.mrndesign.matned.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mrndesign.matned.app.model.auth.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean findByName(String name);
}
