package pl.mrndesign.matned.app.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import pl.mrndesign.matned.app.model.DrawType;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class LottoCardDto {

	private Long id;

	private String ownerSubject;

    private LocalDate firstDrawDate;

    private int numberOfDraws;

    private List<LottoCardNumbersDto> numbers;

    private DrawType drawType;

	public LottoCardDto(LocalDate firstDrawDate, int numberOfDraws, List<LottoCardNumbersDto> numbers, DrawType drawType) {
		this.firstDrawDate = firstDrawDate;
		this.numberOfDraws = numberOfDraws;
		this.numbers = numbers;
		this.drawType = drawType;
	}

	public boolean isPlus() {
        return DrawType.LOTTO_PLUS == drawType;
    }


}
