package ru.pb.ahutils.util.item;

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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
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
import ru.pb.ahutils.client.render.RenderEarthquakeHammer;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class EarthquakeHammer extends Item implements GeoItem {
    private final AnimatableInstanceCache animationCache; // Не static!
    private static final ResourceLocation BASE_ATTACK_KNOCKBACK_ID = ResourceLocation.withDefaultNamespace("base_attack_knockback");

    private static final double ATTACK_DAMAGE = 9.0;
    private static final double ATTACK_SPEED = -3.4;
    private static final double ATTACK_KNOCKBACK = 5.0;
    private static final double SMASH_SPEED_FACTOR = 0.01;
    private static final int EARTHQUAKE_DURATION = 20;
    private static final int COOLDOWN_SECONDS = 5;
    private static final int COOLDOWN_TICKS = COOLDOWN_SECONDS * 20;
    private static final float FISSURE_SPEED = 2.0f / EntityFissure.TICKS_PER_PIECE;
    private static final float MIN_SMASH_FALL_DISTANCE = 0.1f;

    public EarthquakeHammer(Item.Properties properties) {
        super(properties);
        this.animationCache = GeckoLibUtil.createInstanceCache(this);
        GeoItem.registerSyncedAnimatable(this);
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, ATTACK_DAMAGE, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, ATTACK_SPEED, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_KNOCKBACK,
                        new AttributeModifier(BASE_ATTACK_KNOCKBACK_ID, ATTACK_KNOCKBACK, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!(attacker instanceof ServerPlayer serverplayer) || !canSmashAttack(serverplayer)) {
            return true;
        }

        ServerLevel serverlevel = (ServerLevel) attacker.level();
        float fallDistance = serverplayer.fallDistance;

        createEarthquakeAoe(serverlevel, serverplayer, target, fallDistance);
        handleFallDamage(serverplayer, fallDistance);
        playSmashSound(serverlevel, serverplayer, fallDistance);

        return true;
    }

    private void createEarthquakeAoe(ServerLevel level, ServerPlayer owner, LivingEntity target, float fallDistance) {
        Vec3 spawnPos = Utils.moveToRelativeGroundLevel(
                level,
                target.getEyePosition().add(target.getForward().multiply(1.0, 0.0, 1.0)),
                1
        );

        EarthquakeAoe aoeEntity = new EarthquakeAoe(level);
        aoeEntity.moveTo(spawnPos);
        aoeEntity.setOwner(owner);
        aoeEntity.setCircular();
        aoeEntity.setRadius(fallDistance / 4);
        aoeEntity.setDuration(EARTHQUAKE_DURATION);
        aoeEntity.setDamage(fallDistance / 2);
        aoeEntity.setSlownessAmplifier(1);

        level.addFreshEntity(aoeEntity);
    }

    private void handleFallDamage(ServerPlayer player, float fallDistance) {
        if (player.isIgnoringFallDamageFromCurrentImpulse() && player.currentImpulseImpactPos != null) {
            if (player.currentImpulseImpactPos.y > player.position().y) {
                player.currentImpulseImpactPos = player.position();
            }
        } else {
            player.currentImpulseImpactPos = player.position();
        }

        player.setIgnoreFallDamageFromCurrentImpulse(true);
        player.setDeltaMovement(player.getDeltaMovement().with(Direction.Axis.Y, SMASH_SPEED_FACTOR));
        player.connection.send(new ClientboundSetEntityMotionPacket(player));
        player.setSpawnExtraParticlesOnFall(true);
    }

    private void playSmashSound(ServerLevel level, ServerPlayer player, float fallDistance) {
        SoundEvent soundEvent;

        if (player.onGround()) {
            soundEvent = fallDistance > 5.0f ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND;
        } else {
            soundEvent = SoundEvents.MACE_SMASH_AIR;
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                soundEvent, player.getSoundSource(), 1.0f, 1.0f);
    }

    @Override
    public float getAttackDamageBonus(Entity target, float damage, DamageSource damageSource) {
        Entity directEntity = damageSource.getDirectEntity();
        if (!(directEntity instanceof LivingEntity livingentity) || !canSmashAttack(livingentity)) {
            return 0.0f;
        }

        float fallDistance = livingentity.fallDistance;

        if (livingentity.level() instanceof ServerLevel serverlevel) {
            return fallDistance + EnchantmentHelper.modifyFallBasedDamage(
                    serverlevel,
                    livingentity.getWeaponItem(),
                    target,
                    damageSource,
                    0.0f
            ) * fallDistance;
        }

        return fallDistance;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        if (canSmashAttack(attacker)) {
            attacker.resetFallDistance();
        }
    }

    private static boolean canSmashAttack(LivingEntity entity) {
        return entity.fallDistance > MIN_SMASH_FALL_DISTANCE && !entity.isFallFlying();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);

        if (!player.onGround()) {
            return InteractionResultHolder.fail(itemStack);
        }

        if (level.isClientSide) {
            return InteractionResultHolder.success(itemStack);
        }

        createAndShootFissure(level, player);
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        return InteractionResultHolder.consume(itemStack);
    }

    private void createAndShootFissure(Level level, Player player) {
        EntityFissure fissure = new EntityFissure(EntityHandler.FISSURE.get(), player.level());
        fissure.setOwner(player);
        fissure.setPos(player.position());

        Vec3 targetPos = Utils.moveToRelativeGroundLevel(
                level,
                player.getEyePosition().add(player.getForward().multiply(1.0, 0.0, 1.0)),
                1
        );

        double distance = fissure.position().subtract(targetPos).length();
        double timeToReach = distance / FISSURE_SPEED;

        Vec3 targetMovement = targetPos.subtract(player.position())
                .scale(timeToReach * 0.025);

        targetMovement = targetMovement.multiply(1.0, 0.0, 1.0);
        Vec3 futureTargetPos = targetPos.add(targetMovement);
        Vec3 projectileMid = fissure.position().add(0.0, fissure.getBbHeight() / 2.0, 0.0);
        Vec3 shootVec = futureTargetPos.subtract(projectileMid).normalize();

        fissure.shoot(shootVec.x, shootVec.z);
        level.addFreshEntity(fissure);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 20, this::predicate));
    }

    private PlayState predicate(AnimationState<EarthquakeHammer> animationState) {
        return PlayState.STOP;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private RenderEarthquakeHammer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = new RenderEarthquakeHammer();
                }
                return renderer;
            }
        });
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }
}