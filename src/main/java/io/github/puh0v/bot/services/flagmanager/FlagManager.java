package io.github.puh0v.bot.services.flagmanager;

import io.github.puh0v.bot.commands.AbstractCommands;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FlagManager {
    Map<Long, AbstractCommands> flags;

    public FlagManager() {
        flags = new HashMap<>();
    }

    public boolean hasFlag(long id) {
        return flags.containsKey(id);
    }

    public void setFlag(long id, AbstractCommands commands) {
        flags.put(id, commands);
    }

    public AbstractCommands getFlag(long id) {
        return flags.get(id);
    }

    public void removeFlag(long id) {
        flags.remove(id);
    }
}
