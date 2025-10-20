package io.github.puh0v.bot.commands.mysubscriptions;

import io.github.puh0v.bot.buttons.util.ParseNumberOfPage;
import io.github.puh0v.bot.commands.AbstractCommands;
import io.github.puh0v.bot.commands.CommandContext;
import io.github.puh0v.bot.commands.CommandNames;
import io.github.puh0v.bot.commands.pagerenderer.PageWithPaginationRenderer;
import io.github.puh0v.bot.commands.pagerenderer.TextAndPaginationButtonsContext;
import io.github.puh0v.bot.services.flagmanager.FlagManager;
import io.github.puh0v.bot.services.messagesender.MessageSender;
import io.github.puh0v.bot.buttons.ListOfButtons;
import io.github.puh0v.bot.services.messagesender.util.MessageSpec;
import io.github.puh0v.db.subscriptions.SubscriptionsEntity;
import io.github.puh0v.db.subscriptions.SubscriptionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;


/**
 * Команда для получения списка YouTube каналов, стримы которых отслеживает конкретный пользователь
 */
@Slf4j
@Component
public class MySubscriptions extends AbstractCommands {
    private final MessageSender messageSender;
    private final ListOfButtons listOfButtons;
    private final PageWithPaginationRenderer pageWithPaginationRenderer;
    private final FlagManager flagManager;
    private final SubscriptionsRepository subscriptionsRepository;

    public MySubscriptions(ListOfButtons listOfButtons, PageWithPaginationRenderer pageWithPaginationRenderer, FlagManager flagManager, SubscriptionsRepository subscriptionsRepository) {
        super(CommandNames.MY_SUBSCRIPTIONS.getCode());
        this.listOfButtons = listOfButtons;
        this.pageWithPaginationRenderer = pageWithPaginationRenderer;
        this.flagManager = flagManager;
        this.subscriptionsRepository = subscriptionsRepository;
        this.messageSender = new MessageSender();
    }

    @Override
    public void handleCommand(CommandContext commandContext) {
        Long userId = commandContext.getId();
        log.info("[MySubscriptions] Пользователь {} прислал команду \"{}\"", userId, getCommandName());

        List<SubscriptionsEntity> listOfChannels = subscriptionsRepository.findAllByUser_TelegramId(userId);

        if (listOfChannels.isEmpty()) {
            log.info("[MySubscriptions] Список заметок пользователя {} пуст", userId);
            String message = "\uD83D\uDCC2 Ваш список каналов пуст";

            MessageSpec readyMessage = MessageSpec.builder()
                    .updateReceiver(commandContext.getUpdateReceiver())
                    .userId(userId)
                    .text(message)
                    .inlineKeyboardMarkup(getKeyboardWithoutPagination())
                    .disableWebPagePreview(true)
                    .build();

            messageSender.sendMessage(readyMessage);

        } else {
            TextAndPaginationButtonsContext readyPageWithPagination = pageWithPaginationRenderer.getReadyPageContext(listOfChannels, 1, CommandNames.MY_SUBSCRIPTIONS);
            String text = readyPageWithPagination.getMessage();
            List<InlineKeyboardButton> paginationButtons = readyPageWithPagination.getPaginationButtons();

            MessageSpec readyMessage = MessageSpec.builder()
                    .updateReceiver(commandContext.getUpdateReceiver())
                    .userId(userId)
                    .text(text)
                    .inlineKeyboardMarkup(getKeyboardWithPagination(paginationButtons))
                    .disableWebPagePreview(true)
                    .build();

            messageSender.sendMessage(readyMessage);
            flagManager.setFlag(userId, this);
        }
    }


    @Override
    public void handleTextByFlag(CommandContext commandContext) {
        Long userId = commandContext.getId();
        log.info("[MySubscriptions] Пользователь {} ответил на сообщение бота по флагу \"{}\"",
                userId,
                CommandNames.MY_SUBSCRIPTIONS.getCode()
        );

        List<SubscriptionsEntity> listOfChannels = subscriptionsRepository.findAllByUser_TelegramId(userId);
        String userMessage = commandContext.getUserMessage();

        if (userMessage.contains(CommandNames.PAGE.getCode())) {
            log.info("[MySubscriptions] Пользователь {} перелистывает страницу с каналами...", userId);
            int pageNumber = Integer.parseInt(ParseNumberOfPage.getNumberOfPage(userMessage));

            TextAndPaginationButtonsContext textAndPaginationButtonsContext = pageWithPaginationRenderer.getReadyPageContext(listOfChannels, pageNumber,
                    CommandNames.MY_SUBSCRIPTIONS);

            String text = textAndPaginationButtonsContext.getMessage();
            List<InlineKeyboardButton> paginationButtons = textAndPaginationButtonsContext.getPaginationButtons();
            InlineKeyboardMarkup inlineKeyboardMarkup = getKeyboardWithPagination(paginationButtons);

            MessageSpec readyMessage = MessageSpec.builder()
                            .updateReceiver(commandContext.getUpdateReceiver())
                            .userId(userId)
                            .text(text)
                            .inlineKeyboardMarkup(inlineKeyboardMarkup)
                            .disableWebPagePreview(true)
                            .build();

            messageSender.sendMessage(readyMessage);
            flagManager.setFlag(userId, this);
        }
    }


    private InlineKeyboardMarkup getKeyboardWithPagination(List<InlineKeyboardButton> paginationButtons) {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(paginationButtons)
                .keyboardRow(List.of(listOfButtons.getDeleteChannelButton()))
                .keyboardRow(List.of(listOfButtons.getMainMenuButton()))
                .build();
    }

    private InlineKeyboardMarkup getKeyboardWithoutPagination() {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(listOfButtons.getMainMenuButton()))
                .build();
    }
}
