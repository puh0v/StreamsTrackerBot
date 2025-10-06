package io.github.puh0v.youtube.exceptions;

public class ChannelNotFoundException extends RuntimeException {

    public ChannelNotFoundException() {
        super("Такого канала не существует");
    }

    public ChannelNotFoundException(String message) {
        super(message);
    }
}
