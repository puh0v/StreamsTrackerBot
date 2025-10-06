package io.github.puh0v.youtube.exceptions;

public class DuplicateRecordException extends RuntimeException {

    public DuplicateRecordException() {
        super("Такая запись уже существует в БД");
    }

    public DuplicateRecordException(String message) {
        super(message);
    }
}
