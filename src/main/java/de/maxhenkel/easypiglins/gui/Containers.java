package de.maxhenkel.easypiglins.gui;

import de.maxhenkel.corelib.ClientRegistry;
import de.maxhenkel.easypiglins.Main;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;

public class Containers {

    public static ContainerType<BartererContainer> BREEDER_CONTAINER;

    @OnlyIn(Dist.CLIENT)
    public static void clientSetup() {
        ClientRegistry.<BartererContainer, BartererScreen>registerScreen(BREEDER_CONTAINER, BartererScreen::new);
    }

    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
        BREEDER_CONTAINER = new ContainerType<>(BartererContainer::new);
        BREEDER_CONTAINER.setRegistryName(new ResourceLocation(Main.MODID, "barterer"));
        event.getRegistry().register(BREEDER_CONTAINER);
    }

}
