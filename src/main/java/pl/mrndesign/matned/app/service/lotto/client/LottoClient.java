package pl.mrndesign.matned.app.service.lotto.client;

import pl.mrndesign.matned.app.dto.LottoCardDto;
import pl.mrndesign.matned.app.dto.LottoDrawDto;

import java.util.List;

public interface LottoClient {

    List<LottoDrawDto> getDrawingsFor(LottoCardDto card);
}
