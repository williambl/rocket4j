package com.williambl.demo.rocket4j;

import java.io.*;
import java.net.Socket;

public class SocketConnector implements Connector {

    private final Socket socket;
    private final DataOutputStream out;
    private final DataInputStream in;
    private Rocket4J rocket;

    public SocketConnector(String address, int port) throws RocketConnectionException {
        this.socket = initSocket(address, port);
        try {
            this.out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
            this.in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
        } catch (IOException e) {
            throw new RocketConnectionException("Could not create data streams from connection to server", e);
        }

        this.greetServer();
    }

    private static Socket initSocket(String address, int port) throws RocketConnectionException {
        Socket socket;
        try {
            socket = new Socket(address, port);
        } catch (IOException | SecurityException | IllegalArgumentException e) {
            throw new RocketConnectionException("Could not establish connection with server", e);
        }

        return socket;
    }

    private void greetServer() throws RocketConnectionException {
        try {
            this.out.writeBytes(ProtocolConstants.CLIENT_GREETING);
            this.out.flush();
        } catch (IOException e) {
            throw new RocketConnectionException("Could not send greeting to server", e);
        }

        byte[] response = new byte[ProtocolConstants.SERVER_GREETING.length()];
        try {
            this.in.readFully(response, 0, ProtocolConstants.SERVER_GREETING.length());
        } catch (IOException e) {
            throw new RocketConnectionException("Could not read greeting from server", e);
        }

        if (!new String(response).equals(ProtocolConstants.SERVER_GREETING)) {
            throw new RocketConnectionException("Greetings did not match: expected %s got %s".formatted(ProtocolConstants.SERVER_GREETING, new String(response)));
        }
    }

    @Override
    public void update() {
        try {
            this.readCommand();
        } catch (IOException e) {
            Rocket4J.LOGGER.error("Could not read command from server", e);
        }
    }

    private void readCommand() throws IOException {
        while (this.in.available() > 0) {
            ProtocolConstants.Command command = ProtocolConstants.Command.getById(this.in.readByte());
            Rocket4J.LOGGER.info("Handling command {}", command.name());
            command.acceptCommand.accept(this);
        }
    }

    @Override
    public void requestTrack(String name) {
        Rocket4J.LOGGER.info("Sending command {} ({})", ProtocolConstants.Command.GET_TRACK.name(), name);
        try {
            this.out.writeByte(ProtocolConstants.Command.GET_TRACK.id);
            this.out.writeInt(name.length());
            this.out.writeBytes(name);
            this.out.flush();
        } catch (IOException e) {
            Rocket4J.LOGGER.error("Could not communicate with server.", e);
        }
    }

    @Override
    public void onControllerRowChanged(int row) {
        Rocket4J.LOGGER.info("Sending command {} ({})", ProtocolConstants.Command.SET_ROW.name(), row);
        try {
            this.out.writeByte(ProtocolConstants.Command.SET_ROW.id);
            this.out.writeInt(row);
            this.out.flush();
        } catch (IOException e) {
            Rocket4J.LOGGER.error("Could not communicate with server.", e);
        }
    }

    @Override
    public void setRocket(Rocket4J rocket4J) {
        this.rocket = rocket4J;
    }

    @Override
    public void close() throws IOException {
        this.in.close();
        this.out.close();
        this.socket.close();
    }

    private void acceptSetKey() throws IOException {
        int trackId = this.in.readInt();
        int row = this.in.readInt();
        float value = this.in.readFloat();
        byte type = this.in.readByte();

        Track track = this.rocket.getTrack(trackId);
        if (track != null) {
            track.addOrUpdateKey(new TrackKey(row, value, type));
        }
    }


    public void acceptDeleteKey() throws IOException {
        int trackId = this.in.readInt();
        int row = this.in.readInt();

        Track track = this.rocket.getTrack(trackId);
        if (track != null) {
            track.deleteKey(row);
        }
    }

    public void acceptSetRow() throws IOException {
        int row = this.in.readInt();

        // suppress events, otherwise a loop between demo
        // and Rocket emerges
        this.rocket.getController().setCurrentRow(row, true);
    }

    public void acceptPause() throws IOException {
        if (this.in.readBoolean()) {
            this.rocket.getController().pause();
        } else {
            this.rocket.getController().play();
        }
    }

    public void acceptSaveTracks() throws IOException {
    }

    static final class ProtocolConstants {
        private static final String CLIENT_GREETING = "hello, synctracker!";

        private static final String SERVER_GREETING = "hello, demo!";

        enum Command {
            SET_KEY(0, SocketConnector::acceptSetKey),
            DELETE_KEY(1, SocketConnector::acceptDeleteKey),
            GET_TRACK(2, $ -> {}),
            SET_ROW(3, SocketConnector::acceptSetRow),
            PAUSE(4, SocketConnector::acceptPause),
            SAVE_TRACKS(5, SocketConnector::acceptSaveTracks);
            private final byte id;
            private final CommandAcceptor acceptCommand;

            Command(int id, CommandAcceptor acceptCommand) {
                this.id = (byte) id;
                this.acceptCommand = acceptCommand;
            }

            static Command getById(byte id) {
                for (var command : values()) {
                    if (command.id == id) {
                        return command;
                    }
                }

                throw new IllegalArgumentException("Incorrect Command ID: "+id);
            }
        }

        @FunctionalInterface
        interface CommandAcceptor {
            void accept(SocketConnector connector) throws IOException;
        }
    }
}
