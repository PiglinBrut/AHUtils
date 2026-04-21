package ru.pb.ahutils.util.item;

import com.bobmowzie.mowziesmobs.server.item.ItemHandler;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import io.redspace.ironsspellbooks.item.weapons.StaffTier;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import ru.pb.ahutils.client.render.RenderSculptorStaff;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

import static ru.pb.ahutils.registry.AttributeRegistry.EARTH_SPELL_POWER;

public class SculptorStaffItem extends StaffItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static StaffTier SCULPTOR_STAFF = new StaffTier(3.0F, -3.0F, new AttributeContainer[]{new AttributeContainer(AttributeRegistry.MANA_REGEN, (double)0.25F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), new AttributeContainer(EARTH_SPELL_POWER, 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)});

    public SculptorStaffItem(Properties properties) {
        super(properties);
        GeoItem.registerSyncedAnimatable(this);
    }

    public boolean isValidRepairItem(ItemStack tool, ItemStack ingredient) {
        return ingredient.is((Item) ItemHandler.BLUFF_ROD.get());
    }

    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController(this, "controller", 20, this::predicate));
    }

    private PlayState predicate(AnimationState animationState) {
        return PlayState.CONTINUE;
    }

    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private RenderSculptorStaff renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new RenderSculptorStaff();
                }

                return this.renderer;
            }
        });
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
