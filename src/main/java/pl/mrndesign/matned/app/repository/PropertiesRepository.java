package pl.mrndesign.matned.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.mrndesign.matned.app.model.config.Properties;

import java.util.List;
import java.util.Optional;

public interface PropertiesRepository extends JpaRepository<Properties, Long> {

	@Query("""
    select p
    from Properties p
    where p.user.id = :userId
       or (
            p.user.id = 0
            and not exists (
                select 1
                from Properties p2
                where p2.user.id = :userId
                  and p2.name = p.name
            )
       )
    """)
	List<Properties> findPropertiesForUserId(@Param("userId") Long userId);

	@Query("""
    select p
    from Properties p
    where p.user.id = :userId
      and p.name = :name
    """)
	List<Properties> findPropertiesForNameAndUserId(@Param("userId") Long userId, @Param("name") String name);
}
