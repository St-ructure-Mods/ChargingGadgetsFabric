package charginggadgets.config;

import reborncore.common.config.Config;

public class CGConfig {

    @Config(config = "machines", category = "charging_station", key = "ChargingStationMaxOutput", comment = "Charging Station Max Output (Value in EU)")
    public static int chargingStationMaxOutput = 10_000;

    @Config(config = "machines", category = "charging_station", key = "ChargingStationMaxInput", comment = "Charging Station Max Input (Value in EU)")
    public static int chargingStationMaxInput = 10_000;

    @Config(config = "machines", category = "charging_station", key = "ChargingStationMaxEnergy", comment = "Charging Station Max Energy (Value in EU)")
    public static int chargingStationMaxEnergy = 10_000_000;

}
