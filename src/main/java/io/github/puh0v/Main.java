package io.github.puh0v;

import io.github.puh0v.config.botproperties.AdminProperties;
import io.github.puh0v.config.botproperties.BotProperties;
import io.github.puh0v.config.botproperties.AppServerProperties;
import io.github.puh0v.config.botproperties.messages.StartCommandProperties;
import io.github.puh0v.config.youtubeproperties.YouTubeProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties({
        BotProperties.class,
        YouTubeProperties.class,
        AppServerProperties.class,
        AdminProperties.class,
        StartCommandProperties.class
})
public class Main {
    public static void main(String[] args){
        SpringApplication.run(Main.class, args);
    }
}