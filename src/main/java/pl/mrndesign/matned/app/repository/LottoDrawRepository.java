package pl.mrndesign.matned.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.mrndesign.matned.app.model.DrawType;
import pl.mrndesign.matned.app.model.LottoDraw;

import java.time.LocalDate;
import java.util.List;

public interface LottoDrawRepository extends JpaRepository<LottoDraw, Long> {

	@Query("SELECT ld FROM LottoDraw ld WHERE ld.date = :date AND ld.drawType = :drawType")
	List<LottoDraw> findAllLottoDrawsByDateAndDrawType(@Param("date") LocalDate date, @Param("drawType") DrawType drawType);

	@Query("SELECT DISTINCT ld FROM LottoDraw ld JOIN FETCH ld.numbers numbers LEFT JOIN FETCH numbers.numbers")
	List<LottoDraw> findAllWithNumbers();

}
