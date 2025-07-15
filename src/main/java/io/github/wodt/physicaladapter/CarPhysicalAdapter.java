package io.github.wodt.physicaladapter;

import io.github.wodt.model.Position;
import it.wldt.adapter.physical.PhysicalAdapter;
import it.wldt.adapter.physical.PhysicalAssetDescription;
import it.wldt.adapter.physical.PhysicalAssetProperty;
import it.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;
import it.wldt.exception.EventBusException;
import it.wldt.exception.PhysicalAdapterException;

public class CarPhysicalAdapter extends PhysicalAdapter {

    //Physical adapter elements keys.
    private final static String POSITION_PROPERTY_KEY = "position-property-key";
    private final static String MOVEMENT_ACTION_KEY = "movement-action-key";

    /**
     * Default constructor.
     * @param id Adapter identifier.
     */
    public CarPhysicalAdapter(String id) {
        super(id);
    }

    @Override
    public void onIncomingPhysicalAction(PhysicalAssetActionWldtEvent<?> physicalActionEvent) {
        try{
            if (physicalActionEvent != null
                && physicalActionEvent.getActionKey().equals(MOVEMENT_ACTION_KEY)
                && physicalActionEvent.getBody() instanceof String) {
                System.out.println("Received Action Request: " + physicalActionEvent.getActionKey()
                        + " with Body: " + physicalActionEvent.getBody());
            } else {
                System.err.println("Wrong Action received.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Define behavior on adapter start.
     */
    @Override
    public void onAdapterStart() {
        try {
            //Create PAD.
            PhysicalAssetDescription pad = new PhysicalAssetDescription();

            //Add the position property.
            PhysicalAssetProperty<Position> positionProperty =
                    new PhysicalAssetProperty<Position>
                    (POSITION_PROPERTY_KEY, new Position(0,0));
            pad.getProperties().add(positionProperty);

            //Notify the new PAD to the DT's shadowing function.
            this.notifyPhysicalAdapterBound(pad);
        } catch (PhysicalAdapterException | EventBusException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAdapterStop() {

    }
}
