package io.github.puh0v.bot.commands.start;

import io.github.puh0v.bot.commands.CommandNames;
import io.github.puh0v.bot.buttons.KeyboardFactory;
import io.github.puh0v.bot.buttons.ListOfButtons;
import io.github.puh0v.bot.services.messagesender.util.MessageSpec;
import io.github.puh0v.config.botproperties.BotProperties;
import io.github.puh0v.bot.commands.AbstractCommands;
import io.github.puh0v.bot.commands.CommandContext;
import io.github.puh0v.bot.services.messagesender.MessageSender;
import io.github.puh0v.config.botproperties.messages.StartCommandProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.File;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/** Сообщения при первом запуске/перезапуске бота. Также служит главным меню, где находится основной функционал бота.*/
@Slf4j
@Component
public class StartCommand extends AbstractCommands {
    private final BotProperties botProperties;
    private final MessageSender messageSender;
    private final ListOfButtons listOfButtons;
    private final StartCommandProperties startCommandProperties;


    public StartCommand(BotProperties config, ListOfButtons listOfButtons, StartCommandProperties startCommandProperties) {
        super(CommandNames.START.getCode());
        this.botProperties = config;
        this.listOfButtons = listOfButtons;
        this.startCommandProperties = startCommandProperties;
        this.messageSender = new MessageSender();
    }

    @Override
    public void handleCommand(CommandContext commandContext) {
        log.info("[StartCommand] Пользователь {} прислал команду \"{}\"", commandContext.getId(), getCommandName());

        if (startCommandProperties.isImageReady()) {
            String imagePath = startCommandProperties.getImagePath();
            File image = new File(imagePath);

            MessageSpec readyMessage = MessageSpec.builder()
                    .updateReceiver(commandContext.getUpdateReceiver())
                    .userId(commandContext.getId())
                    .text(getWelcomeMessage())
                    .filePath(image)
                    .inlineKeyboardMarkup(getReadyKeyboard())
                    .disableWebPagePreview(true)
                    .build();

            messageSender.sendMessage(readyMessage);

        } else {
            MessageSpec readyMessage = MessageSpec.builder()
                    .updateReceiver(commandContext.getUpdateReceiver())
                    .userId(commandContext.getId())
                    .text(getWelcomeMessage())
                    .inlineKeyboardMarkup(getReadyKeyboard())
                    .disableWebPagePreview(true)
                    .build();

            messageSender.sendMessage(readyMessage);
        }
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
