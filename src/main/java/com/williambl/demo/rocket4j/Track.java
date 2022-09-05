package com.williambl.demo.rocket4j;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Track {
    private final List<TrackKey> keys = new ArrayList<>();

    public Track() {
    }

    /**
     * Returns unmodifiable list of TrackKey-objects
     */
    protected List<TrackKey> getKeys() {
        return Collections.unmodifiableList(this.keys);
    }

    /**
     * Returns index for a key that should be used as
     * first interpolation key when calculating value
     * at the given row.
     *
     * Effectively the returned index is either index
     * of TrackKey that is set for the row, or if no exact
     * match is found, it's index of TrackKey that
     * is right before given row (key.row < row) in the
     * keys-array.
     *
     * @return -1 if key not found, otherwise usable key index
     */
    private int getKeyIndex(int row) {
        // TODO: better way to do this search
        int index = Collections.binarySearch(this.keys, new TrackKey(row, 0.f, TrackKey.KeyType.STEP));

        // Positive index is usable as it is to index this.keys.
        // -1 means that index 0 should be used as insertion point ->
        // means that no usable key is found that passes rule
        // key.row < row. It's therefore an error value.
        // If index < -1, a valid key index can be calculated
        // and returned.
        if (index >= 0 || index == -1) {
            return index;
        } else {
            return -index-2;
        }
    }

    /**
     * Return index of key for given row
     * if row contains key.
     */
    private int getKeyIndexExact(int row) {
        int index = this.getKeyIndex(row);

        if (index >= 0 && this.keys.get(index).getRow() == row) {
            return index;
        } else {
            return -1;
        }
    }

    /**
     * Finds key for the given row.
     *
     * If TrackKey object isn't found for the row,
     * the key before the given row is returned.
     *
     * @return TrackKey if found, otherwise null.
     */
    protected @Nullable TrackKey getKey(int row) {
        int index = this.getKeyIndex(row);

        // Exact hit
        if (index >= 0) {
            return this.keys.get(index);
        } else {
            return null;
        }
    }

    /**
     * Inserts new TrackKey or updates an existing one.
     *
     * If row matches, the old key is replaced.
     */
    public void addOrUpdateKey(TrackKey key) {
        int index = this.getKeyIndexExact(key.getRow());

        if (index >= 0) {
            this.keys.set(index, key);
        } else {
            this.keys.add(key);
            Collections.sort(this.keys);
        }
    }

    /**
     * Deletes key from given row if it exists.
     */
    public void deleteKey(int row) {
        int index = this.getKeyIndexExact(row);
        if (index >= 0) {
            this.keys.remove(index);
        }
    }

    /**
     * Returns value of the track for the given
     * (fractional) row.
     *
     * @param   row the row
     * @return  interpolated value for the row
     */
    public double getValue(double row) {
        if (this.keys.size() == 0) {
            return 0.0;
        }

        int irow = (int) row;
        int idx = this.getKeyIndex(irow);

        // Before any keys, return 0
        if (idx == -1) {
            return 0.0;
        } else if (idx == this.keys.size() - 1) { // After last key, return last value
            return this.keys.get(idx).getValue();
        }

        // Between two keys: return interpolated value
        return TrackKey.interpolate(this.keys.get(idx), this.keys.get(idx+1), row);
    }
}
