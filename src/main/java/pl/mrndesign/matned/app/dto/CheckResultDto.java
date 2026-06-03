package pl.mrndesign.matned.app.dto;

import lombok.Getter;
import pl.mrndesign.matned.app.model.DrawType;

import java.util.Arrays;

@Getter
public class CheckResultDto {

    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String WHITE = "\u001B[37m";
    public static final String BRIGHT_WHITE = "\u001B[97m";
    public static final String RESET = "\u001B[0m";

    private final LottoCardNumbersDto lottoCardNumbersDto;

    private final LottoDrawDto lottoDrawDto;

    private final int[] matchingNumbers;

    private String color;

    public CheckResultDto(LottoCardNumbersDto lottoCardNumbersDto, LottoDrawDto lottoDrawDto, int[] matchingNumbers) {
        this.lottoCardNumbersDto = lottoCardNumbersDto;
        this.lottoDrawDto = lottoDrawDto;
        this.matchingNumbers = matchingNumbers;
        this.color = getFontColor(matchingNumbers.length);
    }

    private String getFontColor(final int machings) {
        return switch (machings) {
            case 3 -> color = YELLOW;
            case 4 -> color = BLUE;
            case 5 -> color = GREEN;
            case 6 -> color = RED;
            default -> color = WHITE;
        };
    }

    private String info() {
        var length = matchingNumbers.length;
        return switch (length) {
            case 1 -> "dupa";
            case 2 -> BRIGHT_WHITE + "no prawie";
            case 3 -> color + "Zawsze coś";
            case 4 -> color + "Będzie na obiad";
            case 5 -> color + "WOW, Dużo kasy!";
            case 6 -> color + "WYGRANA !!!!";
            default -> "dupa zbita";
        };
    }

    @Override
    public String toString() {
        return color +
                WHITE + "wynik:" + color + matchingNumbers.length +
                WHITE + ", pasujące liczby:=" + color + Arrays.toString(matchingNumbers) +
                WHITE + ", zakład:" + color + Arrays.toString(lottoCardNumbersDto.getNumbers()) +
                WHITE + ", numery z losowania:" + color + Arrays.toString(lottoDrawDto.getNumbers()) +
                (lottoDrawDto.getDrawType() == DrawType.LOTTO_PLUS? WHITE + ", PLUS" : ", GŁÓWNE LOSOWANIE") +
                WHITE + ", data losowania:" + color + lottoDrawDto.getDate() +
                WHITE + " -> " + info() +
                RESET;
    }
}
