package pl.mrndesign.matned.app.service.lotto.client;

import pl.mrndesign.matned.app.dto.LottoCardDto;
import pl.mrndesign.matned.app.dto.LottoDrawDto;
import pl.mrndesign.matned.app.exception.TooManyRequestsException;

import java.util.List;

public interface LottoClient {

    List<LottoDrawDto> getDrawsFor(LottoCardDto card) throws TooManyRequestsException;
}
