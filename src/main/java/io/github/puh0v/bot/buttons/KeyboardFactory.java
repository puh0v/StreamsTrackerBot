package io.github.puh0v.bot.buttons;


import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/***
 * Утилитный класс для построения inline-клавиатуры при помощи Builder.
 * </p>
 * Позволяет добавлять строки кнопок при помощи метода addButtonsRow(), а
 * также, при помощи build(), собирать их в InlineKeyboardMarkup для дальнейшего использования в сообщениях.
 */
public class KeyboardFactory {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        List<List<InlineKeyboardButton>> rowsWithButtons = new ArrayList<>();

        public Builder addButtonsRow(InlineKeyboardButton ... buttons) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (InlineKeyboardButton button : buttons) {
                row.add(button);
            }
            rowsWithButtons.add(row);
            return this;
        }

        public InlineKeyboardMarkup build() {
            InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
            keyboardMarkup.setKeyboard(rowsWithButtons);
            return keyboardMarkup;
        }
    }
}
