package io.github.wodt.shadowing;

import it.wldt.adapter.digital.event.DigitalActionWldtEvent;
import it.wldt.adapter.physical.PhysicalAssetDescription;
import it.wldt.adapter.physical.event.PhysicalAssetEventWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetPropertyWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceCreatedWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceDeletedWldtEvent;
import it.wldt.core.model.ShadowingFunction;
import it.wldt.core.state.DigitalTwinStateAction;
import it.wldt.core.state.DigitalTwinStateEvent;
import it.wldt.core.state.DigitalTwinStateEventNotification;
import it.wldt.core.state.DigitalTwinStateProperty;
import it.wldt.exception.EventBusException;

import java.util.Map;

public class MirrorShadowingFunction extends ShadowingFunction {

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
            });

            //Commit state changes.
            this.digitalTwinStateManager.commitStateTransaction();

            //Observe for action calls.
            observeDigitalActionEvents();

            //Notify DT core that the binding has been completed.
            notifyShadowingSync();
        } catch (Exception e) {
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
