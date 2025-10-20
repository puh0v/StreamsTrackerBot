package io.github.puh0v.bot.commands.sendlink;

import io.github.puh0v.bot.commands.AbstractCommands;
import io.github.puh0v.bot.commands.CommandContext;
import io.github.puh0v.bot.commands.CommandNames;
import io.github.puh0v.bot.services.flagmanager.FlagManager;
import io.github.puh0v.bot.services.messagesender.MessageSender;
import io.github.puh0v.bot.buttons.KeyboardFactory;
import io.github.puh0v.bot.buttons.ListOfButtons;
import io.github.puh0v.bot.services.messagesender.util.MessageSpec;
import io.github.puh0v.youtube.exceptions.ChannelNotFoundException;
import io.github.puh0v.youtube.exceptions.DuplicateRecordException;
import io.github.puh0v.youtube.exceptions.YouTubeApiException;
import io.github.puh0v.youtube.services.notification.YouTubeNotificationsService;
import io.github.puh0v.youtube.util.validation.YouTubeLinkValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.net.URISyntaxException;
import java.util.concurrent.CompletionException;

/**
 * Команда для отправки ссылки на YouTube канал, чьи стримы пользователь хочет отслеживать
 */
@Slf4j
@Component
public class SendLinkCommand extends AbstractCommands {
    private final MessageSender messageSender;
    private final FlagManager flagManager;
    private final YouTubeNotificationsService youTubeNotificationsService;
    public final ListOfButtons listOfButtons;

    public SendLinkCommand(FlagManager flagManager, YouTubeNotificationsService youTubeNotificationsService,
                           ListOfButtons listOfButtons) {
        super(CommandNames.SEND_LINK.getCode());
        this.messageSender = new MessageSender();
        this.flagManager = flagManager;
        this.youTubeNotificationsService = youTubeNotificationsService;
        this.listOfButtons = listOfButtons;
    }


    @Override
    public void handleCommand(CommandContext commandContext) {
        log.info("[SendLinkCommand] Пользователь {} прислал команду \"{}\"", commandContext.getId(), getCommandName());

        sendMessage(commandContext, getTextAnswer(), getCancelButton());
        flagManager.setFlag(commandContext.getId(), this);
    }


    @Override
    public void handleTextByFlag(CommandContext commandContext) {
        log.info("[SendLinkCommand] Пользователь {} ответил на сообщение бота по флагу \"{}\"",
                commandContext.getId(),
                CommandNames.SEND_LINK.getCode()
        );

        String userMessage = commandContext.getUserMessage();

        if (userMessage.equals(CommandNames.CANCEL.getCode())) {
            log.info("[SendLinkCommand] Пользователь {} передумал подписываться на канал", commandContext.getId());

            sendMessage(commandContext, "\uD83D\uDE11 Отправка ссылки отменена", getMainMenuButton());
            flagManager.removeFlag(commandContext.getId());

        } else if (YouTubeLinkValidation.isYouTubeLink(userMessage)) {
            log.info("[SendLinkCommand] Пользователь {} прислал YouTube ссылку...", commandContext.getId());

            try {
                youTubeNotificationsService.setStreamNotification(userMessage, commandContext.getId());
                log.info("[SendLinkCommand] Пользователь {} успешно подписался на уведомления YouTube канала", commandContext.getId());

                sendMessage(commandContext, "✅ Вы успешно подписались на уведомления о начале стримов!", getMainMenuButton());
                flagManager.removeFlag(commandContext.getId());

            } catch (DuplicateRecordException e) {
                log.info("[SendLinkCommand] Пользователь {} уже подписан на данный канал", commandContext.getId(), e);
                sendMessage(commandContext, "\uD83D\uDE42 Вы уже отслеживаете данный канал.", getMainMenuButton());

            } catch (URISyntaxException e) {
                log.error("[SendLinkCommand] Пользователь {} ввёл некорректную ссылку на канал", commandContext.getId(), e);
                sendMessage(commandContext, "\uD83D\uDE42\u200D↔\uFE0F Вы ввели некорректную ссылку. Повторите попытку.", getCancelButton());

            } catch (ChannelNotFoundException e) {
                log.error("[SendLinkCommand] Пользователь {} ввёл некорректную ссылку на канал", commandContext.getId(), e);
                sendMessage(commandContext, "\uD83D\uDE15 Такого канала не существует. Повторите попытку.", getCancelButton());

            } catch (YouTubeApiException e) {
                log.error("[SendLinkCommand] У пользователя {} возникла ошибка при взаимодействии с API", commandContext.getId(), e);
                sendMessage(commandContext, "\uD83D\uDE15 Возникла ошибка при взаимодействии с API. Повторите попытку.", getCancelButton());

            } catch (CompletionException e) {
                log.error("[SendLinkCommand] У пользователя {} возникла ошибка при работе с CompletableFuture (асинхронная задача)", commandContext.getId(), e);
                sendMessage(commandContext, "\uD83D\uDE15 Произошла ошибка. Повторите попытку.", getCancelButton());

            } catch (RuntimeException e) {
                log.error("[SendLinkCommand] У пользователя {} возникла ошибка в рантайме", commandContext.getId(), e);
                sendMessage(commandContext, "\uD83D\uDE15 Произошла ошибка. Повторите попытку.", getCancelButton());
            }
        }
    }


    private void sendMessage(CommandContext commandContext, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        MessageSpec readyMessage = MessageSpec.builder()
                .updateReceiver(commandContext.getUpdateReceiver())
                .userId(commandContext.getId())
                .text(text)
                .inlineKeyboardMarkup(inlineKeyboardMarkup)
                .disableWebPagePreview(true)
                .build();

        messageSender.sendMessage(readyMessage);
    }

    private String getTextAnswer() {
        return "👀 Отправьте ссылку на канал, который нужно отслеживать";
    }

    private InlineKeyboardMarkup getCancelButton() {
        return KeyboardFactory.builder()
                .addButtonsRow(listOfButtons.getCancelButton())
                .build();
    }

    private InlineKeyboardMarkup getMainMenuButton() {
        return KeyboardFactory.builder()
                .addButtonsRow(listOfButtons.getMainMenuButton())
                .build();
    }
}
