package de.maxhenkel.easypiglins;

import de.maxhenkel.corelib.config.ConfigBase;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig extends ConfigBase {

    public final ModConfigSpec.DoubleValue piglinVolume;

    public ClientConfig(ModConfigSpec.Builder builder) {
        super(builder);

        piglinVolume = builder
                .comment("The volume of every piglin related sound in this mod")
                .defineInRange("piglin.volume", 1D, 0D, 1D);
    }

}
