package io.github.puh0v.youtube.httpclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.puh0v.config.AppServerProperties;
import io.github.puh0v.config.YouTubeProperties;
import io.github.puh0v.youtube.dto.channels.ChannelItemDto;
import io.github.puh0v.youtube.dto.channels.ChannelsListDto;
import io.github.puh0v.youtube.dto.videos.SnippetDto;
import io.github.puh0v.youtube.dto.videos.VideosListDto;
import io.github.puh0v.youtube.exceptions.ChannelNotFoundException;
import io.github.puh0v.youtube.exceptions.YouTubeApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;


/**
 * HTTP-клиент для взаимодействия с YouTube API и PubSubHubBub.
 *
 * @throws ChannelNotFoundException если канал не найден
 * @throws YouTubeApiException при ошибке YouTube API или парсинга
 */
@Slf4j
@Service
public class YouTubeHttpClient {
    private final YouTubeProperties youTubeProperties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final AppServerProperties appServerProperties;

    public YouTubeHttpClient(YouTubeProperties youTubeProperties, ObjectMapper objectMapper, AppServerProperties appServerProperties) {
        this.youTubeProperties = youTubeProperties;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
        this.appServerProperties = appServerProperties;
    }


    public CompletableFuture<ChannelItemDto> getChannelInfo(String handle) {
        log.info("[YouTubeHttpClient] Отправляю HTTP-запрос для получения информации о канале {}", handle);
        String url = String.format(
                "%s/channels?part=id,snippet&forHandle=%s&key=%s",
                youTubeProperties.apiBaseUrl(),
                handle,
                youTubeProperties.apiKey());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(json -> {
                    try {
                        ChannelsListDto dto = objectMapper.readValue(json, ChannelsListDto.class);
                        if (dto.items() == null || dto.items().isEmpty()) {
                            throw new ChannelNotFoundException();
                        }
                        log.info("[YouTubeHttpClient] HTTP-запрос прошёл успешно! id={}, title={}",
                                dto.items().get(0).id(),
                                dto.items().get(0).snippet().title());
                        return dto.items().get(0);
                    } catch (Exception e) {
                        throw new YouTubeApiException();
                    }
                });
    }


    public CompletableFuture<Integer> setStreamNotification(String channelId) {
        log.info("[YouTubeHttpClient] Отправляю HTTP-запрос для подписки на стримы канала {}", channelId);
        String body =
                "hub.mode=subscribe"
                + "&hub.verify=async"
                + "&hub.lease_seconds=864000"
                + "&hub.callback=" + getEncodedUrl(appServerProperties.url() + youTubeProperties.callbackPath())
                + "&hub.topic=" + getEncodedUrl("https://www.youtube.com/feeds/videos.xml?channel_id=" + channelId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://pubsubhubbub.appspot.com/subscribe"))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(resp ->  {
                    log.warn("[YouTubeHttpClient] Статус: code={} body={}", resp.statusCode(), resp.body());
                    return resp.statusCode();
                });
    }


    private String getEncodedUrl(String url) {
        return URLEncoder.encode(url, StandardCharsets.UTF_8);
    }


    public CompletableFuture<SnippetDto> getVideoInfo(String videoId) {
        log.info("[YouTubeHttpClient] Отправляю HTTP-запрос для получения информации о видео: {}", videoId);
        String url = String.format(
                "%s/videos?part=snippet&id=%s&key=%s",
                youTubeProperties.apiBaseUrl(),
                videoId,
                youTubeProperties.apiKey()
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(json -> {
                    try {
                        VideosListDto dto = objectMapper.readValue(json, VideosListDto.class);
                        SnippetDto snippetDto = dto.items().get(0).snippet();
                        log.info("[YouTubeHttpClient] HTTP-запрос прошёл успешно! id={}, title={}",
                                snippetDto.channelId(),
                                snippetDto.title());
                        return snippetDto;
                    } catch (IOException e) {
                        throw new YouTubeApiException();
                    }
                });
    }
}
