package io.github.puh0v.db.subscriptions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

@Repository
public interface SubscriptionsRepository extends JpaRepository<SubscriptionsEntity, Long> {

    boolean existsByUser_TelegramIdAndChannel_ChannelId(Long telegramId, String channelId);

    List<SubscriptionsEntity> findAllByChannel_ChannelId(String channelId);

    List<SubscriptionsEntity> findAllByUser_TelegramId(Long userId);
}