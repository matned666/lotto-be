package pl.mrndesign.matned.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.mrndesign.matned.app.dto.CheckResultDto;
import pl.mrndesign.matned.app.dto.LottoCardDto;
import pl.mrndesign.matned.app.dto.LottoCardNumbersDto;
import pl.mrndesign.matned.app.dto.LottoCardSaveDto;
import pl.mrndesign.matned.app.model.LottoCard;
import pl.mrndesign.matned.app.model.LottoNumbers;
import pl.mrndesign.matned.app.repository.LottoCardRepository;
import pl.mrndesign.matned.app.service.lotto.common.LottoService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
public class RestApiController {

    private final LottoService lottoService;
    private final LottoCardRepository lottoCardRepository;

    public RestApiController(LottoService lottoService, LottoCardRepository lottoCardRepository) {
        this.lottoService = lottoService;
        this.lottoCardRepository = lottoCardRepository;
    }

    @PostMapping(path = "/check")
    public ResponseEntity<List<CheckResultDto>> checkDraw(@RequestBody LottoCardDto card) {
        List<CheckResultDto> result = lottoService.checkDraw(card);
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/check-static")
    public ResponseEntity<List<CheckResultDto>> staticCheckDraw(@RequestBody LottoCardDto card) {
        List<CheckResultDto> result = lottoService.checkDrawStatic(card);
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/cards")
    public ResponseEntity<LottoCardSaveDto> saveCard(@RequestBody LottoCardSaveDto card) {
        if (card.getId() != null) {
            return lottoCardRepository.findById(card.getId())
                    .map(this::toDto)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        return ResponseEntity.ok(toDto(lottoCardRepository.save(toEntity(card))));
    }

    @GetMapping(path = "/cards")
    public ResponseEntity<List<LottoCardSaveDto>> getCards() {
        return ResponseEntity.ok(lottoCardRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).stream()
                .map(this::toDto)
                .toList());
    }

    @GetMapping(path = "/cards/latest")
    public ResponseEntity<LottoCardSaveDto> getLatestCard() {
        return Optional.ofNullable(lottoCardRepository.findTopByOrderByIdDesc())
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    private LottoCard toEntity(LottoCardSaveDto card) {
        return LottoCard.builder()
                .firstDrawDate(card.getFirstDrawDate())
                .numberOfDrawings(card.getNumberOfDrawings())
                .drawType(card.getDrawType())
                .numbers(card.getNumbers().stream()
                        .map(numbers -> new LottoNumbers(Arrays.stream(numbers.getNumbers()).boxed().toList()))
                .toList())
                .build();
    }

    private LottoCardSaveDto toDto(LottoCard card) {
        var dto = new LottoCardSaveDto();
        dto.setId(card.getId());
        dto.setFirstDrawDate(card.getFirstDrawDate());
        dto.setNumberOfDrawings(card.getNumberOfDrawings());
        dto.setDrawType(card.getDrawType());
        dto.setNumbers(card.getNumbers().stream()
                .map(numbers -> new LottoCardNumbersDto(numbers.getNumbers().stream().mapToInt(Integer::intValue).toArray()))
                .toList());
        return dto;
    }

}
