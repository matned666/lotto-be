package pl.mrndesign.matned.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mrndesign.matned.app.model.LottoCard;

import java.util.List;
import java.util.Optional;

public interface LottoCardRepository extends JpaRepository<LottoCard, Long> {

    List<LottoCard> findAllByOwnerSubjectOrderByIdDesc(String ownerSubject);

    Optional<LottoCard> findByIdAndOwnerSubject(Long id, String ownerSubject);

    LottoCard findTopByOwnerSubjectOrderByIdDesc(String ownerSubject);
}
