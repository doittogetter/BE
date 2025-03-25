package com.doittogether.platform.common.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DatabaseConfigLogger {

    @Value("${HOME_URL}")
    private String homeUrl;

    @Value("${HOME_USERNAME}")
    private String homeUsername;

    @PostConstruct
    public void logDatasourceInfo() {
        log.info("Database URL: {}", homeUrl);
        log.info("Database Username: {}", homeUsername);
    }
}
