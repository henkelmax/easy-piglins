package de.maxhenkel.easypiglins.gui;

import de.maxhenkel.corelib.ClientRegistry;
import de.maxhenkel.easypiglins.Main;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Containers {

    private static final DeferredRegister<MenuType<?>> MENU_TYPE_REGISTER = DeferredRegister.create(BuiltInRegistries.MENU, Main.MODID);
    public static final DeferredHolder<MenuType<?>, MenuType<BartererContainer>> BREEDER_CONTAINER = MENU_TYPE_REGISTER.register("barterer", () ->
            IMenuTypeExtension.create((windowId, inv, data) -> new BartererContainer(windowId, inv))
    );

    public static void init() {
        MENU_TYPE_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetup() {
        ClientRegistry.<BartererContainer, BartererScreen>registerScreen(BREEDER_CONTAINER.get(), BartererScreen::new);
    }

}
