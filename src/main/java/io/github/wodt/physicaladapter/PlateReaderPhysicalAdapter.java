package io.github.wodt.physicaladapter;

import it.wldt.adapter.physical.PhysicalAdapter;
import it.wldt.adapter.physical.PhysicalAssetDescription;
import it.wldt.adapter.physical.PhysicalAssetProperty;
import it.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;
import it.wldt.exception.EventBusException;
import it.wldt.exception.PhysicalAdapterException;

import java.util.List;
import java.util.logging.Logger;

public class PlateReaderPhysicalAdapter extends PhysicalAdapter{
    private static final String IDENTIFIER_PROPERTY_KEY = "identifier";

    /**
     * Default constructor.
     */
    public PlateReaderPhysicalAdapter() {
        super("plate-reader-phusical-adapter");
    }

    @Override
    public void onIncomingPhysicalAction(PhysicalAssetActionWldtEvent<?> physicalActionEvent) {

    }

    @Override
    public void onAdapterStart() {
        final PhysicalAssetDescription pad = new PhysicalAssetDescription();
        final PhysicalAssetProperty<String> identifierProperty = new PhysicalAssetProperty<>(
                IDENTIFIER_PROPERTY_KEY, "ABC123");
        pad.getProperties().add(identifierProperty);

        try {
            this.notifyPhysicalAdapterBound(pad);
        } catch (PhysicalAdapterException | EventBusException e) {
            Logger.getAnonymousLogger().warning(e.getMessage());
        }
    }

    @Override
    public void onAdapterStop() {

    }
}