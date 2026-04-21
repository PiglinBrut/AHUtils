package ru.pb.ahutils.util.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class SpearItem extends Item {
    // Уникальные UUID для атрибутов
     private static final ResourceLocation SPEAR_ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath("yourmodid", "spear_attack_damage");
     private static final ResourceLocation SPEAR_ATTACK_SPEED_ID = ResourceLocation.fromNamespaceAndPath("yourmodid", "spear_attack_speed");
     private static final ResourceLocation SPEAR_ATTACK_RANGE_ID = ResourceLocation.fromNamespaceAndPath("yourmodid", "spear_attack_range");

    private final Multimap<Attribute, AttributeModifier> defaultModifiers;
    private final Tier tier;
    private final float attackDamage;
    private final float attackSpeed;

    // Время зарядки для charge атаки (в тиках)
    private static final int CHARGE_START_TICKS = 10;
    private static final int MAX_CHARGE_TICKS = 4000;
    private static final int COOLDOWN_TICKS = 10;

    private final java.util.Map<UUID, Long> lastAttackTime = new java.util.HashMap<>();

    public SpearItem(Tier tier, Properties properties) {
        super(properties.durability(tier.getUses()));
        this.tier = tier;
        this.attackDamage = tier.getAttackDamageBonus() + 4.0F; // Базовый урон копья
        this.attackSpeed = -2.4F; // Медленнее меча, но быстрее топора

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE.value(), new AttributeModifier(
                SPEAR_ATTACK_DAMAGE_ID, this.attackDamage, AttributeModifier.Operation.ADD_VALUE
        ));
        builder.put(Attributes.ATTACK_SPEED.value(), new AttributeModifier(
                SPEAR_ATTACK_SPEED_ID, this.attackSpeed, AttributeModifier.Operation.ADD_VALUE
        ));
        // Добавляем увеличенную дальность атаки (как у копья)
        builder.put(Attributes.ENTITY_INTERACTION_RANGE.value(), new AttributeModifier(
                SPEAR_ATTACK_RANGE_ID, 1.5, AttributeModifier.Operation.ADD_VALUE
        ));

        this.defaultModifiers = builder.build();
    }

    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public boolean canAttackBlock(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player) {
        return !player.isCreative();
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        // Копье не предназначено для добычи блоков
        return 1.0F;
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        // Обычная тычковая атака (jab attack)
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);

        // Воспроизводим звук атаки копьем
        attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);

        return true;
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull LivingEntity entity) {
        if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0F) {
            stack.hurtAndBreak(2, entity, EquipmentSlot.MAINHAND);
        }
        return true;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        player.startUsingItem(hand);

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return MAX_CHARGE_TICKS;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity entity, @NotNull ItemStack stack, int remainingUseDuration) {
        if (!(entity instanceof Player player)) return;

        int useTicks = getUseDuration(stack, entity) - remainingUseDuration;

        if (useTicks >= CHARGE_START_TICKS) {
            long currentTime = level.getGameTime();
            UUID playerId = player.getUUID();
            long lastAttack = lastAttackTime.getOrDefault(playerId, 0L);

            if (currentTime - lastAttack >= COOLDOWN_TICKS) {
                double velocity = Math.sqrt(
                        player.getDeltaMovement().x * player.getDeltaMovement().x +
                                player.getDeltaMovement().z * player.getDeltaMovement().z
                );

                if (velocity >= 0.15) {
                    performChargeAttack(level, player, stack, useTicks, velocity);
                    lastAttackTime.put(playerId, currentTime);
                }
            }
        }
    }

    private void performChargeAttack(Level level, Player player, ItemStack stack, int chargeTicks, double velocity) {
        if (level.isClientSide) return;

        float speedMultiplier = (float) Math.min(velocity * 2.0, 1.5);
        float chargeMultiplier = 0.7F + (Math.min(chargeTicks, MAX_CHARGE_TICKS) / (float) MAX_CHARGE_TICKS) * 0.8F;

        float finalDamage = this.attackDamage * (0.8F + speedMultiplier) * chargeMultiplier;

        double attackRange = 3.5 + velocity * 2.0;

        double attackAngle = Math.toRadians(60);

        AABB attackBox = player.getBoundingBox().inflate(attackRange, 1.0, attackRange);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, attackBox,
                entity -> entity != player && player.hasLineOfSight(entity) && entity.isAlive());

        boolean hitSomething = false;

        for (LivingEntity target : targets) {
            double distanceToTarget = player.distanceTo(target);
            if (distanceToTarget <= attackRange && distanceToTarget >= 1.5) {

                double dx = target.getX() - player.getX();
                double dz = target.getZ() - player.getZ();
                double angleToTarget = Math.abs(Math.atan2(dz, dx) - Math.toRadians(player.getYRot()));
                angleToTarget = Math.abs(angleToTarget % (2 * Math.PI));
                if (angleToTarget > Math.PI) angleToTarget = 2 * Math.PI - angleToTarget;

                if (angleToTarget <= attackAngle) {
                    target.hurt(player.damageSources().playerAttack(player), finalDamage);

                    double knockbackForce = 0.5 + velocity * 0.8;
                    target.knockback(knockbackForce, player.getX() - target.getX(), player.getZ() - target.getZ());

                    hitSomething = true;

                    level.playSound(null, target.getX(), target.getY(), target.getZ(),
                            SoundEvents.TRIDENT_HIT, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
        }

        if (hitSomething) {
            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        net.minecraft.core.particles.ParticleTypes.SWEEP_ATTACK,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        1, 0, 0, 0, 0
                );
            }
        }
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity, int timeCharged) {
        if (entity instanceof Player player && !level.isClientSide) {
            int useTicks = getUseDuration(stack, entity) - timeCharged;
            if (useTicks > 0 && useTicks < CHARGE_START_TICKS) {
                performWeakJab(level, player, stack);
            }
        }
    }

    private void performWeakJab(Level level, Player player, ItemStack stack) {
        double attackRange = 3.5;
        AABB attackBox = player.getBoundingBox().inflate(attackRange, 1.0, attackRange);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, attackBox,
                entity -> entity != player && player.hasLineOfSight(entity) && entity.isAlive());

        for (LivingEntity target : targets) {
            if (player.distanceTo(target) <= attackRange) {
                float damage = this.attackDamage * 0.6F;
                target.hurt(player.damageSources().playerAttack(player), damage);
                level.playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.TRIDENT_HIT, SoundSource.PLAYERS, 0.7F, 1.2F);
                break; // Только одна цель для слабой атаки
            }
        }
    }

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ItemAbility ability) {
        return ability == ItemAbilities.SWORD_SWEEP || super.canPerformAction(stack, ability);
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return this.tier.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack stack, @NotNull ItemStack repairMaterial) {
        return this.tier.getRepairIngredient().test(repairMaterial);
    }

    public static class SpearAttributeHandler {

        @SubscribeEvent
        public void onItemAttributeModifiers(ItemAttributeModifierEvent event) {
            ItemStack stack = event.getItemStack();

            if (!(stack.getItem() instanceof SpearItem spearItem)) {
                return;
            }

            float attackDamage = spearItem.getAttackDamage();
            float attackSpeed = -2.4F;

            event.addModifier(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(SPEAR_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.bySlot(EquipmentSlot.MAINHAND));

            event.addModifier(Attributes.ATTACK_SPEED,
                    new AttributeModifier(SPEAR_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.bySlot(EquipmentSlot.MAINHAND));

            event.addModifier(Attributes.ENTITY_INTERACTION_RANGE,
                    new AttributeModifier(SPEAR_ATTACK_RANGE_ID,  1.5, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.bySlot(EquipmentSlot.MAINHAND));
        }
    }
}