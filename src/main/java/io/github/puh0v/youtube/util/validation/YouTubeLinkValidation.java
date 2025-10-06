package io.github.puh0v.youtube.util.validation;

import java.net.URI;
import java.util.Set;

/**
 * Утилитный класс для проверки домена в отправленной пользователем ссылке.
 */
public class YouTubeLinkValidation {
    public static final Set<String> HOSTS = Set.of(
            "youtube.com", "www.youtube.com", "m.youtube.com",
            "youtu.be", "www.youtu.be"
    );

    private YouTubeLinkValidation() {}


    public static boolean isYouTubeLink(String link) {
        if (link == null) return false;
        String trimmedLink = link.trim();
        if (trimmedLink.isEmpty()) return false;

        if (trimmedLink.startsWith("@")) return true;

        if (!trimmedLink.startsWith("http://") && !trimmedLink.startsWith("https://")) {
            trimmedLink = "https://" + trimmedLink;
        }

        try {
            URI uri = URI.create(trimmedLink);
            String host = uri.getHost();
            return host != null && HOSTS.contains(host.toLowerCase());
        } catch (Exception e) {
            return false;
        }
    }
}
