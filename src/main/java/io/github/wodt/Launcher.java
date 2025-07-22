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

    private static final String CAR1_EXPOSED_PORT = "CAR1_EXPOSED_PORT";
    private static final String MISSION_PLATFORM_URL_VARIABLE = "MISSION_PLATFORM_URL";

    static {
        //Check existence of environment variables.
        Objects.requireNonNull(System.getenv(CAR1_EXPOSED_PORT), "Please provide the Car1 DT exposed port");
        Objects.requireNonNull(System.getenv(MISSION_PLATFORM_URL_VARIABLE), "Please provide the platform url");
    }

    private Launcher() {}

    /**
     * Main class.
     * @param args args.
     */
    public static void main(final String[] args) {
        try {
            System.out.println("Project launched.");

            final int car1PortNumber = Integer.parseInt(System.getenv(CAR1_EXPOSED_PORT));

            //DT creation.
            final String carDTId = "car1-dt";
            final DigitalTwin car1DT = new DigitalTwin(carDTId,
                    new MirrorShadowingFunction("car-shadowing-function"));
            //DT digital adapter.
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

            //ConsoleDigitalAdapter carDigitalAdapter = new ConsoleDigitalAdapter("car1-da");

            //DT mqtt physical adapter.
            MqttPhysicalAdapterConfiguration physicalAdapterConfiguration = MqttPhysicalAdapterConfiguration.builder("localhost", 1883)
                    .addPhysicalAssetPropertyAndTopic("position", new Position(0, 0), "car1", Position.extractFromJSONFunction())
                    .addPhysicalAssetEventAndTopic("movement", " ", "car1/events/movement", Position.extractFromJSONFunction())
                    .build();
            MqttPhysicalAdapter carPhysicalAdapter = new MqttPhysicalAdapter("car-mqtt-pa", physicalAdapterConfiguration);
            //DT adapters initialization.
            car1DT.addDigitalAdapter(car1DigitalAdapter);
            car1DT.addPhysicalAdapter(carPhysicalAdapter);

            //DT engine creation.
            DigitalTwinEngine digitalTwinEngine = new DigitalTwinEngine();

            //Add DTs to Engine.
            digitalTwinEngine.addDigitalTwin(car1DT);

            //Start Engine.
            digitalTwinEngine.startAll();

        } catch (ModelException | WldtDigitalTwinStateException | WldtWorkerException | WldtRuntimeException |
                 EventBusException | WldtConfigurationException | WldtEngineException | MqttException | MqttPhysicalAdapterConfigurationException e) {
            Logger.getLogger(Launcher.class.getName()).info(e.getMessage());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
