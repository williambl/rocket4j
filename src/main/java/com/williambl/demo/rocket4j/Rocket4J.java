package com.williambl.demo.rocket4j;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rocket4J {

    public static final Logger LOGGER = LoggerFactory.getLogger("Rocket4J");
    private final Connector connector;

    public Rocket4J(Connector connector) {
        this.connector = connector;
    }

    public static @NotNull Rocket4J create(String address, int port, int tries) {
        Rocket4J instance = null;
        for (int i = 0; i < tries; i++) {
            try {
                instance = Rocket4J.create(address, port);
            } catch (RocketConnectionException e) {
                LOGGER.warn("Could not create Rocket4J: {}. {} more tries.", e.getLocalizedMessage(), tries-i);
            }
        }

        if (instance == null) {
            LOGGER.error("Could not create Rocket4J.");
            throw new RuntimeException("Could not create Rocket4J.");
        } else {
            return instance;
        }
    }

    public static Rocket4J create(String address, int port) throws RocketConnectionException {
        return new Rocket4J(new SocketConnector(address, port));
    }
}
