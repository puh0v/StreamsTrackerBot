package io.github.puh0v.youtube.model;

import io.github.puh0v.youtube.enums.BroadcastStatus;
import lombok.Builder;
import lombok.Getter;


/**
 * Модель данных, описывающих информации о видео YouTube канала.
 */
@Builder
@Getter
public class VideoInfo {
    private String videoTitle;
    private String channelTitle;
    private String channelId;
    private String videoUrl;
    private BroadcastStatus status;
}