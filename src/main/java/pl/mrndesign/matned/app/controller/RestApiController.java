package pl.mrndesign.matned.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.mrndesign.matned.app.dto.CheckResultDto;
import pl.mrndesign.matned.app.dto.LottoCardDto;
import pl.mrndesign.matned.app.dto.LottoCardNumbersDto;
import pl.mrndesign.matned.app.dto.LottoCardSaveDto;
import pl.mrndesign.matned.app.model.LottoCard;
import pl.mrndesign.matned.app.model.LottoNumbers;
import pl.mrndesign.matned.app.logging.LogSanitizer;
import pl.mrndesign.matned.app.repository.LottoCardRepository;
import pl.mrndesign.matned.app.service.lotto.common.LottoService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@RestController
@Slf4j
public class RestApiController {

    private static final int LOTTO_NUMBERS_COUNT = 6;
    private static final int MIN_LOTTO_NUMBER = 1;
    private static final int MAX_LOTTO_NUMBER = 49;
    private static final int MAX_NUMBER_OF_DRAWINGS = 20;
    private static final int MAX_NUMBER_GROUPS = 10;

    private final LottoService lottoService;
    private final LottoCardRepository lottoCardRepository;

    public RestApiController(LottoService lottoService, LottoCardRepository lottoCardRepository) {
        this.lottoService = lottoService;
        this.lottoCardRepository = lottoCardRepository;
    }

    @PostMapping(path = "/check")
    public ResponseEntity<List<CheckResultDto>> checkDraw(@RequestBody LottoCardDto card) {
        log.info("Received /check request: {}", LogSanitizer.summarizeCard(card));
        validateCard(card);
        List<CheckResultDto> result = lottoService.checkDraw(card);
        log.info("Completed /check request: {}", LogSanitizer.summarizeResults(result));
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/check-static")
    public ResponseEntity<List<CheckResultDto>> staticCheckDraw(@RequestBody LottoCardDto card) {
        log.info("Received /check-static request: {}", LogSanitizer.summarizeCard(card));
        validateCard(card);
        List<CheckResultDto> result = lottoService.checkDrawStatic(card);
        log.info("Completed /check-static request: {}", LogSanitizer.summarizeResults(result));
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/cards")
    public ResponseEntity<LottoCardSaveDto> saveCard(@RequestBody LottoCardSaveDto card, Authentication authentication) {
        log.info("Received /cards save request: {}", LogSanitizer.summarizeSavedCard(card));
        validateCard(card);
        var ownerSubject = ownerSubject(authentication);
        log.info("Saving card for owner={}", LogSanitizer.maskSubject(ownerSubject));

        if (card.getId() != null) {
            return lottoCardRepository.findByIdAndOwnerSubject(card.getId(), ownerSubject)
                    .map(existingCard -> updateEntity(existingCard, card))
                    .map(lottoCardRepository::save)
                    .map(this::toDto)
                    .map(savedCard -> {
                        log.info("Updated card for owner={}: {}", LogSanitizer.maskSubject(ownerSubject),
                                LogSanitizer.summarizeSavedCard(savedCard));
                        return savedCard;
                    })
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> {
                        log.warn("Card update failed, card not found for owner={} and cardId={}",
                                LogSanitizer.maskSubject(ownerSubject), card.getId());
                        return ResponseEntity.notFound().build();
                    });
        }

        var savedCard = toDto(lottoCardRepository.save(toEntity(card, ownerSubject)));
        log.info("Created card for owner={}: {}", LogSanitizer.maskSubject(ownerSubject),
                LogSanitizer.summarizeSavedCard(savedCard));
        return ResponseEntity.ok(savedCard);
    }

    @GetMapping(path = "/cards")
    public ResponseEntity<List<LottoCardSaveDto>> getCards(Authentication authentication) {
        var ownerSubject = ownerSubject(authentication);
        var cards = lottoCardRepository.findAllByOwnerSubjectOrderByIdDesc(ownerSubject).stream()
                .map(this::toDto)
                .toList();
        log.info("Returned cards list for owner={}: count={}", LogSanitizer.maskSubject(ownerSubject), cards.size());
        return ResponseEntity.ok(cards);
    }

    @GetMapping(path = "/cards/latest")
    public ResponseEntity<LottoCardSaveDto> getLatestCard(Authentication authentication) {
        var ownerSubject = ownerSubject(authentication);
        return Optional.ofNullable(lottoCardRepository.findTopByOwnerSubjectOrderByIdDesc(ownerSubject))
                .map(this::toDto)
                .map(card -> {
                    log.info("Returned latest card for owner={}: {}", LogSanitizer.maskSubject(ownerSubject),
                            LogSanitizer.summarizeSavedCard(card));
                    return card;
                })
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.info("No latest card found for owner={}", LogSanitizer.maskSubject(ownerSubject));
                    return ResponseEntity.noContent().build();
                });
    }

    private LottoCard toEntity(LottoCardSaveDto card, String ownerSubject) {
        return LottoCard.builder()
                .ownerSubject(ownerSubject)
                .firstDrawDate(card.getFirstDrawDate())
                .numberOfDrawings(card.getNumberOfDrawings())
                .drawType(card.getDrawType())
                .numbers(toNumbers(card))
                .build();
    }

    private LottoCard updateEntity(LottoCard existingCard, LottoCardSaveDto card) {
        existingCard.setFirstDrawDate(card.getFirstDrawDate());
        existingCard.setNumberOfDrawings(card.getNumberOfDrawings());
        existingCard.setDrawType(card.getDrawType());
        existingCard.getNumbers().clear();
        existingCard.getNumbers().addAll(toNumbers(card));
        return existingCard;
    }

    private List<LottoNumbers> toNumbers(LottoCardSaveDto card) {
        return card.getNumbers().stream()
                .map(numbers -> new LottoNumbers(Arrays.stream(numbers.getNumbers()).boxed().toList()))
                .toList();
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

    private String ownerSubject(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Rejected request due to missing or unauthenticated principal.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return authentication.getName();
    }

    private void validateCard(LottoCardDto card) {
        if (card == null) {
            throwBadRequest("Card payload is required.");
        }
        validateCommon(card.getFirstDrawDate(), card.getNumberOfDraws(), card.getNumbers(), card.getDrawType());
    }

    private void validateCard(LottoCardSaveDto card) {
        if (card == null) {
            throwBadRequest("Card payload is required.");
        }
        validateCommon(card.getFirstDrawDate(), card.getNumberOfDrawings(), card.getNumbers(), card.getDrawType());
    }

    private void validateCommon(LocalDate firstDrawDate, int numberOfDrawings, List<LottoCardNumbersDto> numbers, Object drawType) {
        if (firstDrawDate == null) {
            throwBadRequest("First draw date is required.");
        }
        if (firstDrawDate.isAfter(LocalDate.now())) {
            throwBadRequest("First draw date cannot be in the future.");
        }
        if (numberOfDrawings < 1 || numberOfDrawings > MAX_NUMBER_OF_DRAWINGS) {
            throwBadRequest("Number of drawings must be between 1 and " + MAX_NUMBER_OF_DRAWINGS + ".");
        }
        if (drawType == null) {
            throwBadRequest("Draw type is required.");
        }
        if (numbers == null || numbers.isEmpty() || numbers.size() > MAX_NUMBER_GROUPS) {
            throwBadRequest("Card must contain between 1 and " + MAX_NUMBER_GROUPS + " number groups.");
        }
        numbers.forEach(this::validateNumbers);
    }

    private void validateNumbers(LottoCardNumbersDto numberGroup) {
        if (numberGroup == null || numberGroup.getNumbers() == null || numberGroup.getNumbers().length != LOTTO_NUMBERS_COUNT) {
            throwBadRequest("Each number group must contain exactly " + LOTTO_NUMBERS_COUNT + " numbers.");
        }
        var uniqueNumbers = Arrays.stream(numberGroup.getNumbers()).distinct().count();
        if (uniqueNumbers != LOTTO_NUMBERS_COUNT) {
            throwBadRequest("Numbers in a group must be unique.");
        }
        var invalidNumber = Arrays.stream(numberGroup.getNumbers())
                .anyMatch(number -> number < MIN_LOTTO_NUMBER || number > MAX_LOTTO_NUMBER);
        if (invalidNumber) {
            throwBadRequest("Numbers must be between " + MIN_LOTTO_NUMBER + " and " + MAX_LOTTO_NUMBER + ".");
        }
    }

    private void throwBadRequest(String message) {
        log.warn("Rejected request validation: {}", message);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

}
