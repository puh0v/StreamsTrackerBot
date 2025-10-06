package io.github.puh0v;

import io.github.puh0v.config.BotProperties;
import io.github.puh0v.config.AppServerProperties;
import io.github.puh0v.config.YouTubeProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties({BotProperties.class, YouTubeProperties.class, AppServerProperties.class})
public class Main {
    public static void main(String[] args){
        SpringApplication.run(Main.class, args);
    }
}