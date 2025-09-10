package io.github.wodt.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/** Record of events from a plate reader in the simulation */
public class PlateReaderRecord {

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    private final String carID;
    private final Date timstamp;

    /**
     * Default constructor.
     * @param carID Plate number of the detected car.
     * @param timestamp Time and date of the event.
     */
    public PlateReaderRecord(String carID, Date timestamp) {
        this.carID = carID;
        this.timstamp = timestamp;
    }

    /**
     * Return car detected.
     * @return Car ID
     */
    public String getCarID() {
        return carID;
    }

    /**
     * Extract record from given JSON string.
     * @param jsonString JSON string.
     * @return Plate reader event.
     */
    public static PlateReaderRecord extractFromJSON(String jsonString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonString);
            String plateNumber = root.get("licencePlate").asText();
            Date timestamp = DATE_FORMAT.parse(root.get("timestamp").asText());
            System.out.println("plate: " + plateNumber + ",timestamp: " + timestamp);
            return new PlateReaderRecord(plateNumber, timestamp);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Function to convert JSON to record.
     * @return Plate reader event.
     */
    public static Function<String, PlateReaderRecord> extractFromJSONFunction() {
        return new Function<String, PlateReaderRecord>() {
            @Override
            public PlateReaderRecord apply(String s) {
                return extractFromJSON(s);
            }
        };
    }
}
