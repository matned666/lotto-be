package pl.mrndesign.matned.app.service.lotto.check;

import pl.mrndesign.matned.app.dto.CheckResultDto;
import pl.mrndesign.matned.app.dto.LottoCardDto;
import pl.mrndesign.matned.app.dto.LottoDrawDto;

import java.util.List;

public interface CheckService {

    List<CheckResultDto> check(LottoCardDto card, List<LottoDrawDto> lottoDrawDtos);

}
