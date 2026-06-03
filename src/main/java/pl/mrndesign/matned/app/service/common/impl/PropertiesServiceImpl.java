package pl.mrndesign.matned.app.service.common.impl;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.mrndesign.matned.app.service.common.PropertiesService;

@Service
public class PropertiesServiceImpl implements PropertiesService {

    private final Environment environment;

    public PropertiesServiceImpl(Environment environment) {
        this.environment = environment;
    }

    public String getProperty(String path) {
        return environment.getProperty(path);
    }

    public String getProperty(String path, String defaultValue) {
        return environment.getProperty(path, defaultValue);
    }

    public <T> T getProperty(String path, Class<T> targetType) {
        return environment.getProperty(path, targetType);
    }
}