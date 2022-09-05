package com.williambl.demo.rocket4j;

import java.io.IOException;

final class RocketConnectionException extends IOException {
    public RocketConnectionException(String message) {
        super(message);
    }

    public RocketConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
