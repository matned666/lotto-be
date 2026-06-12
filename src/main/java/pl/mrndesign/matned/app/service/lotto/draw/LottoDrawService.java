package pl.mrndesign.matned.app.service.lotto.draw;

import pl.mrndesign.matned.app.dto.LottoDrawDto;

import java.time.LocalDate;
import java.util.List;

public interface LottoDrawService {

	List<LottoDrawDto> findDrawsFor(LocalDate date, String drawTypeStr);

	List<LottoDrawDto> save(List<LottoDrawDto> lottoDraws);

	List<Integer> findTop10MostFrequentNumbers();
}
