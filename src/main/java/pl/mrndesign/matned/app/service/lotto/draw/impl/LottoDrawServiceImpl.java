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
import java.util.List;

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

}
