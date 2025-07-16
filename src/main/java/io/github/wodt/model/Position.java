package io.github.wodt.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.function.Function;

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

    /**
     * Returns the Position from a given jsonString.
     * @param jsonString Json string.
     * @return The position, if found.
     */
    public static Position extractFromJSON(String jsonString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonString);
            JsonNode coordinatesNode = root.get("coords");
            double x = coordinatesNode.get("x").asDouble();
            double y = coordinatesNode.get("y").asDouble();
            System.out.println("Coordinates: "+ x +", " +y );
            return new Position(x, y);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Function<String, Position> extractFromJSONFunction() {
        return new Function<String, Position>() {
            @Override
            public Position apply(String s) {
                return extractFromJSON(s);
            }
        };
    }
}
