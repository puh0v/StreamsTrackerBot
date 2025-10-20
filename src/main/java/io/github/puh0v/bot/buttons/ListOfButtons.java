package io.github.puh0v.bot.buttons;


import io.github.puh0v.bot.commands.CommandNames;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;


@Component
public class ListOfButtons {

    public InlineKeyboardButton getMainMenuButton() {
        return InlineKeyboardButton.builder()
                .text("\uD83C\uDFE0 Главное меню")
                .callbackData(CommandNames.START.getCode())
                .build();
    }

    public InlineKeyboardButton getSendLinkButton() {
        return InlineKeyboardButton.builder()
                .text("\uD83D\uDD17 Отправить ссылку")
                .callbackData(CommandNames.SEND_LINK.getCode())
                .build();
    }

    public InlineKeyboardButton getMySubscriptionsButton() {
        return InlineKeyboardButton.builder()
                .text("\uD83D\uDCF1 Мои подписки")
                .callbackData(CommandNames.MY_SUBSCRIPTIONS.getCode())
                .build();
    }

    public InlineKeyboardButton getDeleteChannelButton() {
        return InlineKeyboardButton.builder()
                .text("\uD83D\uDDD1 Отписаться от канала")
                .callbackData(CommandNames.DELETE_SUBSCRIPTION.getCode())
                .build();
    }

    public InlineKeyboardButton getCancelButton() {
        return InlineKeyboardButton.builder()
                .text("\uD83D\uDEAB Отмена")
                .callbackData(CommandNames.CANCEL.getCode())
                .build();
    }

    public InlineKeyboardButton getNextPageButton(Integer initialPage) {
        Integer nextPage = initialPage + 1;
        return InlineKeyboardButton.builder()
                .text("Следующая страница ➡\uFE0F")
                .callbackData(CommandNames.PAGE.getCode() + nextPage.toString())
                .build();
    }

    public InlineKeyboardButton getPreviousPageButton(Integer initialPage) {
        Integer previousPage = initialPage - 1;
        return InlineKeyboardButton.builder()
                .text("⬅\uFE0F Предыдущая страница")
                .callbackData(CommandNames.PAGE.getCode() + previousPage.toString())
                .build();
    }
}
