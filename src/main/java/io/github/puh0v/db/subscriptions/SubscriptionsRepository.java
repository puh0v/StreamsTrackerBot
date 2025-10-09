package io.github.puh0v.db.subscriptions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SubscriptionsRepository extends JpaRepository<SubscriptionsEntity, Long> {

    boolean existsByUser_TelegramIdAndChannel_ChannelId(Long telegramId, String channelId);

    @Query("select distinct s.channel.channelId from SubscriptionsEntity s")
    Set<String> findAllDistinctChannelIds();

    Set<SubscriptionsEntity> findAllByChannel_ChannelId(String channelId);

    List<SubscriptionsEntity> findAllByUser_TelegramId(Long userId);
}