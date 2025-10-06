package io.github.puh0v.youtube.exceptions;

public class YouTubeApiException extends RuntimeException {

    public YouTubeApiException() {
        super("Возникла ошибка при взаимодействии с API");
    }

    public YouTubeApiException(String message) {
        super(message);
    }
}
