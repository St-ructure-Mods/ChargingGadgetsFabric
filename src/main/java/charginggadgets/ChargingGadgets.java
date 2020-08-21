package charginggadgets;

import charginggadgets.config.CGConfig;
import charginggadgets.init.CGContent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reborncore.common.config.Configuration;

public class ChargingGadgets implements ModInitializer {
	public final static String MOD_ID = "charginggadgets";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static ChargingGadgets INSTANCE;

    public static ItemGroup ITEMGROUP = FabricItemGroupBuilder.build(
            new Identifier(MOD_ID, "item_group"),
            () -> new ItemStack(CGContent.Machine.CHARGING_STATION)
    );

    @Override
	public void onInitialize() {
        INSTANCE = this;
        new Configuration(CGConfig.class, "charging_gadgets");



    }
}
