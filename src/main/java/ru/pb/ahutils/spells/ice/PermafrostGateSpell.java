package ru.pb.ahutils.spells.ice;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import ru.pb.ahutils.AHUtils;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class PermafrostGateSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(AHUtils.MOD_ID, "permafrost_gate");
    private final DefaultConfig defaultConfig;

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("fdgs eorsouresbf idsgoudfgois fgougfou fdushdf ousfdhbg")
        );
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
        this.castTime = 100;
        this.baseManaCost = 100;
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
    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
//        if (world.dimension().equals(Level.NETHER)) {
//            entity.changeDimension(new DimensionTransition(entity.getServer().getLevel(Level.OVERWORLD), entity.position(), Vec3.ZERO, entity.getYRot(), entity.getXRot(), DimensionTransition.DO_NOTHING));
//            entity.setPos(entity.getX(), entity.getServer().getAbsoluteMaxWorldSize(), entity.getZ());
//        }
//        if (world.dimension().equals(Level.OVERWORLD)) {
//            entity.changeDimension(new DimensionTransition(entity.getServer().getLevel(Level.NETHER), entity.position(), Vec3.ZERO, entity.getYRot(), entity.getXRot(), DimensionTransition.DO_NOTHING));
//        }
        if (!world.isClientSide) {
            for(int i = 0; i < 16; ++i) {
                double d0 = entity.getX() + (entity.getRandom().nextDouble() - (double)0.5F) * (double)16.0F;
                double d1 = Mth.clamp(entity.getY() + (double)(entity.getRandom().nextInt(16) - 8), (double)world.getMinBuildHeight(), (double)(world.getMinBuildHeight() + ((ServerLevel)world).getLogicalHeight() - 1));
                double d2 = entity.getZ() + (entity.getRandom().nextDouble() - (double)0.5F) * (double)16.0F;
                if (entity.isPassenger()) {
                    entity.stopRiding();
                }

                Vec3 vec3 = entity.position();
                EntityTeleportEvent.ChorusFruit event = EventHooks.onChorusFruitTeleport(entity, d0, d1, d2);

                if (entity.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
                    world.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(entity));
                    SoundSource soundsource;
                    SoundEvent soundevent;
                    soundevent = SoundEvents.CHORUS_FRUIT_TELEPORT;
                    soundsource = SoundSource.PLAYERS;


                    world.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), soundevent, soundsource);
                    entity.resetFallDistance();
                    break;
                }
            }

            if (entity instanceof Player) {
                Player player = (Player)entity;
                player.resetCurrentImpulseContext();
            }
        }
        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.CHARGE_ANIMATION;
    }
}
