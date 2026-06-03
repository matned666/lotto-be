package pl.mrndesign.matned.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.mrndesign.matned.app.model.DrawType;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class LottoDrawDto {

    private LocalDate date;

    private int[] numbers;

    private DrawType drawType;



}
