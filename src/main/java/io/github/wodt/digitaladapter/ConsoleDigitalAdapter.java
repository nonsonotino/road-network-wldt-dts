package io.github.wodt.digitaladapter;

import it.wldt.adapter.digital.DigitalAdapter;
import it.wldt.core.state.DigitalTwinState;
import it.wldt.core.state.DigitalTwinStateEventNotification;

import java.util.ArrayList;

public class ConsoleDigitalAdapter extends DigitalAdapter {

    public ConsoleDigitalAdapter(String id) {
        super(id);
    }

    @Override
    protected void onStateUpdate(DigitalTwinState newDigitalTwinState, DigitalTwinState previousDigitalTwinState, ArrayList digitalTwinStateChangeList) {
        System.out.println("State update: " + newDigitalTwinState);
    }

    @Override
    protected void onEventNotificationReceived(DigitalTwinStateEventNotification digitalTwinStateEventNotification) {

    }

    @Override
    public void onAdapterStart() {

    }

    @Override
    public void onAdapterStop() {

    }

    @Override
    public void onDigitalTwinSync(DigitalTwinState digitalTwinState) {

    }

    @Override
    public void onDigitalTwinUnSync(DigitalTwinState digitalTwinState) {

    }

    @Override
    public void onDigitalTwinCreate() {

    }

    @Override
    public void onDigitalTwinStart() {

    }

    @Override
    public void onDigitalTwinStop() {

    }

    @Override
    public void onDigitalTwinDestroy() {

    }
}
