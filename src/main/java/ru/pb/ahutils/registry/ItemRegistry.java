package ru.pb.ahutils.registry;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.pb.ahutils.AHUtils;
import ru.pb.ahutils.util.tools.EarthquakeHammer;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AHUtils.MOD_ID);
    public static  void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static final DeferredItem<Item> EARTHQUAKE_HAMMER = ITEMS.register("earthquake_hammer", () -> new EarthquakeHammer(new Item.Properties().attributes(EarthquakeHammer.createAttributes())));
}
