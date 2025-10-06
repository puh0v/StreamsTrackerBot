package io.github.puh0v.youtube.services.notification;

import io.github.puh0v.bot.services.notifications.NotificationsSender;
import io.github.puh0v.config.YouTubeProperties;
import io.github.puh0v.db.channels.ChannelsEntity;
import io.github.puh0v.db.channels.ChannelsRepository;
import io.github.puh0v.db.enums.Platform;
import io.github.puh0v.db.subscriptions.SubscriptionsEntity;
import io.github.puh0v.db.subscriptions.SubscriptionsRepository;
import io.github.puh0v.db.telegramusers.TelegramUsersEntity;
import io.github.puh0v.db.telegramusers.TelegramUsersRepository;
import io.github.puh0v.youtube.model.VideoInfo;
import io.github.puh0v.youtube.dto.channels.ChannelItemDto;
import io.github.puh0v.youtube.enums.BroadcastStatus;
import io.github.puh0v.youtube.exceptions.DuplicateRecordException;
import io.github.puh0v.youtube.exceptions.YouTubeApiException;
import io.github.puh0v.youtube.httpclient.YouTubeHttpClient;
import io.github.puh0v.youtube.util.parser.YouTubeParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;


/**
 * Сервис для подписки на YouTube-каналы и для сохранения информации о подписке в БД
 */
@Slf4j
@Service
public class YouTubeNotificationsService {
    private final YouTubeHttpClient youTubeHttpClient;
    private final NotificationsSender notificationsSender;
    private final YouTubeProperties youTubeProperties;
    private final TelegramUsersRepository telegramUsersRepository;
    private final ChannelsRepository channelsRepository;
    private final SubscriptionsRepository subscriptionsRepository;

    public YouTubeNotificationsService(YouTubeHttpClient youTubeHttpClient, NotificationsSender notificationsSender,
                                       YouTubeProperties youTubeProperties, TelegramUsersRepository telegramUsersRepository,
                                       ChannelsRepository channelsRepository, SubscriptionsRepository subscriptionsRepository) {
        this.youTubeHttpClient = youTubeHttpClient;
        this.notificationsSender = notificationsSender;
        this.youTubeProperties = youTubeProperties;
        this.telegramUsersRepository = telegramUsersRepository;
        this.channelsRepository = channelsRepository;
        this.subscriptionsRepository = subscriptionsRepository;
    }


    public void setStreamNotification(String link, Long telegramId) throws URISyntaxException {
        log.info("[YouTubeNotificationsService] Подписываю пользователя {} на уведомления от канала {}",
                telegramId,
                link);
        String channelHandle = YouTubeParser.parseChannelIdentifier(link);
        ChannelItemDto channelInfo = youTubeHttpClient.getChannelInfo(channelHandle).join();
        CompletableFuture<Integer> processStatus = youTubeHttpClient.setStreamNotification(channelInfo.id());
        int code = processStatus.join();
        if (code < 200 || code >= 300) {
            throw new YouTubeApiException();
        }
        saveChannelToDatabase(channelHandle, channelInfo.snippet().title(), channelInfo.id(), Platform.YOUTUBE.name());
        saveNotificationToDatabase(telegramId, channelInfo.id());
    }


    public void prepareNotificationInfo(String videoId) {
        log.info("[YouTubeNotificationsService] Собираю информацию о видео {} для дальнейшей рассылки пользователям телеграм",
                videoId);
        String videoUrl = youTubeProperties.videosBaseUrl() + videoId;

        youTubeHttpClient.getVideoInfo(videoId)
                .thenApply(dto -> VideoInfo.builder()
                                    .videoTitle(dto.title())
                                    .channelTitle(dto.channelTitle())
                                    .channelId(dto.channelId())
                                    .videoUrl(videoUrl)
                                    .status(getBroadcastStatus(dto.liveBroadcastContent()))
                                    .build())
                .thenAccept(videoInfo -> {
                    if (videoInfo.getStatus() == BroadcastStatus.UPCOMING || videoInfo.getStatus() == BroadcastStatus.LIVE) {
                        notificationsSender.notifySubscribers(videoInfo);
                    }
                });
    }


    private void saveChannelToDatabase(String channelHandle, String title, String channelId, String platform) {
        if (channelsRepository.existsByChannelId(channelId)) {
            return;
        }
        ChannelsEntity channel = new ChannelsEntity();
        channel.setChannelHandle(channelHandle);
        channel.setTitle(title);
        channel.setChannelId(channelId);
        channel.setPlatform(platform);
        channelsRepository.save(channel);
        log.info("[YouTubeNotificationsService] Канал {} был успешно сохранён в БД!", channelId);
    }


    private void saveNotificationToDatabase(Long telegramId, String channelId) {
        SubscriptionsEntity subscriptions = new SubscriptionsEntity();
        if (subscriptionsRepository.existsByUser_TelegramIdAndChannel_ChannelId(telegramId, channelId)) {
            throw new DuplicateRecordException();
        } else {
            TelegramUsersEntity telegramUser = telegramUsersRepository.findByTelegramId(telegramId);
            ChannelsEntity channel = channelsRepository.findByChannelId(channelId);
            subscriptions.setUser(telegramUser);
            subscriptions.setChannel(channel);
            subscriptionsRepository.save(subscriptions);
            log.info("[YouTubeNotificationsService] Пользователь {} и канал {} были успешно добавлены в БД с подписками",
                    telegramId,
                    channelId);
        }
    }


    private BroadcastStatus getBroadcastStatus(String status) {
        return BroadcastStatus.getBroadcastStatus(status);
    }
}
