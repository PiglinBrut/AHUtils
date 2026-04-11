package ru.pb.ahutils.util.tools;

import com.bobmowzie.mowziesmobs.server.entity.EntityHandler;
import com.bobmowzie.mowziesmobs.server.entity.effects.geomancy.EntityFissure;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.spells.EarthquakeAoe;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import ru.pb.ahutils.client.RenderEarthquakeHammer;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class EarthquakeHammer extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final ResourceLocation BASE_ATTACK_KNOCKBACK_ID = ResourceLocation.withDefaultNamespace("base_attack_knockback");

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, (double)9.0F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, (double)-3.4F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(BASE_ATTACK_KNOCKBACK_ID, (double)5.0F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public EarthquakeHammer(Item.Properties properties) {
        super(properties);
        GeoItem.registerSyncedAnimatable(this);
    }

    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    public int getEnchantmentValue() {
        return 15;
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof ServerPlayer serverplayer) {
            if (canSmashAttack(serverplayer)) {
                ServerLevel serverlevel = (ServerLevel)attacker.level();

                float f1 = serverplayer.fallDistance;
                float f2;
                //if (f1 <= 3.0F) {
                    f2 = f1;
                //} else {
                //    f2 = ((f1 - 3.0F) / 4.0F) + 3.0F;
                //}

                Vec3 spawn = Utils.moveToRelativeGroundLevel(serverlevel, target.getEyePosition().add(target.getForward().multiply((double)1.0F, (double)0.0F, (double)1.0F)), 1);
                EarthquakeAoe aoeEntity = new EarthquakeAoe(serverlevel);
                aoeEntity.moveTo(spawn);
                aoeEntity.setOwner(serverplayer);
                aoeEntity.setCircular();
                aoeEntity.setRadius(f2/4);
                aoeEntity.setDuration(20);
                aoeEntity.setDamage(f2/2);
                aoeEntity.setOwner(attacker);
                aoeEntity.setSlownessAmplifier(1);
                serverlevel.addFreshEntity(aoeEntity);
                if (serverplayer.isIgnoringFallDamageFromCurrentImpulse() && serverplayer.currentImpulseImpactPos != null) {
                    if (serverplayer.currentImpulseImpactPos.y > serverplayer.position().y) {
                        serverplayer.currentImpulseImpactPos = serverplayer.position();
                    }
                } else {
                    serverplayer.currentImpulseImpactPos = serverplayer.position();
                }

                serverplayer.setIgnoreFallDamageFromCurrentImpulse(true);
                serverplayer.setDeltaMovement(serverplayer.getDeltaMovement().with(Direction.Axis.Y, (double)0.01F));
                serverplayer.connection.send(new ClientboundSetEntityMotionPacket(serverplayer));
                if (target.onGround()) {
                    serverplayer.setSpawnExtraParticlesOnFall(true);
                    SoundEvent soundevent = serverplayer.fallDistance > 5.0F ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND;
                    serverlevel.playSound((Player)null, serverplayer.getX(), serverplayer.getY(), serverplayer.getZ(), soundevent, serverplayer.getSoundSource(), 1.0F, 1.0F);
                } else {
                    serverlevel.playSound((Player)null, serverplayer.getX(), serverplayer.getY(), serverplayer.getZ(), SoundEvents.MACE_SMASH_AIR, serverplayer.getSoundSource(), 1.0F, 1.0F);
                }
            }
        }

        return true;
    }

    public float getAttackDamageBonus(Entity target, float damage, DamageSource damageSource) {
        Entity f3 = damageSource.getDirectEntity();
        if (f3 instanceof LivingEntity livingentity) {
            if (!canSmashAttack(livingentity)) {
                return 0.0F;
            } else {
                float f1 = livingentity.fallDistance;
                float f2;
                //if (f1 <= 3.0F) {
                    f2 = f1;
                //} else {
                //    f2 = ((f1 - 3.0F) / 4.0F) + 3.0F;
                //}

                Level var10 = livingentity.level();
                float var10000;
                if (var10 instanceof ServerLevel) {
                    ServerLevel serverlevel = (ServerLevel)var10;
                    var10000 = f2 + EnchantmentHelper.modifyFallBasedDamage(serverlevel, livingentity.getWeaponItem(), target, damageSource, 0.0F) * f1;
                } else {
                    var10000 = f2;
                }

                return var10000;
            }
        } else {
            return 0.0F;
        }
    }

    public static void damageBonus() {

    }

    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        if (canSmashAttack(attacker)) {
            attacker.resetFallDistance();
        }

    }

    public static boolean canSmashAttack(LivingEntity entity) {
        return entity.fallDistance > 0.1F && !entity.isFallFlying();
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        if (player.onGround()) {
            EntityFissure fissure = new EntityFissure(EntityHandler.FISSURE.get(), player.level());
            fissure.setOwner(player);
            fissure.setPos((player).position());
            Vec3 shootVec;
            float speed = 2.0F / (float)EntityFissure.TICKS_PER_PIECE;
            Vec3 targetPos = Utils.moveToRelativeGroundLevel(level, player.getEyePosition().add(player.getForward().multiply((double)1.0F, (double)0.0F, (double)1.0F)), 1);
            double timeToReach = fissure.position().subtract(targetPos).length() / (double)speed;
            Vec3 targetMovement = targetPos.subtract(targetPos).scale(timeToReach * 0.1F * (double)1.0F / (double)4.0F);
            targetMovement = targetMovement.multiply((double)1.0F, (double)0.0F, (double)1.0F);
            Vec3 futureTargetPos = targetPos.add(targetMovement);
            Vec3 projectileMid = fissure.position().add((double)0.0F, (double)fissure.getBbHeight() / (double)2.0F, (double)0.0F);
            shootVec = futureTargetPos.subtract(projectileMid).normalize();
            fissure.shoot(shootVec.x, shootVec.z);
            level.addFreshEntity(fissure);
            player.getCooldowns().addCooldown(this, 5*20);
            return InteractionResultHolder.consume(itemstack);
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController(this, "controller", 20, this::predicate));
    }

    private PlayState predicate(AnimationState animationState) {
        return null;
    }

//    private PlayState predicate(AnimationState<TestArmorItem> testArmorItemAnimationState) {
//        testArmorItemAnimationState.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
//        return PlayState.CONTINUE;
//    }

    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private RenderEarthquakeHammer renderer;

            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new RenderEarthquakeHammer();
                }

                return this.renderer;
            }
        });
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
