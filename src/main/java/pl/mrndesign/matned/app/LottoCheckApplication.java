package pl.mrndesign.matned.app;

import javax.sql.DataSource;

import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class LottoCheckApplication {

    private static final Logger log = LoggerFactory.getLogger(LottoCheckApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LottoCheckApplication.class, args);
    }

    @Bean
    ApplicationRunner liquibaseDiagnostics(Environment environment) {
        return args -> {
            boolean liquibasePresent;
            try {
                Class.forName("liquibase.integration.spring.SpringLiquibase");
                liquibasePresent = true;
            } catch (ClassNotFoundException ex) {
                liquibasePresent = false;
            }

            log.info("Liquibase diagnostics: present={}, enabled={}, changeLog={}, historyTable={}, lockTable={}",
                    liquibasePresent,
                    environment.getProperty("spring.liquibase.enabled"),
                    environment.getProperty("spring.liquibase.change-log"),
                    environment.getProperty("spring.liquibase.database-change-log-table"),
                    environment.getProperty("spring.liquibase.database-change-log-lock-table"));
        };
    }

	@Bean(name = "liquibase")
	@ConditionalOnProperty(prefix = "spring.liquibase", name = "enabled", havingValue = "true", matchIfMissing = true)
	SpringLiquibase springLiquibase(DataSource dataSource, Environment environment) {
		var liquibase = new SpringLiquibase();
		liquibase.setDataSource(dataSource);
		liquibase.setChangeLog(environment.getProperty("spring.liquibase.change-log", "classpath:db/changelog.xml"));
		liquibase.setDatabaseChangeLogTable(environment.getProperty("spring.liquibase.database-change-log-table", "DATABASECHANGELOG"));
		liquibase.setDatabaseChangeLogLockTable(environment.getProperty("spring.liquibase.database-change-log-lock-table", "DATABASECHANGELOGLOCK"));
		liquibase.setShouldRun(Boolean.parseBoolean(environment.getProperty("spring.liquibase.enabled", "true")));
		return liquibase;
	}
}

