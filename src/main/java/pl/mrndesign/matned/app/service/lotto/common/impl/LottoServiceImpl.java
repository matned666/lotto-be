package pl.mrndesign.matned.app.service.lotto.common.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.mrndesign.matned.app.dto.CheckResultDto;
import pl.mrndesign.matned.app.dto.LottoCardDto;
import pl.mrndesign.matned.app.logging.LogSanitizer;
import pl.mrndesign.matned.app.service.lotto.check.CheckService;
import pl.mrndesign.matned.app.service.lotto.client.LottoClient;
import pl.mrndesign.matned.app.service.lotto.client.impl.LottoClientMock;
import pl.mrndesign.matned.app.service.lotto.common.LottoService;

import java.util.List;

@Service
@Slf4j
public class LottoServiceImpl implements LottoService {

    private final LottoClient lottoClient;
    private final CheckService checkService;

    public LottoServiceImpl(LottoClient lottoClient, CheckService checkService) {
        this.lottoClient = lottoClient;
        this.checkService = checkService;
    }

    @Override
    public List<CheckResultDto> checkDraw(LottoCardDto card) {
        log.info("Starting live draw check: {}", LogSanitizer.summarizeCard(card));
        var drawings = lottoClient.getDrawsFor(card);
        log.info("Fetched live draws: {}", LogSanitizer.summarizeDrawBatch(drawings));
        var results = checkService.check(card, drawings);
        log.info("Finished live draw check: {}", LogSanitizer.summarizeResults(results));
        return results;
    }

    @Override
    public List<CheckResultDto> checkDrawStatic(LottoCardDto card) {
        log.info("Starting static draw check: {}", LogSanitizer.summarizeCard(card));
        var client = new LottoClientMock();
        var drawings = client.getDrawsFor(card);
        log.info("Fetched static draws: {}", LogSanitizer.summarizeDrawBatch(drawings));
        var results = checkService.check(card, drawings);
        log.info("Finished static draw check: {}", LogSanitizer.summarizeResults(results));
        return results;
    }

}
