package io.github.puh0v.bot.services.notifications;

import io.github.puh0v.bot.services.messagesender.MessageSender;
import io.github.puh0v.bot.services.updatereceiver.UpdateReceiver;
import io.github.puh0v.db.subscriptions.SubscriptionsEntity;
import io.github.puh0v.db.subscriptions.SubscriptionsRepository;
import io.github.puh0v.youtube.model.VideoInfo;
import io.github.puh0v.youtube.enums.BroadcastStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Сервис для подготовки списка пользователей и сообщения с уведомлением о запланированном
 * или начавшимся стриме
 */
@Slf4j
@Component
public class NotificationsSender {
    private final MessageSender messageSender;
    private final SubscriptionsRepository subscriptionsRepository;
    private final UpdateReceiver updateReceiver;

    public NotificationsSender(SubscriptionsRepository subscriptionsRepository,
                               @Lazy UpdateReceiver updateReceiver) {
        this.messageSender = new MessageSender();
        this.subscriptionsRepository = subscriptionsRepository;
        this.updateReceiver = updateReceiver;
    }

    public void notifySubscribers(VideoInfo videoInfo) {
        if (videoInfo.getStatus() == BroadcastStatus.UPCOMING) {
            log.info("[NotificationsSender] Рассылаю уведомление о запланированной трансляции: {}", videoInfo);
            sendMessages(videoInfo, getTextForUpcomingStream(videoInfo));
        } else if (videoInfo.getStatus() == BroadcastStatus.LIVE) {
            log.info("[NotificationsSender] Рассылаю уведомление о начавшейся трансляции: {}", videoInfo);
            sendMessages(videoInfo, getTextForLiveStream(videoInfo));
        }
    }

    private String getTextForUpcomingStream(VideoInfo videoInfo) {
        return String.format(
                """
                ⏰ Пользователь %s запланировал стрим ⏰
                
                👉🏻 %s
                %s
                """,
                videoInfo.getChannelTitle(),
                videoInfo.getVideoTitle(),
                videoInfo.getVideoUrl()
        );
    }

    private String getTextForLiveStream(VideoInfo videoInfo) {
        return String.format(
                """
                💥 Пользователь %s начал стрим! 💥
                
                👉🏻 %s
                %s
                """,
                videoInfo.getChannelTitle(),
                videoInfo.getVideoTitle(),
                videoInfo.getVideoUrl()
        );
    }

    private void sendMessages(VideoInfo videoInfo, String messageText) {
        Set<SubscriptionsEntity> telegramUsers = subscriptionsRepository.findAllByChannel_ChannelId(videoInfo.getChannelId());
        for (SubscriptionsEntity telegramUser : telegramUsers) {
            Long userId = telegramUser.getUser().getTelegramId();
            messageSender.sendNotificationMessage(updateReceiver, userId, messageText);
        }
    }
}
