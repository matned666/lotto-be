package pl.mrndesign.matned.app.service.lotto.common;

import pl.mrndesign.matned.app.dto.CheckResultDto;
import pl.mrndesign.matned.app.dto.LottoCardDto;
import pl.mrndesign.matned.app.exception.TooManyRequestsException;

import java.util.List;

public interface LottoService {

    List<CheckResultDto> checkDraw(LottoCardDto card) throws TooManyRequestsException;

    List<CheckResultDto> checkDrawStatic(LottoCardDto card);
}
