package io.github.puh0v.config.youtubeproperties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "youtube")
public record YouTubeProperties(String apiKey, String apiBaseUrl, String mainPageUrl, String callbackPath, String videosBaseUrl) {}
