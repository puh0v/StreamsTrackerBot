package io.github.puh0v.bot.commands.start;

import io.github.puh0v.bot.commands.CommandNames;
import io.github.puh0v.bot.buttons.KeyboardFactory;
import io.github.puh0v.bot.buttons.ListOfButtons;
import io.github.puh0v.config.BotProperties;
import io.github.puh0v.bot.commands.AbstractCommands;
import io.github.puh0v.bot.commands.CommandContext;
import io.github.puh0v.bot.services.messagesender.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/** Сообщения при первом запуске/перезапуске бота. Также служит главным меню, где находится основной функционал бота.*/
@Slf4j
@Component
public class StartCommand extends AbstractCommands {
    private final BotProperties botProperties;
    private final MessageSender messageSender;
    private final ListOfButtons listOfButtons;

    public StartCommand(BotProperties config, ListOfButtons listOfButtons) {
        super(CommandNames.START);
        this.botProperties = config;
        this.listOfButtons = listOfButtons;
        this.messageSender = new MessageSender();
    }

    @Override
    public void handleCommand(CommandContext commandContext) {
        log.info("[StartCommand] Пользователь {} прислал команду \"{}\"", commandContext.getId(), getCommandName());
        messageSender.sendReplyMessage(commandContext, getWelcomeMessage(), getReadyKeyboard());
    }


    private String getWelcomeMessage() {
        return "🔔 Добро пожаловать в " + botProperties.name() + "! 🔔\n" +
                "Бот помогает отслеживать начало стримов на YouTube. Чтобы начать, нажмите на кнопку \"Отправить ссылку\"\n\n"
                + "⚠\uFE0F Сейчас бот на ранней стадии развития. В будущем появятся другие платформы и новые возможности.\n\n"
                + "\uD83D\uDC64 Разработчик: @puh0v";
    }

    private InlineKeyboardMarkup getReadyKeyboard() {
        return KeyboardFactory.builder()
                .addButtonsRow(listOfButtons.getMySubscriptionsButton())
                .addButtonsRow(listOfButtons.getSendLinkButton())
                .build();
    }
}
