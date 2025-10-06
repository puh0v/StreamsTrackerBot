package io.github.puh0v.youtube.dto.channels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChannelsListDto(List<ChannelItemDto> items) {}
