package ru.pb.ahutils.client.model;

import net.minecraft.resources.ResourceLocation;
import ru.pb.ahutils.AHUtils;
import ru.pb.ahutils.util.item.EarthquakeHammer;
import software.bernie.geckolib.model.GeoModel;

public class ModelEarthquakeHammer extends GeoModel<EarthquakeHammer> {
    public ModelEarthquakeHammer() {
    }

    public ResourceLocation getModelResource(EarthquakeHammer object) {
        return ResourceLocation.fromNamespaceAndPath(AHUtils.MOD_ID, "geo/earthquake_hammer.geo.json");
    }

    public ResourceLocation getTextureResource(EarthquakeHammer object) {
        return ResourceLocation.fromNamespaceAndPath(AHUtils.MOD_ID, "textures/item/earthquake_hammer.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EarthquakeHammer earthquakeHammer) {
        return ResourceLocation.fromNamespaceAndPath(AHUtils.MOD_ID, "animations/earthquake_hammer.animation.json");
    }

}
