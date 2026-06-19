package pl.mrndesign.matned.app.service.common.impl;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.mrndesign.matned.app.service.common.MessageService;

@Service
public class MessageServiceImpl implements MessageService {

	private final Environment environment;

	public MessageServiceImpl(Environment environment) {
		this.environment = environment;
	}

	@Override
	public String getMessage(String key) {
		return environment.getProperty(key);
	}

}
