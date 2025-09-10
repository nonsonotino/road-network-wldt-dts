package io.github.wodt.shadowing;

import io.github.wodt.digitaladapter.CityPhysicalAdapter;
import io.github.wodt.model.PlateReaderRecord;
import it.wldt.adapter.digital.event.DigitalActionWldtEvent;
import it.wldt.adapter.mqtt.physical.MqttPhysicalAdapter;
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
import it.wldt.exception.WldtDigitalTwinStateException;

import java.util.Map;
import java.util.logging.Logger;

public class MirrorShadowingFunction extends ShadowingFunction {

    private PhysicalAssetRelationship<String> detected = new PhysicalAssetRelationship<>("vehicle-detection", "detection");

    public MirrorShadowingFunction(String id) {
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

                //Iterate over Physical Relationships
                pad.getRelationships().forEach(relationship -> {
                    try{
                        if(relationship != null && relationship.getName().equals("vehicle-detection")){
                            DigitalTwinStateRelationship<String> insideInDtStateRelationship = new DigitalTwinStateRelationship<>(relationship.getName(), relationship.getName());
                            this.digitalTwinStateManager.createRelationship(insideInDtStateRelationship);
                            observePhysicalAssetRelationship(relationship);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                });


            });

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
        try {
            //Start State transaction.
            this.digitalTwinStateManager.startStateTransaction();

            System.out.println("CAMBIO COORDS " + physicalPropertyEventMessage.getBody());

            //Update DT property.
            this.digitalTwinStateManager.updateProperty(new DigitalTwinStateProperty<>(
                    physicalPropertyEventMessage.getPhysicalPropertyId(),
                    physicalPropertyEventMessage.getBody()));

            //Commit State changes.
            this.digitalTwinStateManager.commitStateTransaction();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPhysicalAssetEventNotification(PhysicalAssetEventWldtEvent<?> physicalAssetEventWldtEvent) {
        try {
            //Notify the DT components of Event.
            this.digitalTwinStateManager.notifyDigitalTwinStateEvent(new DigitalTwinStateEventNotification<>(
                    physicalAssetEventWldtEvent.getPhysicalEventKey(),
                    physicalAssetEventWldtEvent.getBody(),
                    physicalAssetEventWldtEvent.getCreationTimestamp()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPhysicalAssetRelationshipEstablished(PhysicalAssetRelationshipInstanceCreatedWldtEvent<?> physicalAssetRelationshipWldtEvent) {
        try {
            System.out.println("CIAOOOO");
            if (physicalAssetRelationshipWldtEvent != null
                    && physicalAssetRelationshipWldtEvent.getBody() != null) {
                final PhysicalAssetRelationshipInstance<?> paRelInstance =
                        physicalAssetRelationshipWldtEvent.getBody();

                if (paRelInstance.getTargetId() instanceof String) {
                    final String relName = paRelInstance.getRelationship().getName();
                    final String relKey = paRelInstance.getKey();
                    final String relTargetId = (String) paRelInstance.getTargetId();

                    final DigitalTwinStateRelationshipInstance<String> instance =
                            new DigitalTwinStateRelationshipInstance<>(relName, relTargetId, relKey);

                    this.digitalTwinStateManager.startStateTransaction();
                    this.digitalTwinStateManager.addRelationshipInstance(instance);
                    this.digitalTwinStateManager.commitStateTransaction();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPhysicalAssetRelationshipDeleted(PhysicalAssetRelationshipInstanceDeletedWldtEvent<?> physicalAssetRelationshipWldtEvent) {

    }

    @Override
    protected void onDigitalActionEvent(DigitalActionWldtEvent<?> digitalActionWldtEvent) {
        try {
            //Forward of the action to the Physical Adapter.
            this.publishPhysicalAssetActionWldtEvent(digitalActionWldtEvent.getActionKey(),digitalActionWldtEvent.getBody());
        } catch (EventBusException e) {
            e.printStackTrace();
        }
    }
}
