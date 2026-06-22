package ru.pb.ahutils.client.render;

import com.bobmowzie.mowziesmobs.client.render.entity.MowzieGeoArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import software.bernie.geckolib.cache.object.GeoBone;
import ru.pb.ahutils.client.model.ModelGeomancerArmor;
import ru.pb.ahutils.util.item.GeomancerArmorItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class RenderGeomancerArmor extends MowzieGeoArmorRenderer<GeomancerArmorItem> {
    protected GeoBone beads = null;
    protected GeoBone belt = null;
    protected GeoBone robe = null;

    public RenderGeomancerArmor() {
        super(new ModelGeomancerArmor());
    }

    protected void grabRelevantBones(BakedGeoModel bakedModel) {
        super.grabRelevantBones(bakedModel);
        this.beads = this.model.getBone("prayer_beads").orElse(null);
        this.belt = this.model.getBone("belt").orElse(null);
        this.robe = this.model.getBone("robes").orElse(null);
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

    @Override
    public void applyBoneVisibilityByPart(EquipmentSlot currentSlot,
                                          ModelPart currentPart,
                                          HumanoidModel<?> model) {
        super.applyBoneVisibilityByPart(currentSlot, currentPart, model);

        if (model instanceof PlayerModel<?> playerModel) {
            playerModel.hat.visible = true;
            playerModel.jacket.visible = true;
            playerModel.leftSleeve.visible = true;
            playerModel.rightSleeve.visible = true;
            playerModel.leftPants.visible = true;
            playerModel.rightPants.visible = true;
        }
    }
}