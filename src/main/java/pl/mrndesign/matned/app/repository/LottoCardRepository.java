package pl.mrndesign.matned.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mrndesign.matned.app.model.LottoCard;

public interface LottoCardRepository extends JpaRepository<LottoCard, Long> {

    LottoCard findTopByOrderByIdDesc();
}
