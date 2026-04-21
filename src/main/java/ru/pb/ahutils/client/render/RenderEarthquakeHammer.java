package ru.pb.ahutils.client.render;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import ru.pb.ahutils.client.model.ModelEarthquakeHammer;
import ru.pb.ahutils.util.item.EarthquakeHammer;
import software.bernie.geckolib.renderer.GeoItemRenderer;

@OnlyIn(Dist.CLIENT)
public class RenderEarthquakeHammer extends GeoItemRenderer<EarthquakeHammer> {
    public RenderEarthquakeHammer() {
        super(new ModelEarthquakeHammer());
    }
}
