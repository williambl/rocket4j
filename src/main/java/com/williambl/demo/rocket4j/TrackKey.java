package com.williambl.demo.rocket4j;

import java.util.function.DoubleUnaryOperator;

public class TrackKey implements Comparable<TrackKey> {
    /**
     * Interpolation type.
     *
     * Note: it's important that enum's
     * ordinal values map to the same values on
     * Rocket's side!
     */
    public enum KeyType {
        /**
         * No interpolation, just steps to next value.
         */
        STEP($ -> 0.0),
        /**
         * Linear 
         */
        LINEAR(t -> t),
        /**
         * Smooth
         */
        SMOOTH(t -> t * t * (3 - 2*t)),
        /**
         * Ramp up
         */
        RAMP(t -> Math.pow(t, 2.0));

        private final DoubleUnaryOperator function;

        KeyType(DoubleUnaryOperator function) {
            this.function = function;
        }

        public double apply(double rawFactor) {
            return this.function.applyAsDouble(rawFactor);
        }
    }

    private int row;
    private float value;
    private KeyType keyType;

    public TrackKey(int row, float value, KeyType keyType) {
        this.row = row;
        this.value = value;
        this.keyType = keyType;
    }

    public TrackKey(int row, float value, int keyType) {
        this(row, value, KeyType.values()[keyType]);
    }

    public int getRow() {
        return this.row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public float getValue() {
        return this.value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public KeyType getKeyType() {
        return this.keyType;
    }

    public void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }

    /**
     * Keeps tracks sorted in ascending order by 
     * the row value.
     */
    @Override
    public int compareTo(TrackKey other) {
        return Integer.compare(this.row, other.row);
    }

    public String toString() {
        return String.format("TrackKey[row=%d value=%f type=%s]", this.row, this.value, this.keyType);
    }

    /**
     * Interpolates value for row between two TrackKey-objects.
     *
     * Interpolation type depends on the first key; only the value is
     * used from the second key in the process.
     *
     * Row can naturally be fractional, so interpolation between
     * values flowing in time is continuous.
     */
    public static double interpolate(TrackKey first, TrackKey second, double row) {
        double t = (row - first.row) / (second.row - first.row);

        t = first.keyType.apply(t);

        return first.value + (second.value - first.value) * t;
    }
}