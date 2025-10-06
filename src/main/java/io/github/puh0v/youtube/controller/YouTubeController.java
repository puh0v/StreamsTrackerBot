package io.github.puh0v.youtube.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.github.puh0v.youtube.dto.notifications.EntryDto;
import io.github.puh0v.youtube.dto.notifications.FeedDto;
import io.github.puh0v.youtube.services.notification.YouTubeNotificationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Входная точка в приложение. Обрабатывает запросы, которые приходят
 * (от пользователя, от YouTube, и т.д.). Работает с аннотациями типа @GetMapping, @PostMapping.*/
@Slf4j
@RestController
public class YouTubeController {
    private final YouTubeNotificationsService youTubeNotificationsService;

    public YouTubeController(YouTubeNotificationsService youTubeNotificationsService) {
        this.youTubeNotificationsService = youTubeNotificationsService;
    }

    @GetMapping("${youtube.callback-path}")
    public ResponseEntity<String> check(@RequestParam("hub.challenge") String challenge) {
        log.info("[YouTubeController] Пришёл GET-запрос от YouTube");
        return ResponseEntity.ok(challenge);
    }

    @PostMapping("${youtube.callback-path}")
    public ResponseEntity<String> getStreamNotification(@RequestBody String body) {
        log.info("[YouTubeController] Пришёл POST-запрос от YouTube. Начинается парсинг XML...");
        try {
            XmlMapper xmlMapper = new XmlMapper();
            FeedDto FeedDto = xmlMapper.readValue(body, FeedDto.class);
            if (FeedDto.entry() == null || FeedDto.entry().isEmpty()) {
                log.warn("[YouTubeController] Во входящем feed нет entry");
                return ResponseEntity.noContent().build();
            }
            FeedDto.entry().forEach(e -> {
                log.info("[YouTubeController] XML успешно распарсен! videoId={}", e.videoId());
                youTubeNotificationsService.prepareNotificationInfo(e.videoId());
            });
            return ResponseEntity.noContent().build();
        } catch (JsonProcessingException e) {
            log.error("[YouTubeController] Произошла ошибка во время парсинга XML: {}", e.getMessage());
            return ResponseEntity.ok().build();
        }
    }
}
