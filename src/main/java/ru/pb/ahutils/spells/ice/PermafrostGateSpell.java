package ru.pb.ahutils.spells.ice;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import ru.pb.ahutils.AHUtils;
import ru.pb.ahutils.entity.PermafrostGate;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class PermafrostGateSpell extends AbstractSpell {
    private final ResourceLocation spellId = AHUtils.id("permafrost_gate");
    private final DefaultConfig defaultConfig;

    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.ahutils.permafrost_gate_spell_description"));
    }

    public PermafrostGateSpell() {
        this.defaultConfig = (new DefaultConfig())
                .setMinRarity(SpellRarity.LEGENDARY)
                .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
                .setMaxLevel(1)
                .setCooldownSeconds(120.0F)
                .build();
        this.manaCostPerLevel = 0;
        this.baseSpellPower = 0;
        this.spellPowerPerLevel = 0;
        this.castTime = 120;
        this.baseManaCost = 500;
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.PORTAL_TRAVEL);
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.WARDEN_SONIC_CHARGE);
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return this.defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return this.spellId;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide && entity != null) {
            float radius = 2.5F;
            HitResult raycast = Utils.raycastForEntity(level, entity, 16.0F + radius * 1.5F, true);
            Vec3 center = raycast.getLocation();

            if (raycast instanceof BlockHitResult blockHitResult) {
                if (blockHitResult.getDirection().getAxis().isHorizontal()) {
                    center = center.subtract(0.0F, radius, 0.0F);
                } else if (blockHitResult.getDirection() == Direction.DOWN) {
                    center = center.subtract(0.0F, radius * 2.0F - 1.0F, 0.0F);
                } else {
                    center = center.subtract(0.0F, 1.0F, 0.0F);
                }
            }

            level.playSound((Player) null, center.x, center.y, center.z,
                    SoundRegistry.BLACK_HOLE_CAST.get(), SoundSource.AMBIENT, 4.0F, 1.0F);

            PermafrostGate gate = new PermafrostGate(level, entity);
            gate.setRadius(radius);
            gate.setPos(center.x, center.y, center.z);

            level.addFreshEntity(gate);
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    private float getRadius(int spellLevel, LivingEntity entity) {
        return (float)(2 * spellLevel + 4) + 0.125F * this.getSpellPower(spellLevel, entity);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.CHARGE_ANIMATION;
    }
}
