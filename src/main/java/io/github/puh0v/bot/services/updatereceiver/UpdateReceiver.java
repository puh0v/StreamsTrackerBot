package io.github.puh0v.bot.services.updatereceiver;

import io.github.puh0v.config.BotProperties;
import io.github.puh0v.bot.services.commandsservice.CommandsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;


/**
 * Сервис для получения обновлений от Telegram
 */
@Slf4j
@Service
public class UpdateReceiver extends TelegramLongPollingBot {
    private final BotProperties botProperties;
    private final CommandsService commandsService;

    public UpdateReceiver(BotProperties botProperties, CommandsService commandsService) {
        this.botProperties = botProperties;
        this.commandsService = commandsService;
    }

    @Override
    public String getBotUsername() {
        return botProperties.name();
    }

    @Override
    public String getBotToken() {
        return botProperties.token();
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("[UpdateReceiver] Получен апдейт...");
        commandsService.handleUpdate(this, update);
    }
}
