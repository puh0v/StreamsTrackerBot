package io.github.puh0v.bot.buttons.util;

import io.github.puh0v.bot.commands.CommandNames;

public class ParseNumberOfPage {

    public static String getNumberOfPage(String page) {
        int startIndex = page.indexOf(CommandNames.PAGE.getCode());
        int endIndex = startIndex + CommandNames.PAGE.getCode().length();
        return page.substring(endIndex, page.length());
    }
}
