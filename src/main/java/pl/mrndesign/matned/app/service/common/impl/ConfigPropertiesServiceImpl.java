package pl.mrndesign.matned.app.service.common.impl;

import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.mrndesign.matned.app.dto.PropertiesDto;
import pl.mrndesign.matned.app.mapper.PropertiesMapper;
import pl.mrndesign.matned.app.model.config.Properties;
import pl.mrndesign.matned.app.model.config.PropertyType;
import pl.mrndesign.matned.app.repository.PropertiesRepository;
import pl.mrndesign.matned.app.repository.UserRepository;
import pl.mrndesign.matned.app.service.common.ConfigPropertiesService;

import java.util.List;

@Service
public class ConfigPropertiesServiceImpl implements ConfigPropertiesService {

	private final PropertiesRepository propertiesRepository;
	private final PropertiesMapper propertiesMapper;
	private final UserRepository userRepository;

	public ConfigPropertiesServiceImpl(PropertiesRepository propertiesRepository,
	                                   PropertiesMapper propertiesMapper,
	                                   UserRepository userRepository) {
		this.propertiesRepository = propertiesRepository;
		this.propertiesMapper = propertiesMapper;
		this.userRepository = userRepository;
	}

	@Override
	public List<PropertiesDto> findPropertiesForUserId(Long userId) {
		var entities = propertiesRepository.findPropertiesForUserId(userId);
		return propertiesMapper.toDto(entities);
	}

	@Transactional
	@Override
	public void setPropertiesForUserId(Long userId, PropertiesDto prop) {
		var foundProp = propertiesRepository.findPropertiesForNameAndUserId(userId,  prop.getName()).stream().findFirst().orElse(null);
		if (foundProp != null) {
			var propertiesToSave = propertiesMapper.updateEntity(foundProp, prop);
			propertiesRepository.save(propertiesToSave);
			if (prop.getValue() != null && !prop.getValue().isEmpty() && prop.getType() == PropertyType.EMAIL) {
				var user = foundProp.getUser();
				user.setEmail(prop.getValue());
				userRepository.save(user);
			}
		} else {
			var user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found."));
			var propertyToSave = Properties.builder()
					.name(prop.getName())
					.value(prop.getValue())
					.type(prop.getType())
					.user(user)
					.enabled(prop.getEnabled())
					.build();
			propertiesMapper.toDto(propertiesRepository.save(propertyToSave));
		}
	}

}
