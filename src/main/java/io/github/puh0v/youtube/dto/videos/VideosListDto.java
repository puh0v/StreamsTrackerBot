package io.github.puh0v.youtube.dto.videos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VideosListDto(List<ItemsDto> items) {}
