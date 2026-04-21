package ru.pb.ahutils.client.model;

import net.minecraft.resources.ResourceLocation;
import ru.pb.ahutils.util.item.SculptorStaffItem;
import software.bernie.geckolib.model.GeoModel;

public class ModelSculptorStaff extends GeoModel<SculptorStaffItem> {

    public ResourceLocation getModelResource(SculptorStaffItem object) {
        return ResourceLocation.fromNamespaceAndPath("mowziesmobs", "geo/sculptor_staff.geo.json");
    }

    public ResourceLocation getTextureResource(SculptorStaffItem object) {
        return ResourceLocation.fromNamespaceAndPath("mowziesmobs", "textures/item/sculptor_staff.png");
    }

    public ResourceLocation getAnimationResource(SculptorStaffItem animatable) {
        return ResourceLocation.fromNamespaceAndPath("mowziesmobs", "animations/sculptor.animation.json");
    }
}
