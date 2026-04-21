package ru.pb.ahutils.client.render;

import com.bobmowzie.mowziesmobs.client.render.entity.MowzieGeoArmorRenderer;
import ru.pb.ahutils.client.model.ModelGeomancerArmor;
import ru.pb.ahutils.util.item.GeomancerArmorItem;
import net.minecraft.world.entity.EquipmentSlot;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.Optional;

public class RenderGeomancerArmor extends MowzieGeoArmorRenderer<GeomancerArmorItem> {
    protected GeoBone beads = null;
    protected GeoBone belt = null;
    protected GeoBone robe = null;

    public RenderGeomancerArmor() {
        super(new ModelGeomancerArmor());
    }

    protected void grabRelevantBones(BakedGeoModel bakedModel) {
        super.grabRelevantBones(bakedModel);

        Optional<GeoBone> beadsOpt = this.model.getBone("prayer_beads");
        Optional<GeoBone> beltOpt = this.model.getBone("belt");
        Optional<GeoBone> robeOpt = this.model.getBone("robes");

        this.beads = beadsOpt.orElse(null);
        this.belt = beltOpt.orElse(null);
        this.robe = robeOpt.orElse(null);
    }

    public void setAllVisible(boolean pVisible) {
        super.setAllVisible(pVisible);
        this.setBoneVisible(this.beads, false);
        this.setBoneVisible(this.belt, false);
        this.setBoneVisible(this.robe, false);
    }

    protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
        this.setAllVisible(false);
        switch (currentSlot) {
            case HEAD:
                this.setBoneVisible(this.getBodyBone(this.model), true);
                this.setBoneVisible(this.beads, true);
                this.setBoneVisible(this.getHeadBone(this.model), true);
                break;
            case CHEST:
                this.setBoneVisible(this.getBodyBone(this.model), true);
                this.setBoneVisible(this.robe, true);
                this.setBoneVisible(this.getRightArmBone(this.model), true);
                this.setBoneVisible(this.getLeftArmBone(this.model), true);
                break;
            case LEGS:
                this.setBoneVisible(this.getBodyBone(this.model), true);
                this.setBoneVisible(this.belt, true);
                this.setBoneVisible(this.getRightLegBone(this.model), true);
                this.setBoneVisible(this.getLeftLegBone(this.model), true);
                break;
            case FEET:
                this.setBoneVisible(this.getRightBootBone(this.model), true);
                this.setBoneVisible(this.getLeftBootBone(this.model), true);
        }

    }
}