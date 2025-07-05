package de.maxhenkel.easypiglins.gui;

import de.maxhenkel.easypiglins.EasyPiglinsMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Containers {

    private static final DeferredRegister<MenuType<?>> MENU_TYPE_REGISTER = DeferredRegister.create(BuiltInRegistries.MENU, EasyPiglinsMod.MODID);
    public static final DeferredHolder<MenuType<?>, MenuType<BartererContainer>> BREEDER_CONTAINER = MENU_TYPE_REGISTER.register("barterer", () ->
            IMenuTypeExtension.create((windowId, inv, data) -> new BartererContainer(windowId, inv))
    );

    public static void init(IEventBus eventBus) {
        MENU_TYPE_REGISTER.register(eventBus);
    }

    public static void initClient(IEventBus eventBus) {
        eventBus.addListener(Containers::onRegisterScreens);
    }

    public static void onRegisterScreens(RegisterMenuScreensEvent containers) {
        containers.<BartererContainer, BartererScreen>register(BREEDER_CONTAINER.get(), BartererScreen::new);
    }

}
