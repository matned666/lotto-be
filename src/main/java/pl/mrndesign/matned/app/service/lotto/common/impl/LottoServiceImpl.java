package pl.mrndesign.matned.app.service.lotto.common.impl;

import org.springframework.stereotype.Service;
import pl.mrndesign.matned.app.dto.CheckResultDto;
import pl.mrndesign.matned.app.dto.LottoCardDto;
import pl.mrndesign.matned.app.service.lotto.check.CheckService;
import pl.mrndesign.matned.app.service.lotto.client.LottoClient;
import pl.mrndesign.matned.app.service.lotto.client.impl.LottoClientMock;
import pl.mrndesign.matned.app.service.lotto.common.LottoService;

import java.util.List;

@Service
public class LottoServiceImpl implements LottoService {

    private final LottoClient lottoClient;
    private final CheckService checkService;

    public LottoServiceImpl(LottoClient lottoClient, CheckService checkService) {
        this.lottoClient = lottoClient;
        this.checkService = checkService;
    }

    @Override
    public List<CheckResultDto> checkDraw(LottoCardDto card) {
        var drawings = lottoClient.getDrawsFor(card);
        return checkService.check(card, drawings);
    }

    @Override
    public List<CheckResultDto> checkDrawStatic(LottoCardDto card) {
        var client = new LottoClientMock();
        var drawings = client.getDrawsFor(card);
        return checkService.check(card, drawings);
    }

}
