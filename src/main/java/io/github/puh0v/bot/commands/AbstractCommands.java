package io.github.puh0v.bot.commands;

/**
 * Абстрактный класс для всех команд телеграм-бота.
 * <p>
 * Необходим для создания единообразия кода у всех команд, а также для реализации полиморфизма.
 */
public abstract class AbstractCommands {
    private final String commandName;

    public AbstractCommands(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    public abstract void handleCommand(CommandContext commandContext);

    public void handleTextByFlag(CommandContext commandContext) {}
}
