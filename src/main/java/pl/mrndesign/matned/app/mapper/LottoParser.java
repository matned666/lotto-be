package pl.mrndesign.matned.app.mapper;

import org.springframework.stereotype.Component;
import pl.mrndesign.matned.app.model.DrawType;
import pl.mrndesign.matned.app.dto.LottoDrawDto;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class LottoParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<LottoDrawDto> parse(String body, String drawTypeStr) throws Exception {
        JsonNode root = objectMapper.readTree(body);
        JsonNode results = root.get("items");
        List<LottoDrawDto> output = new ArrayList<>();
        if (results != null && results.isArray()) {
                for (JsonNode item : results) {
                    LottoDrawDto mapped = map(item);
                    if (DrawType.get(drawTypeStr.toUpperCase()) == mapped.getDrawType()) {
                        output.add(mapped);
                    }
                }
        }
        return output;
    }

    private LottoDrawDto map(JsonNode node) {
        String dateStr = node.get("drawDate").asString();
        var date = OffsetDateTime.parse(dateStr).toLocalDate();
        JsonNode resultsNode = node.get("results");
        JsonNode numbersNode = resultsNode.get(0).get("resultsJson");
        int[] numbers = new int[numbersNode.size()];
        for (int i = 0; i < numbersNode.size(); i++) {
            numbers[i] = numbersNode.get(i).asInt();
        }
        var drawType = mapType(node.get("gameType").asString(null));
        return new LottoDrawDto(date, numbers, drawType);
    }

    private DrawType mapType(String gameType) {
        if (gameType == null) return null;
        return switch (gameType.toUpperCase()) {
            case "LOTTO" -> DrawType.LOTTO;
            case "LOTTOPLUS" -> DrawType.LOTTO_PLUS;
            default -> throw new IllegalArgumentException("Unknown: " + gameType);
        };
    }
}
