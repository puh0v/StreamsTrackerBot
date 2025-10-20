package io.github.puh0v.bot.services.commandsservice;

import io.github.puh0v.bot.commands.AbstractCommands;
import io.github.puh0v.bot.commands.CommandContext;
import io.github.puh0v.bot.services.flagmanager.FlagManager;
import io.github.puh0v.bot.services.messagesender.MessageSender;
import io.github.puh0v.bot.services.updatereceiver.UpdateReceiver;
import io.github.puh0v.bot.services.registration.UserRegistrationService;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CommandsService получает апдейт от UpdateReceiver, собирает всю необходимую информацию
 * и вызывает нужный класс, отвечающий за конкретную команду.
 */
@Slf4j
@Component
public class CommandsService {
    private final FlagManager flagManager;
    private final UserRegistrationService userRegistrationService;
    private final Map<String, AbstractCommands> mapOfCommands;
    private final MessageSender messageSender;

    public CommandsService(FlagManager flagManager, UserRegistrationService userRegistrationService, List<AbstractCommands> abstractCommands) {
        this.flagManager = flagManager;
        this.userRegistrationService = userRegistrationService;
        mapOfCommands = new HashMap<>();
        for (AbstractCommands command : abstractCommands) {
            mapOfCommands.put(command.getCommandName(), command);
        }
        messageSender = new MessageSender();
    }

    /**
     * handleUpdate получает апейдт от UpdateReceiver, собирает всю нуобходимую информацию
     * в CommandContext и передаёт его в приватные методы для дальнейшей обработки
     * сохранённых данных.
     */
    public void handleUpdate(UpdateReceiver updateReceiver, Update update) {
        log.info("[CommandsService] Начинается обрабока апдейта...");
        Long id = getId(update);
        String userMessage = getUserMessage(update);

        if (id == null || userMessage == null) {
            log.info("[CommandsService] Обработка апдейта отменена из-за отсутствия id пользователя или сообщения.");
            return;
        }
        userRegistrationService.saveUserToDatabase(id);

        CommandContext commandContext = new CommandContext.Builder()
                .setUpdateReceiver(updateReceiver)
                .setUpdate(update)
                .setId(id)
                .setUserMessage(userMessage)
                .build();

        if (update.hasMessage() && update.getMessage().hasText()) {
            log.info("[CommandsService] Поступило текстовое сообщение от пользователя {}...", id);
            executeCommand(commandContext);
        } else if (update.hasCallbackQuery()) {
            log.info("[CommandsService] Поступило Callback-запрос от пользователя {}, {}", id, userMessage);
            resetButtonLoading(commandContext);
            executeCommand(commandContext);
        }
    }

    private void executeCommand(CommandContext commandContext) {
        Long id = commandContext.getId();
        String userMessage = commandContext.getUserMessage();

        if (mapOfCommands.containsKey(userMessage)) {
            log.info("[CommandsService] Поступила команда от пользователя {}", id);
            flagManager.removeFlag(id);
            mapOfCommands.get(userMessage).handleCommand(commandContext);
        } else if (flagManager.hasFlag(id)) {
            log.info("[CommandsService] Пользователь прислал ответ по флагу... {}", id);
            flagManager.getFlag(id).handleTextByFlag(commandContext);
        }
    }

    @Nullable
    private Long getId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        } else {
            return null;
        }
    }

    @Nullable
    private String getUserMessage(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String userMessage = update.getMessage().getText();
            return userMessage;
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getData();
        } else {
            return null;
        }
    }

    private void resetButtonLoading(CommandContext commandContext) {
        String queryId = commandContext.getUpdate().getCallbackQuery().getId();
        AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                .callbackQueryId(queryId)
                .build();
        messageSender.sendAnswerCallbackQuery(commandContext, close);
    }
}
