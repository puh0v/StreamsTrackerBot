package io.github.puh0v.youtube.util.validation;

import java.util.Set;

/**
 * Утилитный класс для проверки домена в отправленной пользователем ссылке.
 */
public class YouTubeLinkValidation {
    public static final Set<String> WHITELIST = Set.of(
            "https://www.youtube.com/",
            "https://youtube.com/",
            "youtube.com/"
    );

    private YouTubeLinkValidation() {}


    public static boolean isYouTubeLink(String link) {
        return WHITELIST.stream().anyMatch(link :: startsWith);
    }
}
