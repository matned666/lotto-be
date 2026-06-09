package pl.mrndesign.matned.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import pl.mrndesign.matned.app.model.DrawType;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class LottoCardDto {

    private final LocalDate firstDrawDate;

    private final int numberOfDraws;

    private final List<LottoCardNumbersDto> numbers;

    private DrawType drawType;

    public boolean isPlus() {
        return DrawType.LOTTO_PLUS == drawType;
    }


}
