package ru.pb.ahutils.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import ru.pb.ahutils.AHUtils;

public class Tags {
    public static final TagKey<Item> EARTH_FOCUS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(AHUtils.MOD_ID, "earth_focus"));
    public static final TagKey<Item> AIR_FOCUS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(AHUtils.MOD_ID, "air_focus"));
}
