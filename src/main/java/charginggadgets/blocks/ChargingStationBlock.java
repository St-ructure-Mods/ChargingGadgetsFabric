package charginggadgets.blocks;

import charginggadgets.blockentity.ChargingStationBlockEntity;
import charginggadgets.client.GuiType;

public class ChargingStationBlock extends GenericMachineBlock {
    public ChargingStationBlock() {
        super(GuiType.CHARGING_STATION, ChargingStationBlockEntity::new);
    }
}
