package io.github.puh0v.bot.services.messagesender.util;

import io.github.puh0v.bot.services.updatereceiver.UpdateReceiver;
import lombok.Builder;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.File;

@Builder
public record MessageSpec(
    UpdateReceiver updateReceiver,
    Long userId,
    String text,
    File filePath,
    InlineKeyboardMarkup inlineKeyboardMarkup,
    boolean disableWebPagePreview
){}
