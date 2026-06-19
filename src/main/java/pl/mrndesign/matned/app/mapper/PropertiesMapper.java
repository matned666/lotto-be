package pl.mrndesign.matned.app.mapper;

import pl.mrndesign.matned.app.dto.PropertiesDto;
import pl.mrndesign.matned.app.model.auth.User;
import pl.mrndesign.matned.app.model.config.Properties;

import java.util.List;

public interface PropertiesMapper {

	Properties toEntity(PropertiesDto propertiesDto, User user);

	PropertiesDto toDto(Properties properties);

	Properties updateEntity(Properties properties, PropertiesDto propertiesDto);

	List<Properties> toEntity(List<PropertiesDto> propertiesDtos, User user);

	List<PropertiesDto> toDto(List<Properties> properties);
}
