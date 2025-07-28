package io.github.wodt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.webbasedwodt.adapter.WoDTDigitalAdapter;
import io.github.webbasedwodt.adapter.WoDTDigitalAdapterConfiguration;
import io.github.webbasedwodt.model.dtd.DTVersion;
import io.github.wodt.digitaladapter.ConsoleDigitalAdapter;
import io.github.wodt.model.Position;
import io.github.wodt.semantics.CarSemantics;
import io.github.wodt.shadowing.MirrorShadowingFunction;
import it.wldt.adapter.mqtt.physical.MqttPhysicalAdapter;
import it.wldt.adapter.mqtt.physical.MqttPhysicalAdapterConfiguration;
import it.wldt.adapter.mqtt.physical.exception.MqttPhysicalAdapterConfigurationException;
import it.wldt.adapter.mqtt.physical.topic.incoming.DigitalTwinIncomingTopic;
import it.wldt.core.engine.DigitalTwin;
import it.wldt.core.engine.DigitalTwinEngine;
import it.wldt.exception.*;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;

public final class Launcher {

    //Environment variables names.
    private static final String CAR1_EXPOSED_PORT = "CAR1_EXPOSED_PORT";
    private static final String CAR2_EXPOSED_PORT = "CAR2_EXPOSED_PORT";
    private static final String CAR3_EXPOSED_PORT = "CAR3_EXPOSED_PORT";
    //private static final String MISSION_PLATFORM_URL_VARIABLE = "MISSION_PLATFORM_URL";

    static {
        //Check existence of environment variables.
        Objects.requireNonNull(System.getenv(CAR1_EXPOSED_PORT), "Please provide the Car1 DT exposed port");
        Objects.requireNonNull(System.getenv(CAR2_EXPOSED_PORT), "Please provide the Car2 DT exposed port");
        Objects.requireNonNull(System.getenv(CAR3_EXPOSED_PORT), "Please provide the Car3 DT exposed port");
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
            final int car1PortNumber = Integer.parseInt(System.getenv(CAR1_EXPOSED_PORT));
            final int car2PortNumber = Integer.parseInt(System.getenv(CAR2_EXPOSED_PORT));
            final int car3PortNumber = Integer.parseInt(System.getenv(CAR3_EXPOSED_PORT));

            //DT IDs.
            final String car1DTId = "car1-dt";
            final String car2DTId = "car2-dt";
            final String car3DTId = "car3-dt";

            //DTs initialization.
            final DigitalTwin car1DT = new DigitalTwin(car1DTId,
                    new MirrorShadowingFunction("car1-shadowing-function"));
            final DigitalTwin car2DT = new DigitalTwin(car2DTId,
                    new MirrorShadowingFunction("car2-shadowing-function"));
            final DigitalTwin car3DT = new DigitalTwin(car3DTId,
                    new MirrorShadowingFunction("car3-shadowing-function"));

            //DT digital adapter.
            /* TODO: reinitialize to WoDT DA.
            WoDTDigitalAdapter car1DigitalAdapter = new WoDTDigitalAdapter(
                    "car1-DA",
                    new WoDTDigitalAdapterConfiguration(
                            new URI("http://localhost:" + car1PortNumber),
                            new DTVersion(1,0,0),
                            new CarSemantics(),
                            car1PortNumber,
                            "car1PA",
                            Set.of()
                    )
            );
            */
            ConsoleDigitalAdapter car1DigitalAdapter = new ConsoleDigitalAdapter("car1-da");
            ConsoleDigitalAdapter car2DigitalAdapter = new ConsoleDigitalAdapter("car1-da");
            ConsoleDigitalAdapter car3DigitalAdapter = new ConsoleDigitalAdapter("car1-da");

            //DT mqtt physical adapter.
            //Car 1.
            MqttPhysicalAdapterConfiguration physicalAdapterConfiguration = MqttPhysicalAdapterConfiguration.builder("localhost", 1883)
                    .addPhysicalAssetPropertyAndTopic("position", new Position(0, 0), "car1", Position.extractFromJSONFunction())
                    .addPhysicalAssetEventAndTopic("movement", " ", "car1/events/movement", Position.extractFromJSONFunction())
                    .build();
            MqttPhysicalAdapter car1PhysicalAdapter = new MqttPhysicalAdapter("car1-mqtt-pa", physicalAdapterConfiguration);
            //Car 2.
            physicalAdapterConfiguration = MqttPhysicalAdapterConfiguration.builder("localhost", 1883)
                    .addPhysicalAssetPropertyAndTopic("position", new Position(0, 0), "car2", Position.extractFromJSONFunction())
                    .addPhysicalAssetEventAndTopic("movement", " ", "car2/events/movement", Position.extractFromJSONFunction())
                    .build();
            MqttPhysicalAdapter car2PhysicalAdapter = new MqttPhysicalAdapter("car2-mqtt-pa", physicalAdapterConfiguration);
            //Car3.
            physicalAdapterConfiguration = MqttPhysicalAdapterConfiguration.builder("localhost", 1883)
                    .addPhysicalAssetPropertyAndTopic("position", new Position(0, 0), "car3", Position.extractFromJSONFunction())
                    .addPhysicalAssetEventAndTopic("movement", " ", "car3/events/movement", Position.extractFromJSONFunction())
                    .build();
            MqttPhysicalAdapter car3PhysicalAdapter = new MqttPhysicalAdapter("car3-mqtt-pa", physicalAdapterConfiguration);

            //DT adapters configuration.
            car1DT.addDigitalAdapter(car1DigitalAdapter);
            car2DT.addDigitalAdapter(car2DigitalAdapter);
            car3DT.addDigitalAdapter(car3DigitalAdapter);
            car1DT.addPhysicalAdapter(car1PhysicalAdapter);
            car2DT.addPhysicalAdapter(car2PhysicalAdapter);
            car3DT.addPhysicalAdapter(car3PhysicalAdapter);

            //DT engine creation.
            DigitalTwinEngine digitalTwinEngine = new DigitalTwinEngine();

            //Add DTs to Engine.
            digitalTwinEngine.addDigitalTwin(car1DT);
            digitalTwinEngine.addDigitalTwin(car2DT);
            digitalTwinEngine.addDigitalTwin(car3DT);

            //Start Engine.
            digitalTwinEngine.startAll();

        } catch (ModelException | WldtDigitalTwinStateException | WldtWorkerException | WldtRuntimeException |
                 EventBusException | WldtConfigurationException | WldtEngineException | MqttException | MqttPhysicalAdapterConfigurationException e) {
            Logger.getLogger(Launcher.class.getName()).info(e.getMessage());
        }
    }
}
