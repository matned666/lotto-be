package pl.mrndesign.matned.app.dto;

import java.util.Arrays;

public class LottoCardNumbersDto {

    private int[] numbers;

    public LottoCardNumbersDto(int[] numbers) {
        this.numbers = numbers;
    }

    public int[] getNumbers() {
        return numbers;
    }

    public void setNumbers(int[] numbers) {
        this.numbers = numbers;
    }

    @Override
    public String toString() {
        return "LottoCardNumbers{" +
                "numbers=" + Arrays.toString(numbers) +
                '}';
    }
}
