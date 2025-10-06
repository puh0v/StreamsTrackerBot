package io.github.puh0v.bot.services.messagesender;

import io.github.puh0v.bot.commands.CommandContext;
import io.github.puh0v.bot.services.updatereceiver.UpdateReceiver;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


/**
 * Сервис для удобной сборки и отправки сообщений пользователям Telegram-бота.
 */
@Slf4j
public class MessageSender {

    public void sendReplyMessage(CommandContext commandContext, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        log.info("[MessageSender] Подготавливаю сообщение \"{}\" для пользователя {}",
                text, commandContext.getId());

        SendMessage sendMessage = SendMessage.builder()
                .chatId(commandContext.getId().toString())
                .text(text)
                .parseMode(ParseMode.HTML)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        sendMessage(commandContext.getUpdateReceiver(), sendMessage, commandContext.getId());
    }


    public void sendNotificationMessage(UpdateReceiver updateReceiver, Long userId, String text) {
        log.info("[MessageSender] Начинаю подготовку сообщения с уведомлением для пользователя {}", userId);
        SendMessage sendMessage = SendMessage.builder()
                .chatId(userId.toString())
                .text(text)
                .parseMode(ParseMode.HTML)
                .build();
        sendMessage(updateReceiver, sendMessage, userId);
    }

    public void sendAnswerCallbackQuery(CommandContext commandContext, AnswerCallbackQuery answerCallbackQuery) {
        try {
            commandContext.getUpdateReceiver().execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            log.error("[MessageSender] Возникла ошибка закрытия запроса инлайн-кнопки у пользователя {}",
                    commandContext.getId(), e);
        }
    }


    private void sendMessage(UpdateReceiver updateReceiver, SendMessage sendMessage, Long userId) {
        try {
            updateReceiver.execute(sendMessage);
            log.info("[MessageSender] Сообщение успешно отправлено пользователю  {}!", userId);
        } catch (TelegramApiException e) {
            log.error("[MessageSender] Возникла ошибка при отправке сообщения пользователю {}", userId, e);
            throw new RuntimeException(e);
        }
    }
}