package com.williambl.demo.rocket4j;

public class TimeController extends Controller {
    long lastTime;

    public TimeController(int rowsPerSecond) {
        super(rowsPerSecond);
        this.lastTime = 0;
    }

    @Override
    public void update() {
        if (!this.isPlaying()) {
            this.lastTime = 0;
            return;
        }

        if (this.lastTime == 0) {
            this.lastTime = System.nanoTime();
        }

        // Update current time
        long currentTime = System.nanoTime();
        long timespan = currentTime - this.lastTime;
        this.lastTime = currentTime;
        this.setCurrentRow(this.currentRow + (timespan/1e9)* this.rowsPerSecond, false);
    }
}