package ru.pb.ahutils.registry;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.item.UpgradeOrbItem;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import io.redspace.ironsspellbooks.item.weapons.StaffTier;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.pb.ahutils.AHUtils;
import ru.pb.ahutils.util.item.EarthquakeHammer;
import ru.pb.ahutils.util.item.GeomancerArmorItem;
import ru.pb.ahutils.util.item.SculptorStaffItem;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AHUtils.MOD_ID);
    public static  void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static final DeferredItem<Item> EARTHQUAKE_HAMMER = ITEMS.register("earthquake_hammer", () -> new EarthquakeHammer(new Item.Properties().attributes(EarthquakeHammer.createAttributes())));

//    public static final DeferredItem<Item> SPEAR = ITEMS.register("spear", () -> new SpearItem(Tiers.DIAMOND, new Item.Properties()));

    public static final DeferredItem<Item> EARTH_UPGRADE_ORB = ITEMS.register("earth_upgrade_orb", () -> new UpgradeOrbItem(ItemPropertiesHelper.material().rarity(Rarity.UNCOMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, SchoolRegistry.EARTH_SPELL_POWER)));

    public static final DeferredHolder<Item, Item> EARTH_RUNE = ITEMS.register("earth_rune", () -> new Item(ItemPropertiesHelper.material()));;

    public static final DeferredItem<ArmorItem> GEOMANCER_BEADS = ITEMS.register("geomancer_beads", () -> new GeomancerArmorItem(ArmorItem.Type.HELMET, (new Item.Properties()).rarity(Rarity.UNCOMMON).durability(ArmorItem.Type.HELMET.getDurability(33))));
    public static final DeferredItem<ArmorItem> GEOMANCER_ROBE = ITEMS.register("geomancer_robe", () -> new GeomancerArmorItem(ArmorItem.Type.CHESTPLATE, (new Item.Properties()).rarity(Rarity.UNCOMMON).durability(ArmorItem.Type.CHESTPLATE.getDurability(33))));
    public static final DeferredItem<ArmorItem> GEOMANCER_BELT = ITEMS.register("geomancer_belt", () -> new GeomancerArmorItem(ArmorItem.Type.LEGGINGS, (new Item.Properties()).rarity(Rarity.UNCOMMON).durability(ArmorItem.Type.LEGGINGS.getDurability(33))));
    public static final DeferredItem<ArmorItem> GEOMANCER_SANDALS = ITEMS.register("geomancer_sandals", () -> new GeomancerArmorItem(ArmorItem.Type.BOOTS, (new Item.Properties()).rarity(Rarity.UNCOMMON).durability(ArmorItem.Type.BOOTS.getDurability(33))));

    public static final DeferredItem<StaffItem> SCULPTOR_STAFF = ITEMS.register("sculptor_staff", () -> new SculptorStaffItem(ItemPropertiesHelper.equipment(1).attributes(ExtendedSwordItem.createAttributes(SculptorStaffItem.SCULPTOR_STAFF)).rarity(Rarity.RARE)));

}
