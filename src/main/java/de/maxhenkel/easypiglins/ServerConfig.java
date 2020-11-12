package de.maxhenkel.easypiglins;

import de.maxhenkel.corelib.config.ConfigBase;
import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig extends ConfigBase {

    public final ForgeConfigSpec.BooleanValue piglinInventorySounds;

    public ServerConfig(ForgeConfigSpec.Builder builder) {
        super(builder);

        piglinInventorySounds = builder
                .comment("If piglins should make sounds while in the players inventory")
                .define("piglin.inventory_sounds", true);
    }

}
