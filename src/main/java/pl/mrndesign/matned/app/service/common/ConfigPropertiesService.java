package pl.mrndesign.matned.app.service.common;

import pl.mrndesign.matned.app.dto.PropertiesDto;

import java.util.List;

public interface ConfigPropertiesService {

	List<PropertiesDto> findPropertiesForUserId(Long userId);

	void setPropertiesForUserId(Long userId, PropertiesDto propertiesDto);

}
