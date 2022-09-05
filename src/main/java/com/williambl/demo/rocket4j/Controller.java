package com.williambl.demo.rocket4j;

import java.util.ArrayList;
import java.util.List;

public abstract class Controller {
    protected double currentRow = 0.0;
    protected double rowsPerSecond;

    private boolean playing = false;

    List<ControllerListener> listeners = new ArrayList<>();

    public Controller(double rps) {
        this.rowsPerSecond = rps;
    }

    public void addEventListener(ControllerListener listener) {
        this.listeners.add(listener);
    }

    public void removeEventListener(ControllerListener listener) {
        this.listeners.remove(listener);
    }

    public double getCurrentRow() {
        return this.currentRow;
    }

    public double getCurrentTime() {
        return this.currentRow / this.rowsPerSecond;
    }

    /**
     * @param row new row value
     * @param silent to suppress notifications
     */
    public void setCurrentRow(double row, boolean silent) {
        // If whole row changes, notify listeners
        if ((int) row != (int) this.currentRow && !silent) {
            for (ControllerListener listener : this.listeners) {
                listener.controllerRowChanged((int) row);
            }
        }

        this.currentRow = row;
    }

    abstract public void update();

    public boolean isPlaying() {
        return this.playing;
    }

    private void setPlayState(boolean status) {
        if (status != this.playing) {
            this.playing = status;
            for (ControllerListener listener : this.listeners) {
                listener.controllerStatusChanged(status);
            }
        }
    }

    public void pause() {
        this.setPlayState(false);
    }

    public void play() {
        this.setPlayState(true);
    }
}
