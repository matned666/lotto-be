package pl.mrndesign.matned.app.service.lotto.draw.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.mrndesign.matned.app.dto.LottoDrawDto;
import pl.mrndesign.matned.app.mapper.LottoMapper;
import pl.mrndesign.matned.app.mapper.impl.LottoDrawMapper;
import pl.mrndesign.matned.app.model.DrawType;
import pl.mrndesign.matned.app.model.LottoDraw;
import pl.mrndesign.matned.app.repository.LottoDrawRepository;
import pl.mrndesign.matned.app.service.lotto.draw.LottoDrawService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LottoDrawServiceImpl implements LottoDrawService {

	private final LottoDrawRepository lottoDrawRepository;
	private final LottoMapper<LottoDraw, LottoDrawDto> lottoDrawMapper;

	public LottoDrawServiceImpl(LottoDrawRepository lottoDrawRepository) {
		this.lottoDrawRepository = lottoDrawRepository;
		this.lottoDrawMapper = new LottoDrawMapper();
	}

	@Override
	public List<LottoDrawDto> findDrawsFor(LocalDate date, String drawTypeStr) {
		var draws = lottoDrawRepository.findAllLottoDrawsByDateAndDrawType(date, DrawType.get(drawTypeStr));
		return lottoDrawMapper.toDto(draws);
	}

	@Override
	public List<LottoDrawDto> save(List<LottoDrawDto> lottoDraws) {
		var draws = lottoDraws.stream()
				.map(dto -> lottoDrawRepository.save(lottoDrawMapper.toEntity(dto)))
				.toList();
		return lottoDrawMapper.toDto(draws);
	}

	@Override
	public List<Integer> findTop10MostFrequentNumbers() {
		Map<Integer, Long> frequencyByNumber = lottoDrawRepository.findAllWithNumbers().stream()
				.map(LottoDraw::getNumbers)
				.filter(Objects::nonNull)
				.map(numbers -> numbers.getNumbers())
				.filter(Objects::nonNull)
				.flatMap(List::stream)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		return frequencyByNumber.entrySet().stream()
				.sorted(Map.Entry.<Integer, Long>comparingByValue(Comparator.reverseOrder())
						.thenComparing(Map.Entry.comparingByKey()))
				.limit(10)
				.map(Map.Entry::getKey)
				.toList();
	}

}
