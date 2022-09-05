package com.williambl.demo.rocket4j;

import java.io.*;
import java.net.Socket;

public class SocketConnector implements Connector {

    private final Socket socket;
    private final DataOutputStream out;
    private final DataInputStream in;

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

    }

    @Override
    public void onTrackAdded() {

    }

    @Override
    public void onControllerRowChanged() {

    }

    @Override
    public void close() {

    }

    static final class ProtocolConstants {
        private static final String CLIENT_GREETING = "hello, synctracker!";
        private static final String SERVER_GREETING = "hello, demo!";
    }

}
