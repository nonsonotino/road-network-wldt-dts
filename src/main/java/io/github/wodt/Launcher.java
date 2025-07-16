package io.github.wodt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wodt.digitaladapter.ConsoleDigitalAdapter;
import io.github.wodt.model.Position;
import io.github.wodt.shadowing.MirrorShadowingFunction;
import it.wldt.adapter.mqtt.physical.MqttPhysicalAdapter;
import it.wldt.adapter.mqtt.physical.MqttPhysicalAdapterConfiguration;
import it.wldt.adapter.mqtt.physical.exception.MqttPhysicalAdapterConfigurationException;
import it.wldt.adapter.mqtt.physical.topic.incoming.DigitalTwinIncomingTopic;
import it.wldt.core.engine.DigitalTwin;
import it.wldt.core.engine.DigitalTwinEngine;
import it.wldt.exception.*;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.function.Function;
import java.util.logging.Logger;

public final class Launcher {

    private Launcher() {}

    /**
     * Main class.
     * @param args args.
     */
    public static void main(final String[] args) {
        try {
            System.out.println("Project launched.");

            //DT creation.
            final String carDTId = "car1-dt";
            final DigitalTwin carDT = new DigitalTwin(carDTId,
                    new MirrorShadowingFunction("car-shadowing-function"));
            //DT digital adapter.
            ConsoleDigitalAdapter carDigitalAdapter = new ConsoleDigitalAdapter("car1-da");
            //DT mqtt physical adapter.
            MqttPhysicalAdapterConfiguration config = MqttPhysicalAdapterConfiguration.builder("localhost", 1883)
                    .addPhysicalAssetPropertyAndTopic("position", new Position(0, 0), "car1", Position.extractFromJSONFunction())
                    .addPhysicalAssetEventAndTopic("movement", " ", "car1/events/movement", Position.extractFromJSONFunction())
                    .build();
            MqttPhysicalAdapter carPhysicalAdapter = new MqttPhysicalAdapter("car-mqtt-pa", config);
            //DT adapters initialization.
            carDT.addDigitalAdapter(carDigitalAdapter);
            carDT.addPhysicalAdapter(carPhysicalAdapter);

            //DT engine creation.
            DigitalTwinEngine digitalTwinEngine = new DigitalTwinEngine();

            //Add DTs to Engine.
            digitalTwinEngine.addDigitalTwin(carDT);

            //Start Engine.
            digitalTwinEngine.startAll();

        } catch (ModelException | WldtDigitalTwinStateException | WldtWorkerException | WldtRuntimeException |
                 EventBusException | WldtConfigurationException | WldtEngineException | MqttException | MqttPhysicalAdapterConfigurationException e) {
            Logger.getLogger(Launcher.class.getName()).info(e.getMessage());
        }
    }
}
