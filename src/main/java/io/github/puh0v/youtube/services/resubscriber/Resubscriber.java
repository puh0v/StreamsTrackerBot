package io.github.puh0v.youtube.services.resubscriber;

import io.github.puh0v.db.subscriptions.SubscriptionsRepository;
import io.github.puh0v.youtube.httpclient.YouTubeHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class Resubscriber {
    private final SubscriptionsRepository subscriptionsRepository;
    private final YouTubeHttpClient youTubeHttpClient;

    public Resubscriber(SubscriptionsRepository subscriptionsRepository, YouTubeHttpClient youTubeHttpClient) {
        this.subscriptionsRepository = subscriptionsRepository;
        this.youTubeHttpClient = youTubeHttpClient;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void resubscribe() {
        log.info("[Resubscriber] Началась плановая переподписка каналов на PubSubHubbub...");
        Set<String> channelIds = subscriptionsRepository.findAllDistinctChannelIds();

        if (channelIds.isEmpty()) {
            log.info("[Resubscriber] Каналы отсутствуют. Операция отменена.");
            return;
        }

        Map<String, CompletableFuture<Integer>> futuresByChannel = new HashMap<>();

        channelIds.forEach(channelId -> {
            CompletableFuture<Integer> result = youTubeHttpClient.setStreamNotification(channelId);
            futuresByChannel.put(channelId, result);
            });

        CompletableFuture.allOf(futuresByChannel.values().toArray(new CompletableFuture[0])).join();

        long success = 0, fail = 0;
        for (Map.Entry<String, CompletableFuture<Integer>> entry : futuresByChannel.entrySet()) {
            String channelId = entry.getKey();
            try {
                int statusCode = entry.getValue().get();
                if (statusCode < 200 || statusCode >= 300) {
                    log.info("[Resubscriber] Не удалось обновить подписку канала {}: {}",
                            channelId,
                            statusCode);
                    fail++;
                } else  {
                    log.info("[Resubscriber] Подписка канала {} обновлена успешно: {}",
                            channelId,
                            statusCode);
                    success++;
                }
            } catch (InterruptedException e) {
                log.info("[Resubscriber] Ошибка при подписке канала {}: InterruptedException", channelId);
                fail++;
            } catch (ExecutionException e) {
                log.info("[Resubscriber] Ошибка при подписке канала {}: ExecutionException", channelId);
                fail++;
            }
        }
        log.info("[Resubscriber] Плановая переподписка каналов на PubSubHubbub окончена.\n"
                + "Итог: success={}, fail={}",
                success,
                fail);
    }
}
