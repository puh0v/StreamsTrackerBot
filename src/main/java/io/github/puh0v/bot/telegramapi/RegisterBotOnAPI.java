package io.github.puh0v.bot.telegramapi;

import io.github.puh0v.bot.services.updatereceiver.UpdateReceiver;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Компонент для регистрации Telegram-бота в Telegram Bots API.
 */
@Slf4j
@Component
public class RegisterBotOnAPI {
    private final UpdateReceiver updateReceiver;

    public RegisterBotOnAPI(UpdateReceiver updateReceiver) {
        this.updateReceiver = updateReceiver;
    }

    @PostConstruct
    public void registerBotOnAPI() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(updateReceiver);
        } catch (TelegramApiException e) {
            log.error("[RegisterBotOnAPI] Произошла ошибка при инициализации бота: {}", e.getMessage());
        }
        log.info("[RegisterBotOnAPI] Инициализация бота прошла успешно!");
    }
}
