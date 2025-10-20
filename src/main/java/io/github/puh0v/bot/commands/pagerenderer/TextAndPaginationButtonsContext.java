package io.github.puh0v.bot.commands.pagerenderer;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TextAndPaginationButtonsContext {
    private final String message;
    private final List<InlineKeyboardButton> paginationButtons;

    public TextAndPaginationButtonsContext(String text) {
        this.message = text;
        this.paginationButtons = new ArrayList<>();
    }

    public TextAndPaginationButtonsContext(String text, List<InlineKeyboardButton> paginationButtons) {
        this.message = text;
        this.paginationButtons = new ArrayList<>(paginationButtons);
    }
}
