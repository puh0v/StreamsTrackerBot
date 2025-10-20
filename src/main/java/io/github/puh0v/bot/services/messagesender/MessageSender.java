package io.github.puh0v.bot.services.messagesender;

import io.github.puh0v.bot.commands.CommandContext;
import io.github.puh0v.bot.services.messagesender.util.MessageSpec;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


/**
 * Сервис для удобной сборки и отправки сообщений пользователям Telegram-бота.
 */
@Slf4j
public class MessageSender {

    public void sendMessage(MessageSpec readyMessage) {
        Long userId = readyMessage.userId();

        if (readyMessage.filePath() != null) {
            log.info("[MessageSender] Подготавливаю сообщение для отправки пользователю {}", userId);

            SendPhoto sendPhoto = SendPhoto.builder()
                    .chatId(userId.toString())
                    .photo(new InputFile(readyMessage.filePath()))
                    .caption(readyMessage.text())
                    .replyMarkup(readyMessage.inlineKeyboardMarkup())
                    .build();

            SendMessageWithPhoto(sendPhoto, readyMessage);

        } else {
            log.info("[MessageSender] Подготавливаю изображение с текстом для отправки пользователю {}", userId);

            SendMessage sendMessage = SendMessage.builder()
                    .chatId(userId.toString())
                    .text(readyMessage.text())
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(readyMessage.inlineKeyboardMarkup())
                    .disableWebPagePreview(readyMessage.disableWebPagePreview())
                    .build();

            sendTextMessage(sendMessage, readyMessage);
        }
    }


    public void prepareTextMessageWithNotification(MessageSpec readyMessage) {
        Long userId = readyMessage.userId();
        log.info("[MessageSender] Начинаю подготовку сообщения с уведомлением для отправки пользователю {}", userId);

        SendMessage sendMessage = SendMessage.builder()
                .chatId(userId.toString())
                .text(readyMessage.text())
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(false)
                .build();

        sendTextMessage(sendMessage, readyMessage);
    }


    public void sendAnswerCallbackQuery(CommandContext commandContext, AnswerCallbackQuery answerCallbackQuery) {
        try {
            commandContext.getUpdateReceiver().execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            log.error("[MessageSender] Возникла ошибка закрытия запроса инлайн-кнопки у пользователя {}",
                    commandContext.getId(), e);
        }
    }


    private void sendTextMessage(SendMessage sendMessage, MessageSpec readyMessage) {
        try {
            readyMessage.updateReceiver().execute(sendMessage);
            log.info("[MessageSender] Сообщение успешно отправлено пользователю  {}!", readyMessage.userId());
        } catch (TelegramApiException e) {
            log.error("[MessageSender] Возникла ошибка при отправке сообщения пользователю {}", readyMessage.userId(), e);
            throw new RuntimeException(e);
        }
    }

    private void SendMessageWithPhoto(SendPhoto sendPhoto, MessageSpec readyMessage) {
        try {
            readyMessage.updateReceiver().execute(sendPhoto);
        } catch (TelegramApiException e) {
            log.error("[MessageSender] Возникла ошибка при отправке изображения пользователю {}", readyMessage.userId(), e);
            throw new RuntimeException(e);
        }
    }
}