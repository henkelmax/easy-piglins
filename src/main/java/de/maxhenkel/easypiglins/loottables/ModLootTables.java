package de.maxhenkel.easypiglins.loottables;

import com.mojang.serialization.MapCodec;
import de.maxhenkel.easypiglins.EasyPiglinsMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModLootTables {

    private static final
    DeferredRegister<MapCodec<? extends LootItemFunction>> LOOT_FUNCTION_TYPE_REGISTER = DeferredRegister.create(BuiltInRegistries.LOOT_FUNCTION_TYPE, EasyPiglinsMod.MODID);
    public static DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<CopyBlockEntityData>> COPY_BLOCK_ENTITY = LOOT_FUNCTION_TYPE_REGISTER.register("copy_block_entity", () -> CopyBlockEntityData.CODEC);

    public static void init(IEventBus eventBus) {
        LOOT_FUNCTION_TYPE_REGISTER.register(eventBus);
    }
}
