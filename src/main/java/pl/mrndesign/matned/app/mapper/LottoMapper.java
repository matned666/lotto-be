package pl.mrndesign.matned.app.mapper;

import java.util.List;

public interface LottoMapper <ENTITY, DTO> {

	ENTITY toEntity(DTO dto);

	DTO toDto(ENTITY entity);

	ENTITY updateEntity(ENTITY entity, DTO dto);

	List<ENTITY> toEntity(List<DTO> dtoList);

	List<DTO> toDto(List<ENTITY> entityList);

}
