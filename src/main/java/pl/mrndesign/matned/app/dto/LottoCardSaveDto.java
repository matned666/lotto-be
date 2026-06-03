package pl.mrndesign.matned.app.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.mrndesign.matned.app.model.DrawType;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LottoCardSaveDto {

    private Long id;

    private LocalDate firstDrawDate;

    private int numberOfDrawings;

    private List<LottoCardNumbersDto> numbers;

    private DrawType drawType;
}
