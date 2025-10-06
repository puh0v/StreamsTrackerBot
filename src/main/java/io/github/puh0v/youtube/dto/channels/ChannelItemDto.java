package io.github.puh0v.youtube.dto.channels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChannelItemDto(String id, ChannelSnippetDto snippet) {}