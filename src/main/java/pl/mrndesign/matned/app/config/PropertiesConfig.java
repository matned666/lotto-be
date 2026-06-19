package pl.mrndesign.matned.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:language/messages.properties")
public class PropertiesConfig {
}