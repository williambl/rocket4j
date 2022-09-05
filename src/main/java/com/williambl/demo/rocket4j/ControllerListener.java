package com.williambl.demo.rocket4j;

interface ControllerListener {
    void controllerStatusChanged(boolean isPlaying);
    void controllerRowChanged(int row);
}