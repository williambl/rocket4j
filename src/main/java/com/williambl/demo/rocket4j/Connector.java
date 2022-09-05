package com.williambl.demo.rocket4j;

public interface Connector extends AutoCloseable {
    void update();
    void onTrackAdded();
    void onControllerRowChanged();
}
