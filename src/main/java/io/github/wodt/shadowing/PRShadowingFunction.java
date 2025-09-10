package io.github.wodt.shadowing;

import io.github.wodt.model.PlateReaderRecord;
import it.wldt.adapter.digital.DigitalAdapter;
import it.wldt.adapter.digital.event.DigitalActionWldtEvent;
import it.wldt.adapter.physical.PhysicalAssetDescription;
import it.wldt.adapter.physical.PhysicalAssetRelationship;
import it.wldt.adapter.physical.PhysicalAssetRelationshipInstance;
import it.wldt.adapter.physical.event.PhysicalAssetEventWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetPropertyWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceCreatedWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceDeletedWldtEvent;
import it.wldt.core.model.ShadowingFunction;
import it.wldt.core.state.*;
import it.wldt.exception.EventBusException;
import it.wldt.exception.ModelException;
import it.wldt.exception.WldtDigitalTwinStateException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class PRShadowingFunction extends ShadowingFunction {

    //HTTP client for REST API calls.
    HttpClient requestClient = HttpClient.newHttpClient();

    //Phisical asset relationships.
    private PhysicalAssetRelationship<String> detected = new PhysicalAssetRelationship<>("vehicle-detection", "detection");

    /**
     * Default Constructor
     *
     * @param id Unique Identifier of the Shadowing Model Function
     */
    public PRShadowingFunction(String id) {
        super(id);
    }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onDigitalTwinBound(Map<String, PhysicalAssetDescription> adaptersPhysicalAssetDescriptionMap) {
        try{
            //Start state transaction.
            this.digitalTwinStateManager.startStateTransaction();

            //Iterate the received PAD.
            adaptersPhysicalAssetDescriptionMap.values().forEach(pad->{

                //Iterate all Properties.
                pad.getProperties().forEach(property->{
                    try {
                        //Create and Write new property on the DT state.
                        this.digitalTwinStateManager.createProperty(new DigitalTwinStateProperty<>(property.getKey(), property.getInitialValue()));

                        //Start observing property.
                        this.observePhysicalAssetProperty(property);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                //Iterate all Events.
                pad.getEvents().forEach(event->{
                    try {
                        //Initialize new Event.
                        DigitalTwinStateEvent dtStateEvent = new DigitalTwinStateEvent(event.getKey(), event.getType());

                        //Create and write Event on the DT state.
                        this.digitalTwinStateManager.registerEvent(dtStateEvent);

                        //Start observing Event.
                        this.observePhysicalAssetEvent(event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                //Iterate all Actions.
                pad.getActions().forEach(action->{
                    try {
                        //Instance a new Action.
                        DigitalTwinStateAction dtStateAction = new DigitalTwinStateAction(action.getKey(), action.getType(), action.getContentType());

                        //Enable action on DT state.
                        this.digitalTwinStateManager.enableAction(dtStateAction);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            });

            try {
                DigitalTwinStateRelationship detectionRel = new DigitalTwinStateRelationship<>("vehicle-detection", "detection");
                this.digitalTwinStateManager.createRelationship(detectionRel);
                observePhysicalAssetRelationship(detected);


            } catch (WldtDigitalTwinStateException e) {
                throw new RuntimeException(e);
            } catch (ModelException e) {
                throw new RuntimeException(e);
            } catch (EventBusException e) {
                throw new RuntimeException(e);
            }

            //Commit state changes.
            this.digitalTwinStateManager.commitStateTransaction();

            //Observe for action calls.
            observeDigitalActionEvents();

            //Notify DT core that the binding has been completed.
            notifyShadowingSync();
        }
        catch (WldtDigitalTwinStateException e){
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDigitalTwinUnBound(Map<String, PhysicalAssetDescription> adaptersPhysicalAssetDescriptionMap, String errorMessage) {

    }

    @Override
    protected void onPhysicalAdapterBidingUpdate(String adapterId, PhysicalAssetDescription adapterPhysicalAssetDescription) {

    }

    @Override
    protected void onPhysicalAssetPropertyVariation(PhysicalAssetPropertyWldtEvent<?> physicalPropertyEventMessage) {

    }

    @Override
    protected void onPhysicalAssetEventNotification(PhysicalAssetEventWldtEvent<?> physicalAssetEventWldtEvent) {
        try {
            if(physicalAssetEventWldtEvent.getBody() != null) {

                //Event received.
                PlateReaderRecord record = (PlateReaderRecord)physicalAssetEventWldtEvent.getBody();

                //Request all DTs with this licence plate.
                HttpRequest APIRequest = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:57381/wodt/directory?pa="+record.getCarID()))
                        .GET()
                        .build();
                HttpResponse<String> APIResponse = requestClient.send(APIRequest, HttpResponse.BodyHandlers.ofString());
                System.out.println("APIRESPONSE-BODY: " + APIResponse.body());



                //Physical event instance.
                PhysicalAssetRelationshipInstanceCreatedWldtEvent < ?>physicalAssetRelationshipWldtEvent =
                new PhysicalAssetRelationshipInstanceCreatedWldtEvent<>(detected.createRelationshipInstance(
                        "ID", new HashMap<>()
                ));


                System.out.println("CIAOOOO" + physicalAssetRelationshipWldtEvent.getBody());
                if (physicalAssetRelationshipWldtEvent != null
                        && physicalAssetRelationshipWldtEvent.getBody() != null) {
                    final PhysicalAssetRelationshipInstance<?> paRelInstance =
                            physicalAssetRelationshipWldtEvent.getBody();

                    if (paRelInstance.getTargetId() instanceof String) {
                        final String relName = paRelInstance.getRelationship().getName();
                        final String relKey = paRelInstance.getKey();
                        final String relTargetId = (String) paRelInstance.getTargetId();

                        System.out.println(relName + relKey + relTargetId);

                        final DigitalTwinStateRelationshipInstance<String> instance =
                                new DigitalTwinStateRelationshipInstance<>(relName, relTargetId, relKey);

                        this.digitalTwinStateManager.startStateTransaction();
                        this.digitalTwinStateManager.addRelationshipInstance(instance);
                        this.digitalTwinStateManager.commitStateTransaction();

                        System.out.println("STATE: " + this.digitalTwinStateManager.getDigitalTwinState());

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPhysicalAssetRelationshipEstablished(PhysicalAssetRelationshipInstanceCreatedWldtEvent<?> physicalAssetRelationshipWldtEvent) {

    }

    @Override
    protected void onPhysicalAssetRelationshipDeleted(PhysicalAssetRelationshipInstanceDeletedWldtEvent<?> physicalAssetRelationshipWldtEvent) {

    }

    @Override
    protected void onDigitalActionEvent(DigitalActionWldtEvent<?> digitalActionWldtEvent) {

    }
}
