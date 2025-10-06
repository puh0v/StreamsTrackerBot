package io.github.puh0v.bot.buttons;


import io.github.puh0v.bot.commands.CommandNames;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;


@Component
public class ListOfButtons {

    public InlineKeyboardButton getMainMenuButton() {
        return InlineKeyboardButton.builder()
                .text("\uD83C\uDFE0 Главное меню")
                .callbackData(CommandNames.START)
                .build();
    }

    public InlineKeyboardButton getSendLinkButton() {
        return InlineKeyboardButton.builder()
                .text("\uD83D\uDD17 Отправить ссылку")
                .callbackData(CommandNames.SEND_LINK)
                .build();
    }

    public InlineKeyboardButton getMySubscriptionsButton() {
        return InlineKeyboardButton.builder()
                .text("\uD83D\uDCF1 Мои подписки")
                .callbackData(CommandNames.MY_SUBSCRIPTIONS)
                .build();
    }

    public InlineKeyboardButton getCancelButton() {
        return InlineKeyboardButton.builder()
                .text("\uD83D\uDEAB Отмена")
                .callbackData(CommandNames.CANCEL)
                .build();
    }
}
