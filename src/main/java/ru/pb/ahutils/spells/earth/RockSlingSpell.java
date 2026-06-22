package ru.pb.ahutils.spells.earth;

import com.bobmowzie.mowziesmobs.server.entity.EntityHandler;
import com.bobmowzie.mowziesmobs.server.entity.effects.geomancy.EntityGeomancyBase;
import com.bobmowzie.mowziesmobs.server.entity.effects.geomancy.EntityRockSling;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.firebolt.FireboltProjectile;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import ru.pb.ahutils.AHUtils;
import ru.pb.ahutils.registry.SchoolRegistry;
import ru.pb.ahutils.util.item.SculptorStaffItem;

@AutoSpellConfig
public class RockSlingSpell extends AbstractSpell {
    private final ResourceLocation spellId = AHUtils.id("rock_sling");
    private final DefaultConfig defaultConfig;

    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation((int)this.getDamage(spellLevel, caster)+3, 2)}));
    }

    public RockSlingSpell() {
        this.defaultConfig = (new DefaultConfig()).setMinRarity(SpellRarity.COMMON).setSchoolResource(SchoolRegistry.EARTH_RESOURCE).setMaxLevel(10).setCooldownSeconds((double)1.0F).build();
        this.manaCostPerLevel = 2;
        this.baseSpellPower = 0;
        this.spellPowerPerLevel = 1;
        this.castTime = 0;
        this.baseManaCost = 10;
    }

    public CastType getCastType() {
        return CastType.INSTANT;
    }

    public DefaultConfig getDefaultConfig() {
        return this.defaultConfig;
    }

    public ResourceLocation getSpellResource() {
        return this.spellId;
    }

    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!(entity instanceof Player player)) {
            return;
        }

        boolean hasSculptorStaff = player.getMainHandItem().getItem() instanceof SculptorStaffItem || player.getOffhandItem().getItem() instanceof SculptorStaffItem;
        int projectileCount = hasSculptorStaff ? 3 : 1;

        Vec3 from = player.getEyePosition(1.0F);
        Vec3 to = from.add(player.getLookAngle().scale(5.0D));
        BlockHitResult result = level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

        BlockPos targetPos;
        BlockState targetBlock;

        if (result.getType() == HitResult.Type.BLOCK) {
            targetPos = result.getBlockPos();
            targetBlock = level.getBlockState(targetPos);
        } else {
            targetPos = player.blockPosition();
            targetBlock = Blocks.DIRT.defaultBlockState();
        }
        for (int i = 0; i < projectileCount; i++) {
            Vec3 spawnPos;
            Vec3 launchVec;

            if (hasSculptorStaff) {
                double angleOffset = Math.toRadians(-45 + i * 45);
                Vec3 spawnOffset = (new Vec3(0, -1, 2.5))
                        .yRot((float)Math.toRadians((-(player.getYRot()))))
                        .yRot((float) angleOffset);
                spawnPos = player.position()
                        .add(spawnOffset.x + 0.5, spawnOffset.y + 2.0, spawnOffset.z + 0.5);
                launchVec = player.getViewVector(1.0F).multiply(1.0, 0.9, 1.0);


            } else {
                double angleOffset = Math.toRadians(0);
                Vec3 spawnOffset = (new Vec3(0, -1, 2.5))
                        .yRot((float)Math.toRadians((-(player.getYRot()))))
                        .yRot((float) angleOffset);
                spawnPos = player.position()
                        .add(spawnOffset.x + 0.5, spawnOffset.y + 2.0, spawnOffset.z + 0.5);
                launchVec = player.getViewVector(1.0F).multiply(1.0, 0.9, 1.0);
            }

            EntityRockSling boulder = new EntityRockSling(
                    EntityHandler.ROCK_SLING.get(),
                    level,
                    player,
                    targetBlock,
                    targetPos,
                    EntityGeomancyBase.GeomancyTier.values()[1]
            );
            boulder.setDamage((int)this.getDamage(spellLevel, entity) + 3);
            boulder.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            boulder.setLaunchVec(launchVec);

            if (!level.isClientSide && boulder.checkCanSpawn()) {
                level.addFreshEntity(boulder);
            }
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
        return super.getDamageSource(projectile, attacker);
    }

    private float getDamage(int spellLevel, LivingEntity entity) {
        return this.getSpellPower(spellLevel, entity);
    }
}
