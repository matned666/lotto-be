package pl.mrndesign.matned.app.mapper.impl;

import org.springframework.stereotype.Component;
import pl.mrndesign.matned.app.dto.PropertiesDto;
import pl.mrndesign.matned.app.mapper.PropertiesMapper;
import pl.mrndesign.matned.app.model.auth.User;
import pl.mrndesign.matned.app.model.config.Properties;
import pl.mrndesign.matned.app.service.common.MessageService;

import java.util.List;

@Component
public class PropertiesMapperImpl implements PropertiesMapper {

	private final MessageService messageService;

	public PropertiesMapperImpl(MessageService messageService) {
		this.messageService = messageService;
	}

	@Override
	public Properties toEntity(PropertiesDto propertiesDto, User user) {
		return Properties.builder()
				.id(propertiesDto.getId())
				.user(user)
				.name(propertiesDto.getName())
				.type(propertiesDto.getType())
				.value(propertiesDto.getValue())
				.enabled(propertiesDto.getEnabled())
				.build();
	}

	@Override
	public PropertiesDto toDto(Properties properties) {
		return PropertiesDto.builder()
				.id(properties.getId())
				.name(properties.getName())
				.userId(properties.getUser().getId())
				.type(properties.getType())
				.value(properties.getValue())
				.enabled(properties.getEnabled())
				.label(messageService.getMessage(properties.getName()))
				.build();
	}

	@Override
	public Properties updateEntity(Properties properties, PropertiesDto propertiesDto) {
		properties.setId(propertiesDto.getId());
		properties.setName(propertiesDto.getName());
		properties.setType(propertiesDto.getType());
		properties.setValue(propertiesDto.getValue());
		properties.setEnabled(propertiesDto.getEnabled());
		return properties;
	}

	@Override
	public List<Properties> toEntity(List<PropertiesDto> propertiesDtos, User user) {
		return propertiesDtos.stream()
				.map(dto -> toEntity(dto, user))
				.toList();
	}

	@Override
	public List<PropertiesDto> toDto(List<Properties> properties) {
		return properties.stream()
				.map(this::toDto)
				.toList();
	}
}
