package de.maxhenkel.easypiglins;

import de.maxhenkel.corelib.config.ConfigBase;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig extends ConfigBase {

    public final ModConfigSpec.BooleanValue piglinInventorySounds;

    public ServerConfig(ModConfigSpec.Builder builder) {
        super(builder);

        piglinInventorySounds = builder
                .comment("If piglins should make sounds while in the players inventory")
                .define("piglin.inventory_sounds", true);
    }

}
