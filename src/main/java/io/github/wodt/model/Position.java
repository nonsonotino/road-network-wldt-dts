package io.github.wodt.model;

/** A set of coordinates inside the world */
public class Position {
    private final double latitude;
    private final double longitude;

    /**
     * Default Position constructor.
     * @param latitude Latitude.
     * @param longitude Longitude.
     */
    public Position(final double latitude, final double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Returns the latitude.
     * @return Latitude.
     */
    public double getLatitude() {
        return this.latitude;
    }

    /**
     * Returns the longitude.
     * @return Longitude.
     */
    public double getLongitude() {
        return this.longitude;
    }
}
