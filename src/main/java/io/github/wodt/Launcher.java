package io.github.wodt;

import io.github.webbasedwodt.adapter.WoDTDigitalAdapter;
import io.github.webbasedwodt.adapter.WoDTDigitalAdapterConfiguration;
import io.github.webbasedwodt.model.dtd.DTVersion;
import io.github.wodt.model.PlateReaderRecord;
import io.github.wodt.model.Position;
import io.github.wodt.semantics.CarSemantics;
import io.github.wodt.semantics.PlateReaderSemantics;
import io.github.wodt.shadowing.VehicleShadowingFunction;
import io.github.wodt.shadowing.PRShadowingFunction;
import it.wldt.adapter.mqtt.physical.MqttPhysicalAdapter;
import it.wldt.adapter.mqtt.physical.MqttPhysicalAdapterConfiguration;
import it.wldt.adapter.mqtt.physical.exception.MqttPhysicalAdapterConfigurationException;
import it.wldt.adapter.physical.PhysicalAssetRelationship;
import it.wldt.core.engine.DigitalTwin;
import it.wldt.core.engine.DigitalTwinEngine;
import it.wldt.exception.*;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.URI;
import java.util.Set;
import java.util.logging.Logger;

public final class Launcher {

    //Environment variables names.
    private static final String CAR1_EXPOSED_PORT = "CAR1_EXPOSED_PORT";
    private static final String CAR2_EXPOSED_PORT = "CAR2_EXPOSED_PORT";
    private static final String CAR3_EXPOSED_PORT = "CAR3_EXPOSED_PORT";
    //private static final String MISSION_PLATFORM_URL_VARIABLE = "MISSION_PLATFORM_URL";

    static {
        //Check existence of environment variables.
        /*
        Objects.requireNonNull(System.getenv(CAR1_EXPOSED_PORT), "Please provide the Car1 DT exposed port");
        Objects.requireNonNull(System.getenv(CAR2_EXPOSED_PORT), "Please provide the Car2 DT exposed port");
        Objects.requireNonNull(System.getenv(CAR3_EXPOSED_PORT), "Please provide the Car3 DT exposed port");
        */
        //Objects.requireNonNull(System.getenv(MISSION_PLATFORM_URL_VARIABLE), "Please provide the platform url");
    }

    private Launcher() {}

    /**
     * Main class.
     * @param args args.
     */
    public static void main(final String[] args) {
        try {
            System.out.println("Project launched.");

            //Port numbers.
            /*
            final int car1PortNumber = Integer.parseInt(System.getenv(CAR1_EXPOSED_PORT));
            final int car2PortNumber = Integer.parseInt(System.getenv(CAR2_EXPOSED_PORT));
            final int car3PortNumber = Integer.parseInt(System.getenv(CAR3_EXPOSED_PORT));
             */

            //DT IDs.
            final String car1DTId = "car1-dt";
            final String car2DTId = "car2-dt";
            final String car3DTId = "car3-dt";
            final String prDTId = "pr-dt";

            //DTs initialization.
            final DigitalTwin car1DT = new DigitalTwin(car1DTId,
                    new VehicleShadowingFunction("car1-shadowing-function"));
            final DigitalTwin car2DT = new DigitalTwin(car2DTId,
                    new VehicleShadowingFunction("car2-shadowing-function"));
            final DigitalTwin car3DT = new DigitalTwin(car3DTId,
                    new VehicleShadowingFunction("car3-shadowing-function"));
            final DigitalTwin prDT = new DigitalTwin(prDTId,
                    new PRShadowingFunction("pr-shadowing-function"));

            //DT digital adapter.
            WoDTDigitalAdapter car1DigitalAdapter = new WoDTDigitalAdapter(
                    "car1-da",
                    new WoDTDigitalAdapterConfiguration(
                            URI.create("http://localhost:57382"),
                            new DTVersion(1,0,0),
                            new CarSemantics(),
                            57382,
                            "ABC123",
                            Set.of(URI.create("http://localhost:57381"))
                    ));
            WoDTDigitalAdapter car2DigitalAdapter = new WoDTDigitalAdapter(
                    "car2-da",
                    new WoDTDigitalAdapterConfiguration(
                            URI.create("http://localhost:57383"),
                            new DTVersion(1,0,0),
                            new CarSemantics(),
                            57383,
                            "ABC124",
                            Set.of(URI.create("http://localhost:57381"))
                    ));
            WoDTDigitalAdapter car3DigitalAdapter = new WoDTDigitalAdapter(
                    "car3-da",
                    new WoDTDigitalAdapterConfiguration(
                            URI.create("http://localhost:57384"),
                            new DTVersion(1,0,0),
                            new CarSemantics(),
                            57384,
                            "ABC125",
                            Set.of(URI.create("http://localhost:57381"))
                    ));
            WoDTDigitalAdapter prDigitalAdapter = new WoDTDigitalAdapter(
                    "pr-da",
                    new WoDTDigitalAdapterConfiguration(
                            URI.create("http://localhost:57399"),
                            new DTVersion(1,0,0),
                            new PlateReaderSemantics(),
                            57399,
                            "PR123",
                            Set.of(URI.create("http://localhost:57381"))
            ));

            //DT mqtt physical adapter.
            //Car 1.
            MqttPhysicalAdapterConfiguration car1PhysicalAdapterConfiguration = MqttPhysicalAdapterConfiguration.builder("localhost", 1883)
                    .addPhysicalAssetPropertyAndTopic("position", new Position(0, 0), "car1/events/movement", Position.extractFromJSONFunction())
                    //.addPhysicalAssetEventAndTopic("movement", " ", "car1/events/movement", Position.extractFromJSONFunction())
                    .build();
            MqttPhysicalAdapter car1PhysicalAdapter = new MqttPhysicalAdapter("car1-mqtt-pa", car1PhysicalAdapterConfiguration);
            //Car 2.
            MqttPhysicalAdapterConfiguration car2PhysicalAdapterConfiguration = MqttPhysicalAdapterConfiguration.builder("localhost", 1883)
                    .addPhysicalAssetPropertyAndTopic("position", new Position(0, 0), "car2/events/movement", Position.extractFromJSONFunction())
                    //.addPhysicalAssetEventAndTopic("movement", " ", "car2/events/movement", Position.extractFromJSONFunction())
                    .build();
            MqttPhysicalAdapter car2PhysicalAdapter = new MqttPhysicalAdapter("car2-mqtt-pa", car2PhysicalAdapterConfiguration);
            //Car3.
            MqttPhysicalAdapterConfiguration car3PhysicalAdapterConfiguration = MqttPhysicalAdapterConfiguration.builder("localhost", 1883)
                    .addPhysicalAssetPropertyAndTopic("position", new Position(0, 0), "car3/events/movement", Position.extractFromJSONFunction())
                    //.addPhysicalAssetEventAndTopic("movement", " ", "car3/events/movement", Position.extractFromJSONFunction())
                    .build();
            MqttPhysicalAdapter car3PhysicalAdapter = new MqttPhysicalAdapter("car3-mqtt-pa", car3PhysicalAdapterConfiguration);
            //Plate reader.
            MqttPhysicalAdapterConfiguration ptPhysicalAdapterConfiguration = MqttPhysicalAdapterConfiguration.builder("localhost", 1883)
                    .addPhysicalAssetEventAndTopic("car-detection", "detection", "plateReader1/events/car_detected", PlateReaderRecord.extractFromJSONFunction())
                    .build();
            ptPhysicalAdapterConfiguration.getPhysicalAssetDescription().getRelationships().add(new PhysicalAssetRelationship<>("vehicle-detection", "detection"));
            MqttPhysicalAdapter prPhysicalAdapter = new MqttPhysicalAdapter("pr-mqtt-pa", ptPhysicalAdapterConfiguration);

            //DT adapters configuration.
            //Digital
            car1DT.addDigitalAdapter(car1DigitalAdapter);
            car2DT.addDigitalAdapter(car2DigitalAdapter);
            car3DT.addDigitalAdapter(car3DigitalAdapter);
            prDT.addDigitalAdapter(prDigitalAdapter);

            //Physical
            car1DT.addPhysicalAdapter(car1PhysicalAdapter);
            car2DT.addPhysicalAdapter(car2PhysicalAdapter);
            car3DT.addPhysicalAdapter(car3PhysicalAdapter);
            prDT.addPhysicalAdapter(prPhysicalAdapter);

            //DT engine creation.
            DigitalTwinEngine digitalTwinEngine = new DigitalTwinEngine();

            //Add DTs to Engine.
            digitalTwinEngine.addDigitalTwin(car1DT);
            digitalTwinEngine.addDigitalTwin(car2DT);
            digitalTwinEngine.addDigitalTwin(car3DT);
            digitalTwinEngine.addDigitalTwin(prDT);

            //Start Engine.
            digitalTwinEngine.startDigitalTwin(car1DTId);
            digitalTwinEngine.startDigitalTwin(car2DTId);
            digitalTwinEngine.startDigitalTwin(car3DTId);
            digitalTwinEngine.startDigitalTwin(prDTId);

        } catch (ModelException | WldtDigitalTwinStateException | WldtWorkerException | WldtRuntimeException |
                 EventBusException | WldtConfigurationException | WldtEngineException | MqttException | MqttPhysicalAdapterConfigurationException e) {
            Logger.getLogger(Launcher.class.getName()).info(e.getMessage());
        }
    }
}
