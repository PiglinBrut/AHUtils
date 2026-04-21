package ru.pb.ahutils.event;

import com.bobmowzie.mowziesmobs.server.entity.EntityHandler;
import com.bobmowzie.mowziesmobs.server.entity.effects.geomancy.EntityBoulderProjectile;
import com.bobmowzie.mowziesmobs.server.entity.effects.geomancy.EntityGeomancyBase;
import com.bobmowzie.mowziesmobs.server.item.ItemHandler;
import com.bobmowzie.mowziesmobs.server.potion.EffectGeomancy;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import ru.pb.ahutils.AHUtils;
import ru.pb.ahutils.registry.ItemRegistry;

@EventBusSubscriber(modid = AHUtils.MOD_ID)
public class GeomancerArmorHandler {

    // Модификаторы для поножей
    private static final ResourceLocation GEOMANCY_BELT_DEFENSE = ResourceLocation.fromNamespaceAndPath(AHUtils.MOD_ID, "geomancy_belt_defense_boost");
    private static final AttributeModifier DEFENSE_MODIFIER_BELT = new AttributeModifier(GEOMANCY_BELT_DEFENSE, 4.0, AttributeModifier.Operation.ADD_VALUE);

    private static final ResourceLocation GEOMANCY_BELT_KNOCKBACK_RESISTANCE = ResourceLocation.fromNamespaceAndPath(AHUtils.MOD_ID, "geomancy_belt_knockback_resistance_boost");
    private static final AttributeModifier KNOCKBACK_MODIFIER_BELT = new AttributeModifier(GEOMANCY_BELT_KNOCKBACK_RESISTANCE, 1.0, AttributeModifier.Operation.ADD_VALUE);

    // Модификатор для шлема (+3 урона)
    private static final ResourceLocation GEOMANCY_BEADS_ATTACK = ResourceLocation.fromNamespaceAndPath(AHUtils.MOD_ID, "geomancy_beads_attack_boost");
    private static final AttributeModifier ATTACK_MODIFIER_BEADS = new AttributeModifier(GEOMANCY_BEADS_ATTACK, 3.0, AttributeModifier.Operation.ADD_VALUE);

    // ==================== ПОНOЖИ: Защита от замедления ====================
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) {
            return;
        }

        if (livingEntity.level().isClientSide) {
            return;
        }

        ItemStack leggings = livingEntity.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS);
        AttributeInstance armorAttribute = livingEntity.getAttribute(Attributes.ARMOR);
        AttributeInstance knockbackAttribute = livingEntity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);

        boolean hasGeomancerLeggings = leggings.getItem() == ItemRegistry.GEOMANCER_BELT.get();
        boolean hasSlowness = livingEntity.hasEffect(MobEffects.MOVEMENT_SLOWDOWN);

        if (hasGeomancerLeggings && hasSlowness) {
            if (armorAttribute != null && !armorAttribute.hasModifier(GEOMANCY_BELT_DEFENSE)) {
                armorAttribute.addTransientModifier(DEFENSE_MODIFIER_BELT);
            }
            if (knockbackAttribute != null && !knockbackAttribute.hasModifier(GEOMANCY_BELT_KNOCKBACK_RESISTANCE)) {
                knockbackAttribute.addTransientModifier(KNOCKBACK_MODIFIER_BELT);
            }
        } else {
            if (armorAttribute != null && armorAttribute.hasModifier(GEOMANCY_BELT_DEFENSE)) {
                armorAttribute.removeModifier(GEOMANCY_BELT_DEFENSE);
            }
            if (knockbackAttribute != null && knockbackAttribute.hasModifier(GEOMANCY_BELT_KNOCKBACK_RESISTANCE)) {
                knockbackAttribute.removeModifier(GEOMANCY_BELT_KNOCKBACK_RESISTANCE);
            }
        }
    }

    // ==================== ШЛЕМ: +3 урона голыми руками ====================
    @SubscribeEvent
    public static void onEquipmentChanged(LivingEquipmentChangeEvent event) {
        LivingEntity entity = event.getEntity();
        ItemStack weapon = entity.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND);
        AttributeInstance attackAttribute = entity.getAttribute(Attributes.ATTACK_DAMAGE);

        if (attackAttribute != null && (event.getSlot() == net.minecraft.world.entity.EquipmentSlot.HEAD ||
                event.getSlot() == net.minecraft.world.entity.EquipmentSlot.MAINHAND)) {
            attackAttribute.removeModifier(GEOMANCY_BEADS_ATTACK);

            boolean hasGeomancerHelmet = entity.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD)
                    .getItem() == ItemRegistry.GEOMANCER_BEADS.get();
            boolean hasValidWeapon = weapon.isEmpty() || weapon.getItem() == ItemHandler.EARTHREND_GAUNTLET.get();

            if (hasGeomancerHelmet && hasValidWeapon) {
                attackAttribute.addTransientModifier(ATTACK_MODIFIER_BEADS);
            }
        }
    }

    // ==================== НАГРУДНИК: Призыв валунов при получении урона ====================
    @SubscribeEvent
    public static void onLivingHurt(LivingDamageEvent.Post event) {
        // Проверяем, кто нанёс урон (атаковал игрок)
        if (event.getSource().getEntity() instanceof Player player) {
            ItemStack chestplate = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);

            if (chestplate.getItem() == ItemRegistry.GEOMANCER_ROBE.get()) {
                // Оригинальный шанс: 50% (из кода: !(random > 0.5F))
                if (player.getRandom().nextFloat() <= 0.5f) {
                    spawnBoulderNearPlayer(player);
                }
            }
        }

        // Также проверяем, если урон получил игрок (для пассивного эффекта)
        if (event.getEntity() instanceof Player player) {
            ItemStack chestplate = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);

            if (chestplate.getItem() == ItemRegistry.GEOMANCER_ROBE.get()) {
                if (player.getRandom().nextFloat() <= 0.5f) {
                    spawnBoulderNearPlayer(player);
                }
            }
        }
    }

    // ==================== БОТИНКИ: Ускорение после падения ====================
    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getDistance() > 4.0F) {
                ItemStack boots = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET);
                if (boots.getItem() == ItemRegistry.GEOMANCER_SANDALS.get()) {
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED,
                            60,  // 3 секунды
                            0    // Уровень I
                    ));
                }
            }
        }
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЙ МЕТОД: Спавн валунов ====================
    private static void spawnBoulderNearPlayer(Player player) {
        if (!((double)player.getRandom().nextFloat() > (double)0.5F)) {
            int i = Mth.floor(player.getX());
            int j = Mth.floor(player.getY());
            int k = Mth.floor(player.getZ());

            for(int l = 0; l < 10; ++l) {
                double radius = Math.pow((double)player.getRandom().nextFloat(), (double)0.5F) * (double)10.0F + (double)3.0F;
                double angle = (double)player.getRandom().nextFloat() * Math.PI * (double)2.0F;
                int i1 = i + (int)(Math.cos(angle) * radius);
                int j1 = j + Mth.nextInt(player.getRandom(), 0, 15) * Mth.nextInt(player.getRandom(), -1, 1);
                int k1 = k + (int)(Math.sin(angle) * radius);
                BlockPos spawnBoulderPos = new BlockPos(i1, j1, k1);
                BlockState state = player.level().getBlockState(spawnBoulderPos);
                int searchDist = 0;

                int maxSearchDist;
                for(maxSearchDist = 10; state.canBeReplaced() && searchDist < maxSearchDist; ++searchDist) {
                    spawnBoulderPos = spawnBoulderPos.below();
                    state = player.level().getBlockState(spawnBoulderPos);
                }

                for(int var20 = 0; !state.canBeReplaced() && var20 < maxSearchDist; ++var20) {
                    spawnBoulderPos = spawnBoulderPos.above();
                    state = player.level().getBlockState(spawnBoulderPos);
                }

                spawnBoulderPos = spawnBoulderPos.below();
                state = player.level().getBlockState(spawnBoulderPos);
                if (EffectGeomancy.isBlockUseable(state)) {
                    EntityBoulderProjectile boulder = new EntityBoulderProjectile((EntityType) EntityHandler.BOULDER_PROJECTILE.get(), player.level(), player, state, spawnBoulderPos, EntityGeomancyBase.GeomancyTier.SMALL);
                    boulder.setPos((double)((float)spawnBoulderPos.getX() + 0.5F), (double)(spawnBoulderPos.getY() + 2), (double)((float)spawnBoulderPos.getZ() + 0.5F));
                    if (!player.level().isClientSide && boulder.checkCanSpawn()) {
                        player.level().addFreshEntity(boulder);
                        break;
                    }
                }
            }

        }
    }
}