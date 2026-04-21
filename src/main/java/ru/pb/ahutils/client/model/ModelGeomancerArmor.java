package ru.pb.ahutils.client.model;

import net.minecraft.resources.ResourceLocation;
import ru.pb.ahutils.util.item.GeomancerArmorItem;
import software.bernie.geckolib.model.GeoModel;

public class ModelGeomancerArmor extends GeoModel<GeomancerArmorItem> {

    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("mowziesmobs", "geo/geomancer_armor.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("mowziesmobs", "textures/item/geomancer_armor.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath("mowziesmobs", "animations/empty.animation.json");

    @Override
    public ResourceLocation getModelResource(GeomancerArmorItem object) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(GeomancerArmorItem object) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(GeomancerArmorItem animatable) {
        return ANIMATION;
    }
}