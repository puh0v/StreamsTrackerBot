package io.github.puh0v.bot.commands;

import lombok.Getter;

@Getter
public enum CommandNames {
    START("/start"),
    SEND_LINK("/send_link"),
    MY_SUBSCRIPTIONS("/my_subscriptions"),
    DELETE_SUBSCRIPTION("/delete_subscription"),
    ADMIN_COMMAND("/admin_command"),
    CANCEL("/cancel"),
    PAGE("/PAGE_");

    private String code;

    private CommandNames(String code) {
        this.code = code;
    }
}
