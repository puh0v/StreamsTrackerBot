package io.github.puh0v.bot.commands.deletesubscription;

import io.github.puh0v.bot.buttons.KeyboardFactory;
import io.github.puh0v.bot.buttons.ListOfButtons;
import io.github.puh0v.bot.buttons.util.ParseNumberOfPage;
import io.github.puh0v.bot.commands.AbstractCommands;
import io.github.puh0v.bot.commands.CommandContext;
import io.github.puh0v.bot.commands.CommandNames;
import io.github.puh0v.bot.commands.pagerenderer.PageWithPaginationRenderer;
import io.github.puh0v.bot.commands.pagerenderer.TextAndPaginationButtonsContext;
import io.github.puh0v.bot.services.flagmanager.FlagManager;
import io.github.puh0v.bot.services.messagesender.MessageSender;
import io.github.puh0v.bot.services.messagesender.util.MessageSpec;
import io.github.puh0v.db.subscriptions.SubscriptionsEntity;
import io.github.puh0v.db.subscriptions.SubscriptionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;


@Slf4j
@Component
public class DeleteSubscription extends AbstractCommands {
    private final FlagManager flagManager;
    private final PageWithPaginationRenderer pageWithPaginationRenderer;
    private final SubscriptionsRepository subscriptionsRepository;
    private final ListOfButtons listOfButtons;
    private final MessageSender messageSender;

    public DeleteSubscription(FlagManager flagManager, PageWithPaginationRenderer pageWithPaginationRenderer, SubscriptionsRepository subscriptionsRepository,
                              ListOfButtons listOfButtons) {
        super(CommandNames.DELETE_SUBSCRIPTION.getCode());
        this.flagManager = flagManager;
        this.pageWithPaginationRenderer = pageWithPaginationRenderer;
        this.subscriptionsRepository = subscriptionsRepository;
        this.listOfButtons = listOfButtons;
        this.messageSender = new MessageSender();
    }

    @Override
    public void handleCommand(CommandContext commandContext) {
        Long userId = commandContext.getId();
        log.info("[DeleteSubscriptions] Пользователь {} прислал команду {}",
                userId,
                CommandNames.DELETE_SUBSCRIPTION.getCode());

        List<SubscriptionsEntity> listOfChannels = subscriptionsRepository.findAllByUser_TelegramId(userId);
        String message;

        if (listOfChannels.isEmpty()) {
            message = "\uD83D\uDCC2 Ваш список каналов пуст";
            sendMessage(commandContext, message, getMainMenuButton());

        } else {
            flagManager.setFlag(userId, this);

            TextAndPaginationButtonsContext textAndPaginationButtonsContext = pageWithPaginationRenderer.getReadyPageContext(listOfChannels, 1, CommandNames.DELETE_SUBSCRIPTION);
            message = textAndPaginationButtonsContext.getMessage();

            InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                            .keyboardRow(textAndPaginationButtonsContext.getPaginationButtons())
                            .keyboardRow(List.of(listOfButtons.getCancelButton()))
                            .build();

            sendMessage(commandContext, message, inlineKeyboardMarkup);
        }
    }


    @Override
    public void handleTextByFlag(CommandContext commandContext) {
        log.info("[DeleteSubscriptions] Пользователь {} ответил на сообщение бота по флагу \"{}\"",
                commandContext.getId(),
                CommandNames.DELETE_SUBSCRIPTION.getCode());

        List<SubscriptionsEntity> listOfChannels = subscriptionsRepository.findAllByUser_TelegramId(commandContext.getId());
        String userMessage = commandContext.getUserMessage();

        if (userMessage.equals(CommandNames.CANCEL.getCode())) {
            log.info("[DeleteSubscriptions] Пользователь {} отменил отписку от канала", commandContext.getId());
            sendMessage(commandContext, "\uD83D\uDE0F Отписка от канала отменена", getMainMenuButton());
            flagManager.removeFlag(commandContext.getId());
            return;
        }

        int pageNumber;

        if (userMessage.contains(CommandNames.PAGE.getCode())) {
            log.info("[DeleteSubscriptions] Пользователь {} перелистывает страницу с каналами...", commandContext.getId());

            pageNumber= Integer.parseInt(ParseNumberOfPage.getNumberOfPage(userMessage));
            TextAndPaginationButtonsContext readyMessageWithPagination = pageWithPaginationRenderer.getReadyPageContext(listOfChannels, pageNumber,
                    CommandNames.DELETE_SUBSCRIPTION);

            String message = readyMessageWithPagination.getMessage();

            InlineKeyboardMarkup readyKeyboard = InlineKeyboardMarkup.builder()
                    .keyboardRow(readyMessageWithPagination.getPaginationButtons())
                    .keyboardRow(List.of(listOfButtons.getCancelButton()))
                    .build();

            sendMessage(commandContext, message, readyKeyboard);

        } else if (userMessage.matches("\\d{1,7}")) {
            log.info("[DeleteSubscriptions] Пользователь {} прислал номер канала для его удаления", commandContext.getId());

            List<SubscriptionsEntity> subscriptions = subscriptionsRepository.findAllByUser_TelegramId(commandContext.getId());
            Integer index = Integer.parseInt(userMessage) - 1;

            if (index < 0 || index > subscriptions.size() - 1) {
                log.info("[DeleteSubscriptions] Пользователь {} прислал неверный номер канала. Ожидаем повторный запрос...", commandContext.getId());

                String message = "\uD83D\uDE42\u200D↔\uFE0F Канала c таким номером не существует. Введите другой номер.";
                sendMessage(commandContext, message, getCancelButton());

            } else {
                SubscriptionsEntity channel = subscriptions.get(index);
                String channelId = channel.getChannel().getChannelId();

                log.info("[DeleteSubscriptions] Пользователь {} прислал верный номер канала. Начинаю удаление подписки: channelId = {}",
                        commandContext.getId(),
                        channelId);

                deleteSubscription(commandContext, channel);
            }
        }
    }


    private void deleteSubscription(CommandContext commandContext, SubscriptionsEntity channel) {
        String channelTitle = channel.getChannel().getTitle();
        String channelId = channel.getChannel().getChannelId();

        try {
            subscriptionsRepository.delete(channel);
            log.info("[DeleteSubscriptions] Пользователь {} успешно отписался от канала {}",
                    commandContext.getId(),
                    channelId);

        } catch (Exception e) {
            log.error("[DeleteSubscriptions] Произошла ошибка при удалении подписки из базы данных: telegramId = {}, channelId = {}",
                    commandContext.getId(),
                    channelId);
            sendMessage(commandContext, "\uD83D\uDE1E Произошла ошибка на стороне бота. "
                    + "Попробуйте отписаться ещё раз, либо дождитесь восстановления работоспособности.", getCancelButton());
            return;
        }

        String message = "✅ Вы успешно отписались от уведомлений с канала " + channelTitle + "!";
        sendMessage(commandContext, message, getReadyKeyboardInCaseOfSuccess(commandContext));

        flagManager.removeFlag(commandContext.getId());
    }


    private void sendMessage(CommandContext commandContext, String message, InlineKeyboardMarkup inlineKeyboardMarkup) {
        MessageSpec readyMessage = MessageSpec.builder()
                .updateReceiver(commandContext.getUpdateReceiver())
                .userId(commandContext.getId())
                .text(message)
                .inlineKeyboardMarkup(inlineKeyboardMarkup)
                .disableWebPagePreview(true)
                .build();

        messageSender.sendMessage(readyMessage);
    }

    private InlineKeyboardMarkup getReadyKeyboardInCaseOfSuccess(CommandContext commandContext) {
        InlineKeyboardButton deleteOneMoreChannelButton = listOfButtons.getDeleteChannelButton();
        deleteOneMoreChannelButton.setText("\uD83D\uDDD1 Выбрать ещё один");
        return KeyboardFactory.builder()
                .addButtonsRow(deleteOneMoreChannelButton)
                .addButtonsRow(listOfButtons.getMainMenuButton())
                .build();
    }

    private InlineKeyboardMarkup getMainMenuButton() {
        return KeyboardFactory.builder()
                .addButtonsRow(listOfButtons.getMainMenuButton())
                .build();
    }

    private InlineKeyboardMarkup getCancelButton() {
        return KeyboardFactory.builder()
                .addButtonsRow(listOfButtons.getCancelButton())
                .build();
    }
}
