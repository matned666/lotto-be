package pl.mrndesign.matned.app.service.lotto.client.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.mrndesign.matned.app.exception.LottoException;
import pl.mrndesign.matned.app.mapper.LottoParser;
import pl.mrndesign.matned.app.dto.LottoCardDto;
import pl.mrndesign.matned.app.dto.LottoDrawDto;
import pl.mrndesign.matned.app.logging.LogSanitizer;
import pl.mrndesign.matned.app.service.common.PropertiesService;
import pl.mrndesign.matned.app.service.lotto.client.LottoClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LottoClientImpl implements LottoClient {

    private static final int MAX_SEARCH_DAYS_PADDING = 14;
    private static final int MAX_SEARCH_DAYS_PER_DRAWING = 4;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final LottoParser lottoParser;
    private final PropertiesService propertiesService;


    public LottoClientImpl(LottoParser lottoParser, PropertiesService propertiesService) {
        this.lottoParser = lottoParser;
        this.propertiesService = propertiesService;
    }

    @Override
    public List<LottoDrawDto> getDrawsFor(LottoCardDto card) {
        log.info("Fetching draws from Lotto API for {}", LogSanitizer.summarizeCard(card));
        var result = new ArrayList<LottoDrawDto>();
        for (String type : card.getDrawType().getTypesToRequest()) {
            log.info("Starting draw fetch sequence for type={}", type);
            getDrawResultForDate(card.getFirstDrawDate(), type, result, card.getNumberOfDraws(), 1);
        }
        log.info("Finished Lotto API fetch: {}", LogSanitizer.summarizeDrawBatch(result));
        return result;
    }

    public List<LottoDrawDto> getDrawResultForDate(LocalDate date, String drawTypeStr, List<LottoDrawDto> result, int numberOfDraws, int actualDraw) {
        if (date.isAfter(LocalDate.now()) || numberOfDraws < actualDraw ) {
            log.debug("Stopping draw fetch recursion: {}", LogSanitizer.summarizeDrawFetch(date, drawTypeStr, actualDraw, numberOfDraws));
            return result;
        }
        try {
            String responseBody = "";
            try {
                log.debug("Calling Lotto API: {}", LogSanitizer.summarizeDrawFetch(date, drawTypeStr, actualDraw, numberOfDraws));
                responseBody = makeRequest(date, drawTypeStr);
            } catch (LottoException e) {
                var nextDate = date.plusDays(1);
                log.info("No draw found for type={} on date={}, moving to next date={}", drawTypeStr, date, nextDate);
                return getDrawResultForDate(nextDate, drawTypeStr, result, numberOfDraws, actualDraw);
            }
            var lottoDraws = lottoParser.parse(responseBody, drawTypeStr);
            result.addAll(lottoDraws);
            log.info("Parsed Lotto API response for type={}: addedDraws={}, accumulatedDraws={}",
                    drawTypeStr, lottoDraws.size(), result.size());
            var nextDate = date.plusDays(1);
            return getDrawResultForDate(nextDate, drawTypeStr, result, numberOfDraws, ++actualDraw);
        } catch (Exception e) {
            log.error("Unexpected error while fetching draws: {}", LogSanitizer.summarizeDrawFetch(date, drawTypeStr, actualDraw, numberOfDraws), e);
            throw new RuntimeException(e);
        }
    }

    private String makeRequest(LocalDate date, String drawTypeStr) throws LottoException {
        var url = generateUrl(date, drawTypeStr);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120 Safari/537.36")
                .header("Accept", "application/json, text/plain, */*")
                .header("Referer", "https://www.lotto.pl/")
                .header("Origin", "https://www.lotto.pl")
                .header("Secret", propertiesService.getProperty("lotto.client.key"))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() == 404) {
                log.debug("Lotto API returned 404 for type={} and date={}", drawTypeStr, date);
                throw new LottoException(response.body());
            }

            if (response.statusCode() != 200) {
                log.warn("Lotto API returned unexpected status={} for type={} and date={}",
                        response.statusCode(), drawTypeStr, date);
                throw new RuntimeException("HTTP " + response.statusCode() + "\n" + response.body());
            }

            log.debug("Lotto API returned success for type={} and date={}", drawTypeStr, date);
            return response.body();
        } catch (java.io.IOException e) {
            log.error("I/O error while calling Lotto API for type={} and date={}", drawTypeStr, date, e);
            throw new RuntimeException("Could not connect to Lotto API.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while calling Lotto API for type={} and date={}", drawTypeStr, date, e);
            throw new RuntimeException("Interrupted while calling Lotto API.", e);
        } catch (LottoException e) {
            throw new LottoException(e.getMessage());
        }
    }

    private String generateUrl(LocalDate date, String drawTypeStr) {
        return propertiesService.getProperty("lotto.client.host") +
                propertiesService.getProperty("lotto.client.endpoints.byDatePerGame") +
                "?" + "drawDate=" + date.atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME) +
                "&" + "gameType=" + drawTypeStr +
                "&" + "index=" + 1 +
                "&" + "size=" + 5 +
                "&" + "sort=" + "drawDate" +
                "&" + "order=" + "ASC";
    }

}
