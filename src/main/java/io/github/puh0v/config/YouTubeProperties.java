package io.github.puh0v.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "youtube")
public record YouTubeProperties(String apiKey, String apiBaseUrl, String mainPageUrl, String callbackPath, String videosBaseUrl) {}
