package io.github.puh0v.config.botproperties.messages;

import io.github.puh0v.bot.commands.CommandNames;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

@Slf4j
@Getter
@ConfigurationProperties(prefix = "app.modes.messages.start")
public class StartCommandProperties {
    @Setter
    private boolean enableImageMessage;
    @Setter
    private String imagePath;
    private boolean imageStatus;


    @PostConstruct
    private void initImageStatus() {
        File image = new File(imagePath);

        if (imagePath == null || imagePath.isBlank() || !enableImageMessage || !image.isFile()) {
            log.info("[StartCommandProperties] Изображение для команды \"{}\" не готово для использования."
                            + " Вместо изображения с подписью будет отправляться только текстовое сообщение.",
                    CommandNames.START.getCode()
            );
            imageStatus = false;

        } else {
            log.info("[StartCommandProperties] Изображение для команды \"{}\" готово к отправке!", CommandNames.START.getCode());
            imageStatus = true;
        }
    }

    public boolean isImageReady() {
        return imageStatus;
    }
}