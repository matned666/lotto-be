package pl.mrndesign.matned.app.model;

import java.security.InvalidParameterException;
import java.util.List;

public enum DrawType {

    LOTTO(List.of("Lotto")),
    LOTTO_PLUS(List.of("Lotto", "LottoPlus"));

    private final List<String> typesToRequest;

    DrawType(List<String> typesToRequest) {
        this.typesToRequest = typesToRequest;
    }

    public List<String> getTypesToRequest() {
        return typesToRequest;
    }

    public static DrawType get(String drawStr){
        return switch (drawStr.toUpperCase()) {
            case "LOTTO" -> LOTTO;
            case "LOTTOPLUS" -> LOTTO_PLUS;
            default -> throw new InvalidParameterException("No DrawType:" + drawStr);
        };
    }
}
