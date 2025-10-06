package io.github.puh0v.bot.commands;

import io.github.puh0v.bot.services.updatereceiver.UpdateReceiver;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Контекст запроса пользователя Телеграм-боту.
 * <p>
 * Позволяет собирать, хранить и удобно использовать
 * всю необходимую информацию о пользоваетеле и апдейте в целом.
 */
public class CommandContext {
    private final UpdateReceiver updateReceiver;
    private final Update update;
    private final Long id;
    private final String userMessage;

    private CommandContext(UpdateReceiver updateReceiver, Update update, Long id, String userTextMessage) {
        this.updateReceiver = updateReceiver;
        this.update = update;
        this.id = id;
        this.userMessage = userTextMessage;
    }

    public UpdateReceiver getUpdateReceiver() {
        return updateReceiver;
    }

    public Update getUpdate() {
        return update;
    }

    public Long getId() {
        return id;
    }

    public String getUserMessage() {
        return userMessage;
    }


    public static class Builder {
        private UpdateReceiver updateReceiver;
        private Update update;
        private Long id;
        private String userMessage;

        public Builder setUpdateReceiver(UpdateReceiver updateReceiver) {
            this.updateReceiver = updateReceiver;
            return this;
        }

        public Builder setUpdate(Update update) {
            this.update = update;
            return this;
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setUserMessage(String userMessage) {
            this.userMessage = userMessage;
            return this;
        }

        public CommandContext build() {
            return new CommandContext(updateReceiver, update, id, userMessage);
        }
    }
}
