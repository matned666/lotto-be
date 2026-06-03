package pl.mrndesign.matned.app;

import pl.mrndesign.matned.app.service.lotto.card.LottoCardServiceMock;
import pl.mrndesign.matned.app.service.lotto.check.impl.CheckServiceImpl;
import pl.mrndesign.matned.app.service.lotto.client.impl.LottoClientMock;
import pl.mrndesign.matned.app.dto.CheckResultDto;

public class Main {

    static void main(String[] args) {
        var lottoClient = new LottoClientMock();
        var lottoCardService = new LottoCardServiceMock();
        var checkService = new CheckServiceImpl();

        var card = lottoCardService.getLottoCard();
        var drawings = lottoClient.getDrawingsFor(card);

        var results = checkService.check(card, drawings);

        for (CheckResultDto result : results) {
            System.out.println(result);
        }
    }
}
