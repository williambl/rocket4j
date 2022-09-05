package com.williambl.demo.rocket4j;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class Rocket4J {

    public static final Logger LOGGER = LoggerFactory.getLogger("Rocket4J");
    private final Connector connector;
    private final Controller controller;
    private final TrackContainer tracks;

    public Rocket4J(Connector connector, Controller controller) {
        this.connector = connector;
        this.controller = controller;
        this.tracks = new TrackContainer(this);

        this.connector.setRocket(this);
    }

    public static @NotNull Rocket4J create(Controller controller) {
        return create("localhost", 1338, controller, 10);
    }

    public static @NotNull Rocket4J create(Path path, Controller controller) {
        try {
            return new Rocket4J(new FileConnector(path), controller);
        } catch (RocketConnectionException e) {
            LOGGER.error("Could not create Rocket4J.");
            throw new RuntimeException("Could not create Rocket4J.", e);
        }
    }

    public static @NotNull Rocket4J create(String address, int port, Controller controller, int tries) {
        Rocket4J instance = null;
        for (int i = 0; i < tries; i++) {
            try {
                instance = Rocket4J.create(address, port, controller);
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

    public static Rocket4J create(String address, int port, Controller controller) throws RocketConnectionException {
        return new Rocket4J(new SocketConnector(address, port), controller);
    }

    public Track getTrack(int trackId) {
        return this.tracks.getById(trackId);
    }

    public Controller getController() {
        return this.controller;
    }

    /**
     * Returns current time in seconds.
     */
    public double getCurrentTime() {
        return this.controller.getCurrentTime();
    }

    /**
     * Returns current (fractional) row.
     */
    public double getCurrentRow() {
        return this.controller.getCurrentRow();
    }

    public void requestTrack(TrackContainer trackContainer, String name) {
        if (trackContainer == this.tracks) {
            this.connector.requestTrack(name);
        }
    }

    public void update() {
        this.connector.update();
        this.controller.update();
    }
}
