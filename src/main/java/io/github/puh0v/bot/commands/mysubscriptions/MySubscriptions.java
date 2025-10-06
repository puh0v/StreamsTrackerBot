package io.github.puh0v.bot.commands.mysubscriptions;

import io.github.puh0v.bot.commands.AbstractCommands;
import io.github.puh0v.bot.commands.CommandContext;
import io.github.puh0v.bot.commands.CommandNames;
import io.github.puh0v.bot.services.messagesender.MessageSender;
import io.github.puh0v.bot.buttons.ListOfButtons;
import io.github.puh0v.config.YouTubeProperties;
import io.github.puh0v.db.channels.ChannelsEntity;
import io.github.puh0v.db.subscriptions.SubscriptionsEntity;
import io.github.puh0v.db.subscriptions.SubscriptionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;


/**
 * Команда для получения списка YouTube каналов, стримы которых отслеживает конкретный пользователь
 */
@Slf4j
@Component
public class MySubscriptions extends AbstractCommands {
    private final SubscriptionsRepository subscriptionsRepository;
    private final YouTubeProperties youTubeProperties;
    private final MessageSender messageSender;
    private final ListOfButtons listOfButtons;

    public MySubscriptions(SubscriptionsRepository subscriptionsRepository, YouTubeProperties youTubeProperties,
                           ListOfButtons listOfButtons) {
        super(CommandNames.MY_SUBSCRIPTIONS);
        this.subscriptionsRepository = subscriptionsRepository;
        this.youTubeProperties = youTubeProperties;
        this.listOfButtons = listOfButtons;
        this.messageSender = new MessageSender();
    }

    @Override
    public void handleCommand(CommandContext commandContext) {
        log.info("[MySubscriptions] Пользователь {} прислал команду \"{}\"", commandContext.getId(), getCommandName());
        List<SubscriptionsEntity> listOfChannels = getListOfChannels(commandContext);
        sendMessagesWithLinks(commandContext, listOfChannels);
    }

    private List<SubscriptionsEntity> getListOfChannels(CommandContext commandContext) {
        return subscriptionsRepository.findAllByUser_TelegramId(commandContext.getId());
    }

    private void sendMessagesWithLinks(CommandContext commandContext, List<SubscriptionsEntity> channels) {
        log.info("[MySubscriptions] Началась подготовка списка с подписками пользователя {}", commandContext.getId(), getCommandName());
        String mainPageUrl = youTubeProperties.mainPageUrl();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\uD83D\uDCCC Стримеры, которых вы отслеживаете \uD83D\uDCCC\n\n");

        for(SubscriptionsEntity channel : channels) {
            ChannelsEntity ch = channel.getChannel();
            String title = ch.getTitle();
            String channelHandle = ch.getChannelHandle();
            stringBuilder.append("<a href=\"" + mainPageUrl +"/" + channelHandle + "\">" + title + "</a>\n");
        }
        messageSender.sendReplyMessage(commandContext, stringBuilder.toString(), getReadyKeyboard());
    }

    private InlineKeyboardMarkup getReadyKeyboard() {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(listOfButtons.getMainMenuButton()))
                .build();
    }
}
