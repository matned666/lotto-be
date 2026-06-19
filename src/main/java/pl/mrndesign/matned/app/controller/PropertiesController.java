package pl.mrndesign.matned.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.mrndesign.matned.app.dto.PropertiesDto;
import pl.mrndesign.matned.app.service.auth.AuthService;
import pl.mrndesign.matned.app.service.common.ConfigPropertiesService;

import java.util.List;

@RestController
@RequestMapping("/api/props")
@Slf4j
public class PropertiesController {

	private final ConfigPropertiesService configPropertiesService;
	private final AuthService authService;


	public PropertiesController(ConfigPropertiesService configPropertiesService, AuthService authService) {
		this.configPropertiesService = configPropertiesService;
		this.authService = authService;
	}

	@GetMapping
	public ResponseEntity<List<PropertiesDto>> findPropertiesForUserId(Authentication authentication) {
		var user = authService.getByAuth(authentication);
		log.info("Loading properties for user {}.", user.getName());
		var props = configPropertiesService.findPropertiesForUserId(user.getId());
		log.info("Loaded properties for user {}: {}.", user.getName(), props);
		return ResponseEntity.ok(props);
	}

	@PostMapping
	public ResponseEntity<List<PropertiesDto>> setProperties(
			@RequestBody PropertiesDto property,
			Authentication authentication
	) {
		var user = authService.getByAuth(authentication);
		log.info("Setting properties for user {}: {}", user.getName(), property);
		configPropertiesService.setPropertiesForUserId(user.getId(), property);
		log.info("Properties set successfuly for user: {}.", user.getName());
		return ResponseEntity.ok(configPropertiesService.findPropertiesForUserId(user.getId()));
	}


}
