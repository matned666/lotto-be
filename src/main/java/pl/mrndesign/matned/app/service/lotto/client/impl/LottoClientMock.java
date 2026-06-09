package pl.mrndesign.matned.app.service.lotto.client.impl;

import pl.mrndesign.matned.app.model.DrawType;
import pl.mrndesign.matned.app.dto.LottoCardDto;
import pl.mrndesign.matned.app.dto.LottoDrawDto;
import pl.mrndesign.matned.app.service.lotto.client.LottoClient;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public class LottoClientMock implements LottoClient {


    @Override
    public List<LottoDrawDto> getDrawsFor(LottoCardDto card) {
        return Stream.of(
                new LottoDrawDto(LocalDate.of(2026, 5, 28), new int[]{6, 7, 15, 22, 42, 43}, DrawType.LOTTO),
                new LottoDrawDto(LocalDate.of(2026, 5, 28), new int[]{2, 4, 8, 24, 32, 38}, DrawType.LOTTO_PLUS),
                new LottoDrawDto(LocalDate.of(2026, 5, 30), new int[]{1, 6, 10, 32, 35, 42}, DrawType.LOTTO),
                new LottoDrawDto(LocalDate.of(2026, 5, 30), new int[]{2, 19, 25, 32, 46, 47}, DrawType.LOTTO_PLUS),
                new LottoDrawDto(LocalDate.of(2026, 6, 2), new int[]{8, 20, 28, 31, 36, 39}, DrawType.LOTTO),
                new LottoDrawDto(LocalDate.of(2026, 6, 2), new int[]{4, 10, 13, 34, 38, 46}, DrawType.LOTTO_PLUS),
                new LottoDrawDto(LocalDate.of(2026, 6, 4), new int[]{13, 17, 20, 41, 43, 46}, DrawType.LOTTO),
                new LottoDrawDto(LocalDate.of(2026, 6, 4), new int[]{2, 3, 10, 13, 26, 42}, DrawType.LOTTO_PLUS),
                new LottoDrawDto(LocalDate.of(2026, 6, 6), new int[]{7, 8, 17, 32, 36, 47}, DrawType.LOTTO),
                new LottoDrawDto(LocalDate.of(2026, 6, 6), new int[]{17, 29, 34, 36, 41, 48}, DrawType.LOTTO_PLUS)
        )
                .filter(d -> card.getFirstDrawDate().isEqual(d.getDate()) || d.getDate().isAfter(card.getFirstDrawDate()))
                .filter(d -> card.isPlus() || (!card.isPlus() && d.getDrawType() != DrawType.LOTTO_PLUS))
                .limit(card.isPlus()? card.getNumberOfDraws() * 2: card.getNumberOfDraws())
                .toList();
    }
}
