package pl.mrndesign.matned.app.logging;

import pl.mrndesign.matned.app.dto.CheckResultDto;
import pl.mrndesign.matned.app.dto.LottoCardDto;
import pl.mrndesign.matned.app.dto.LottoCardNumbersDto;
import pl.mrndesign.matned.app.dto.LottoDrawDto;
import pl.mrndesign.matned.app.model.DrawType;

import java.time.LocalDate;
import java.util.List;

public final class LogSanitizer {

    private LogSanitizer() {
    }

    public static String maskSubject(String subject) {
        if (subject == null || subject.isBlank()) {
            return "anonymous";
        }
        if (subject.length() <= 4) {
            return "****";
        }
        return subject.substring(0, 2) + "***" + subject.substring(subject.length() - 2);
    }

    public static String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return "n/a";
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return "***";
        }
        return email.charAt(0) + "***" + email.substring(atIndex);
    }

    public static String summarizeCard(LottoCardDto card) {
        if (card == null) {
            return "card=null";
        }
        return "drawType=" + drawType(card.getDrawType())
                + ", firstDrawDate=" + safeDate(card.getFirstDrawDate())
                + ", draws=" + card.getNumberOfDraws()
                + ", groups=" + numberGroups(card.getNumbers());
    }

    public static String summarizeSavedCard(LottoCardDto card) {
        if (card == null) {
            return "card=null";
        }
        return "cardId=" + card.getId()
                + ", drawType=" + drawType(card.getDrawType())
                + ", firstDrawDate=" + safeDate(card.getFirstDrawDate())
                + ", drawings=" + card.getNumberOfDraws()
                + ", groups=" + numberGroups(card.getNumbers());
    }

    public static String summarizeDrawFetch(LocalDate date, String drawType, int drawIndex, int expectedDraws) {
        return "drawType=" + drawType
                + ", date=" + safeDate(date)
                + ", requestedIndex=" + drawIndex
                + ", expectedDraws=" + expectedDraws;
    }

    public static String summarizeDrawBatch(List<LottoDrawDto> draws) {
        if (draws == null || draws.isEmpty()) {
            return "draws=0";
        }
        LocalDate firstDate = draws.stream().map(LottoDrawDto::getDate).min(LocalDate::compareTo).orElse(null);
        LocalDate lastDate = draws.stream().map(LottoDrawDto::getDate).max(LocalDate::compareTo).orElse(null);
        return "draws=" + draws.size()
                + ", from=" + safeDate(firstDate)
                + ", to=" + safeDate(lastDate);
    }

    public static String summarizeResults(List<CheckResultDto> results) {
        if (results == null || results.isEmpty()) {
            return "results=0";
        }
        int bestMatch = results.stream()
                .mapToInt(result -> result.getMatchingNumbers().length)
                .max()
                .orElse(0);
        long winningResults = results.stream()
                .filter(result -> result.getMatchingNumbers().length >= 3)
                .count();
        return "results=" + results.size()
                + ", winningResults=" + winningResults
                + ", bestMatch=" + bestMatch;
    }

    private static int numberGroups(List<LottoCardNumbersDto> numbers) {
        return numbers == null ? 0 : numbers.size();
    }

    private static String drawType(DrawType drawType) {
        return drawType == null ? "null" : drawType.name();
    }

    private static String safeDate(LocalDate date) {
        return date == null ? "null" : date.toString();
    }
}
