package ru.pb.ahutils.util.item;

import com.bobmowzie.mowziesmobs.server.item.ItemGeomancerArmor;
import com.bobmowzie.mowziesmobs.server.item.ItemHandler;
import com.bobmowzie.mowziesmobs.server.item.MaterialHandler;
import io.redspace.ironsspellbooks.item.armor.IDisableJacket;
import io.redspace.ironsspellbooks.item.armor.ImbuableChestplateArmorItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import ru.pb.ahutils.client.render.RenderGeomancerArmor;
import ru.pb.ahutils.registry.AttributeRegistry;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class GeomancerArmorItem extends ImbuableChestplateArmorItem implements IDisableJacket {
    public String controllerName = "controller";
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GeomancerArmorItem(ArmorItem.Type slot, Item.Properties settings) {
        super(MaterialHandler.GEOMANCER_ARMOR_MATERIAL, slot, settings, schoolAttributes(AttributeRegistry.EARTH_SPELL_POWER));
    }

    private PlayState predicate(AnimationState<ItemGeomancerArmor> state) {
        return PlayState.STOP;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, this.controllerName, 0, this::predicate));
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        tooltip.add(Component.translatable(this.getDescriptionId().replace("ahutils", "mowziesmobs") + ".text.0").setStyle(ItemHandler.TOOLTIP_STYLE));
    }

    @OnlyIn(Dist.CLIENT)
    public GeoArmorRenderer<?> supplyRenderer() {
        return new RenderGeomancerArmor();
    }

    public static class ClientExtensions implements IClientItemExtensions {
        private GeoArmorRenderer<?> armorRenderer;

        public ClientExtensions() {
        }

        public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
            if (this.armorRenderer == null) {
                this.armorRenderer = new RenderGeomancerArmor();
            }

            this.armorRenderer.prepForRender(entityLiving, itemStack, equipmentSlot, original);
            return this.armorRenderer;
        }
    }
}
