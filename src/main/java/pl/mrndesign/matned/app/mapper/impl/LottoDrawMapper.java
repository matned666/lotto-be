package pl.mrndesign.matned.app.mapper.impl;

import pl.mrndesign.matned.app.dto.LottoDrawDto;
import pl.mrndesign.matned.app.mapper.LottoMapper;
import pl.mrndesign.matned.app.model.LottoDraw;
import pl.mrndesign.matned.app.model.LottoNumbers;

import java.util.Arrays;
import java.util.List;

public class LottoDrawMapper implements LottoMapper<LottoDraw, LottoDrawDto> {

	@Override
	public LottoDraw toEntity(LottoDrawDto dto) {
		return new LottoDraw(
				dto.getDate(),
				toNumbers(dto),
				dto.getDrawType()
		);
	}

	@Override
	public LottoDrawDto toDto(LottoDraw entity) {
		return new LottoDrawDto(
				entity.getDate(),
				toNumbersDto(entity),
				entity.getDrawType()
		);
	}

	@Override
	public LottoDraw updateEntity(LottoDraw lottoDraw, LottoDrawDto lottoDrawDto) {
		lottoDraw.setDate(lottoDrawDto.getDate());
		lottoDraw.setDrawType(lottoDrawDto.getDrawType());
		lottoDraw.setNumbers(toNumbers(lottoDrawDto));
		return lottoDraw;
	}

	@Override
	public List<LottoDraw> toEntity(List<LottoDrawDto> lottoDrawDtos) {
		return lottoDrawDtos.stream().map(this::toEntity).toList();
	}

	@Override
	public List<LottoDrawDto> toDto(List<LottoDraw> lottoDraws) {
		return lottoDraws.stream().map(this::toDto).toList();
	}

	private LottoNumbers toNumbers(LottoDrawDto dto) {
		if (dto.getNumbers() == null) {
			return null;
		}
		return new LottoNumbers(
				Arrays.stream(dto.getNumbers())
						.boxed()
						.toList()
		);
	}

	private int[] toNumbersDto(LottoDraw entity) {
		if (entity.getNumbers() == null) {
			return new int[0];
		}
		return entity.getNumbers().getNumbers()
				.stream()
				.mapToInt(Integer::intValue)
				.toArray();
	}


}
