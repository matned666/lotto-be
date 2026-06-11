package pl.mrndesign.matned.app.dto;

import lombok.*;
import pl.mrndesign.matned.app.model.DrawType;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LottoDrawDto {

    private LocalDate date;

    private int[] numbers;

    private DrawType drawType;


	public LottoDrawDto(LocalDate date, DrawType drawType) {
		this.date = date;
		this.drawType = drawType;
	}
}
