package io.github.puh0v.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.server")
public record AppServerProperties(String url) {}
