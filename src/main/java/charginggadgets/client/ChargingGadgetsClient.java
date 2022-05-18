package charginggadgets.client;

import net.fabricmc.api.ClientModInitializer;

public class ChargingGadgetsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientGuiType.validate();
    }
}
