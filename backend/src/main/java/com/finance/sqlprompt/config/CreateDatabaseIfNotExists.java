package com.finance.sqlprompt.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Before Flyway runs, ensure the target database exists so the connection does not fail.
 * Runs when the Flyway initializer bean is about to be created.
 */
@Component
public class CreateDatabaseIfNotExists implements BeanPostProcessor {

    private static final Pattern DB_NAME = Pattern.compile("jdbc:mysql://[^/]+/([^?]+)(\\?.*)?");

    private final Environment env;
    private boolean created;

    public CreateDatabaseIfNotExists(Environment env) {
        this.env = env;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if ("flywayInitializer".equals(beanName) && !created) {
            ensureDatabaseExists();
            created = true;
        }
        return bean;
    }

    private void ensureDatabaseExists() {
        String url = env.getProperty("spring.datasource.url");
        String username = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");
        if (url == null || username == null) return;

        Matcher m = DB_NAME.matcher(url);
        if (!m.matches()) return;
        String database = m.group(1);
        String suffix = m.group(2) != null ? m.group(2) : "";
        String serverUrl = url.replace("/" + database + suffix, "/" + suffix);

        try {
            try (var conn = DriverManager.getConnection(serverUrl, username, password);
                 var stmt = conn.createStatement()) {
                stmt.execute("CREATE DATABASE IF NOT EXISTS `" + database.replace("`", "``") + "`");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not create database '" + database + "'. Create it manually or check credentials.", e);
        }
    }
}
