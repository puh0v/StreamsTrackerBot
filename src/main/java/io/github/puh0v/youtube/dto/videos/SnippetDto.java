package io.github.puh0v.youtube.dto.videos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SnippetDto(String liveBroadcastContent, String title, String channelTitle, String channelId) {}