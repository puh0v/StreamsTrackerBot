package io.github.puh0v.youtube.util.parser;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Утилитный класс для извлечения тега YouTube канала из ссылки на этот канал.
 */
public class YouTubeParser {

    public static String parseChannelIdentifier(String link) throws URISyntaxException {
        String path = new URI(link).getPath();
        if (path.startsWith("/@")) {
            path = path.substring(1);
        } else if (path.startsWith("/channel/")) {
            path = path.substring(("/channel/".length()));
        } else if (path.startsWith("/user/")) {
            path = path.substring("/user/".length());
        } else if (path.startsWith("/c/")) {
            path = path.substring("/c/".length());
        }

        if (path.contains("/")) {
            int index = path.indexOf("/");
            path = path.substring(0, index);
        }
        return path;
    }
}