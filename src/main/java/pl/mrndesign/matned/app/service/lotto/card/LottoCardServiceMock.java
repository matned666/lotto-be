package pl.mrndesign.matned.app.service.lotto.card;

import pl.mrndesign.matned.app.model.DrawType;
import pl.mrndesign.matned.app.dto.LottoCardDto;
import pl.mrndesign.matned.app.dto.LottoCardNumbersDto;

import java.time.LocalDate;
import java.util.List;

public class LottoCardServiceMock implements LottoCardService {
    @Override
    public LottoCardDto getLottoCard() {
        var firstDrawingDate = LocalDate.of(2026, 5, 30);
        var numberOfDraws = 5;
        var numbers = List.of(
                new LottoCardNumbersDto(new int[]{1,5,19,23,36,37}),
                new LottoCardNumbersDto(new int[]{4,16,22,31,36,42})
        );
        return new LottoCardDto(firstDrawingDate, numberOfDraws, numbers, DrawType.LOTTO_PLUS);
    }
}
