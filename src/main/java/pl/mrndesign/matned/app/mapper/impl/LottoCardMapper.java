package pl.mrndesign.matned.app.mapper.impl;

import pl.mrndesign.matned.app.dto.LottoCardDto;
import pl.mrndesign.matned.app.dto.LottoCardNumbersDto;
import pl.mrndesign.matned.app.mapper.LottoMapper;
import pl.mrndesign.matned.app.model.LottoCard;
import pl.mrndesign.matned.app.model.LottoNumbers;

import java.util.Arrays;
import java.util.List;

public class LottoCardMapper implements LottoMapper<LottoCard, LottoCardDto> {


	@Override
	public LottoCard toEntity(LottoCardDto lottoCardDto) {
		return LottoCard.builder()
				.id(lottoCardDto.getId())
				.ownerSubject(lottoCardDto.getOwnerSubject())
				.drawType(lottoCardDto.getDrawType())
				.numbers(toNumbers(lottoCardDto))
				.firstDrawDate(lottoCardDto.getFirstDrawDate())
				.numberOfDraws(lottoCardDto.getNumberOfDraws())
				.build();
	}

	@Override
	public LottoCardDto toDto(LottoCard lottoCard) {
		return LottoCardDto.builder()
				.id(lottoCard.getId())
				.ownerSubject(lottoCard.getOwnerSubject())
				.drawType(lottoCard.getDrawType())
				.numbers(toNumbersDto(lottoCard))
				.numberOfDraws(lottoCard.getNumberOfDraws())
				.firstDrawDate(lottoCard.getFirstDrawDate())
				.build();
	}

	@Override
	public LottoCard updateEntity(LottoCard lottoCard, LottoCardDto lottoCardDto) {
		lottoCard.setFirstDrawDate(lottoCardDto.getFirstDrawDate());
		lottoCard.setNumberOfDraws(lottoCardDto.getNumberOfDraws());
		lottoCard.setDrawType(lottoCardDto.getDrawType());
		lottoCard.getNumbers().clear();
		lottoCard.getNumbers().addAll(toNumbers(lottoCardDto));
		return lottoCard;
	}

	@Override
	public List<LottoCard> toEntity(List<LottoCardDto> lottoCardDtos) {
		return lottoCardDtos.stream().map(this::toEntity).toList();
	}

	@Override
	public List<LottoCardDto> toDto(List<LottoCard> lottoCards) {
		return lottoCards.stream().map(this::toDto).toList();
	}

	private List<LottoNumbers> toNumbers(LottoCardDto dto) {
		if (dto.getNumbers() == null) {
			return null;
		}
		return dto.getNumbers().stream()
				.map(numbers -> new LottoNumbers(Arrays.stream(numbers.getNumbers()).boxed().toList()))
				.toList();
	}

	private List<LottoCardNumbersDto> toNumbersDto(LottoCard entity) {
		if (entity.getNumbers() == null) {
			return null;
		}
		return entity.getNumbers().stream()
				.map(n -> new LottoCardNumbersDto(n.getNumbers()
						.stream()
						.mapToInt(Integer::intValue)
						.toArray())
				).toList();
	}

}
