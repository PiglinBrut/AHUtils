package ru.pb.ahutils.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.pb.ahutils.AHUtils;

public class TabRegistry {
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AHUtils.MOD_ID);

    public static void register(IEventBus eventBus) {
        TABS.register(eventBus);
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EQUIPMENT_TAB =
            TABS.register("ahutils_content", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.ahutils.ahutils_content")).icon(() -> new ItemStack(ItemRegistry.EARTHQUAKE_HAMMER.get())).displayItems((enabledFeatures, entries) -> {
                entries.accept(ItemRegistry.EARTHQUAKE_HAMMER.get());
                entries.accept(ItemRegistry.EARTH_UPGRADE_ORB.get());
                entries.accept(ItemRegistry.EARTH_RUNE.get());
                entries.accept(ItemRegistry.SCULPTOR_STAFF.get());
                entries.accept(ItemRegistry.GEOMANCER_BEADS.get());
                entries.accept(ItemRegistry.GEOMANCER_ROBE.get());
                entries.accept(ItemRegistry.GEOMANCER_BELT.get());
                entries.accept(ItemRegistry.GEOMANCER_SANDALS.get());
                entries.accept(ItemRegistry.AIR_UPGRADE_ORB.get());
                entries.accept(ItemRegistry.AIR_RUNE.get());
            }).withTabsBefore(new ResourceKey[]{CreativeModeTabs.SPAWN_EGGS}).build());
}
