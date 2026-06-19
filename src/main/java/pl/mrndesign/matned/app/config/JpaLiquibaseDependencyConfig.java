package pl.mrndesign.matned.app.config;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaLiquibaseDependencyConfig {

	@Bean
	static BeanFactoryPostProcessor entityManagerFactoryDependsOnLiquibase() {
		return beanFactory -> {
			if (beanFactory.containsBeanDefinition("entityManagerFactory")
					&& beanFactory.containsBeanDefinition("liquibase")) {
				beanFactory.getBeanDefinition("entityManagerFactory")
						.setDependsOn("liquibase");
			}
		};
	}
}
