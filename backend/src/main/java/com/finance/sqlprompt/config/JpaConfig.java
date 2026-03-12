package com.finance.sqlprompt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Central JPA configuration: auditing (created_at/updated_at) and repository scanning.
 * Transaction management is enabled by default in Spring Boot but made explicit here.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.finance.sqlprompt.repository")
public class JpaConfig {
}
