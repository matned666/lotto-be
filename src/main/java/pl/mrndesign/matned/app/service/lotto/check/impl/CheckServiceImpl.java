package pl.mrndesign.matned.app.service.lotto.check.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.mrndesign.matned.app.dto.CheckResultDto;
import pl.mrndesign.matned.app.dto.LottoCardDto;
import pl.mrndesign.matned.app.dto.LottoCardNumbersDto;
import pl.mrndesign.matned.app.dto.LottoDrawDto;
import pl.mrndesign.matned.app.logging.LogSanitizer;
import pl.mrndesign.matned.app.service.lotto.check.CheckService;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CheckServiceImpl implements CheckService {

    public List<CheckResultDto> check(LottoCardDto card, List<LottoDrawDto> lottoDrawDtos) {
        log.info("Calculating check results for card={}, draws={}",
                LogSanitizer.summarizeCard(card),
                LogSanitizer.summarizeDrawBatch(lottoDrawDtos));
        var results = new ArrayList<CheckResultDto>();
        for (LottoCardNumbersDto cardNumber : card.getNumbers()) {
            for (LottoDrawDto draw : lottoDrawDtos) {
                int[] checkResult = checkResult(cardNumber.getNumbers(), draw.getNumbers());
                results.add(new CheckResultDto(cardNumber, draw, checkResult));
            }
        }
        log.info("Calculated check results summary: {}", LogSanitizer.summarizeResults(results));
        return results;
    }

    private int[] checkResult(int[] toCheck, int[] result) {
        List<Integer> common = new ArrayList<>();
        for (int num1 : toCheck) {
            for (int num2 : result) {
                if (num1 == num2) {
                    common.add(num1);
                    break; // aby nie dodać tego samego elementu wielokrotnie
                }
            }
        }
        int[] output = new int[common.size()];
        for (int i = 0; i < common.size(); i++) {
            output[i] = common.get(i);
        }
        return output;
    }

}
