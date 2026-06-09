package pl.mrndesign.matned.app.service.lotto.client.impl;

import org.springframework.stereotype.Service;
import pl.mrndesign.matned.app.mapper.LottoParser;
import pl.mrndesign.matned.app.dto.LottoCardDto;
import pl.mrndesign.matned.app.dto.LottoDrawDto;
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
    public List<LottoDrawDto> getDrawingsFor(LottoCardDto card) {
        var result = new ArrayList<LottoDrawDto>();
        for (String type : card.getDrawType().getTypesToRequest()) {
            result.addAll(getDrawResults(card.getFirstDrawDate(), card.getNumberOfDrawings(), type));
        }
        return result;
    }

    private List<LottoDrawDto> getDrawResults(LocalDate firstDrawDate, int numberOfDrawings, String drawTypeStr) {
        var result = new ArrayList<LottoDrawDto>();
        var date = firstDrawDate;
        var today = LocalDate.now();
        var maxSearchDays = numberOfDrawings * MAX_SEARCH_DAYS_PER_DRAWING + MAX_SEARCH_DAYS_PADDING;

        for (int searchedDays = 0; !date.isAfter(today) && result.size() < numberOfDrawings && searchedDays < maxSearchDays; searchedDays++) {
            try {
                var responseBody = makeRequest(date, drawTypeStr);
                var lottoDraws = lottoParser.parse(responseBody, drawTypeStr);
                result.addAll(lottoDraws);
            } catch (Exception e) {
                throw new RuntimeException("Could not read Lotto draws for " + date + " and " + drawTypeStr + ".", e);
            }
            date = date.plusDays(1);
        }

        return result.size() > numberOfDrawings
                ? result.subList(0, numberOfDrawings)
                : result;
    }

    private String makeRequest(LocalDate date, String drawTypeStr) {
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

            if (response.statusCode() != 200) {
                throw new RuntimeException("HTTP " + response.statusCode() + "\n" + response.body());
            }

            return response.body();
        } catch (java.io.IOException e) {
            throw new RuntimeException("Could not connect to Lotto API.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while calling Lotto API.", e);
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
