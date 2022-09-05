package com.williambl.demo.rocket4j;

public interface Connector extends AutoCloseable {
    void update();
    void requestTrack(String name);
    void onControllerRowChanged(int row);
    void setRocket(Rocket4J rocket4J);
}
