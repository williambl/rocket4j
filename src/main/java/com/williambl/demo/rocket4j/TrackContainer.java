package com.williambl.demo.rocket4j;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class TrackContainer {
    private final LinkedHashMap<String, Track> tracks = new LinkedHashMap<>();
    private final Rocket4J rocket;

    public TrackContainer(Rocket4J rocket) {
        this.rocket = rocket;
    }

    public Track get(String name) {
        return this.tracks.get(name);
    }

    /**
     * Fetch track by it's zero-based insertion id.
     *
     * Tracks are in same order as they were added.
     * This directly maps to Rocket's insertion order and
     * the method is intended to be used when Rocket sends
     * SET_KEY, which contains id of the track.
     */
    public Track getById(int id) {
        return this.tracks.values().stream().toList().get(id);
    }

    /**
     * Returns track with the given name or creates
     * a new track if it doesn't exist.
     *
     * @param name name of Track to get or create
     * @return Track object
     */
    public @NotNull Track getOrCreate(String name) {
        Track track = this.get(name);

        // Not in map, create new
        if (track == null) {
            track = new Track();
            this.tracks.put(name, track);
            this.rocket.requestTrack(this, name);
        }

        return track;
    }

    /**
     * Deletes a track.
     *
     * If track is not found, does nothing.
     *
     * @param name track to delete
     */
    public void delete(String name) {
        if (this.tracks.get(name) != null) {
            this.tracks.remove(name);
        }
    }

    /**
     * Returns unmodifiable list of Track-objects.
     */
    public List<Track> getAll() {
        return this.tracks.values().stream().toList();
    }
}
