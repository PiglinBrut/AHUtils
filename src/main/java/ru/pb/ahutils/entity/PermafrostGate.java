package ru.pb.ahutils.entity;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import ru.pb.ahutils.registry.EntityRegistry;
import ru.pb.ahutils.registry.SpellRegistry;

import java.util.ArrayList;
import java.util.List;

public class PermafrostGate extends Projectile implements AntiMagicSusceptible {

    private static final EntityDataAccessor<Float> DATA_RADIUS;
    private List<Entity> trackingEntities;
    private int soundTick;
    private float damage;
    private ResourceKey<Level> targetDimension;
    private BlockPos targetSpawnPoint;

    private static final int DURATION_TICKS = 600; // 30 секунд
    private static final int LOOP_SOUND_DURATION = 160;

    public PermafrostGate(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.trackingEntities = new ArrayList<>();
        this.targetDimension = ResourceKey.create(Registries.DIMENSION,
                ResourceLocation.fromNamespaceAndPath("minecraft", "the_end"));
    }

    public PermafrostGate(Level pLevel, LivingEntity owner) {
        this(EntityRegistry.PERMAFROST_GATE.get(), pLevel);
        this.setOwner(owner);
        this.damage = 0;
    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
        this.discard();
    }

    @Override
    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    public float getDamage() {
        return 0;
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return EntityDimensions.scalable(this.getRadius() * 2.0F, this.getRadius() * 2.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        pBuilder.define(DATA_RADIUS, 3.0F);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (DATA_RADIUS.equals(pKey)) {
            this.refreshDimensions();
            if (this.getRadius() < 0.1F) {
                this.discard();
            }
        }
        super.onSyncedDataUpdated(pKey);
    }

    public void setRadius(float pRadius) {
        if (!this.level().isClientSide) {
            this.getEntityData().set(DATA_RADIUS, Math.min(pRadius, 48.0F));
        }
    }

    public float getRadius() {
        return this.getEntityData().get(DATA_RADIUS);
    }

    public void setTargetDimension(ResourceKey<Level> dimension) {
        this.targetDimension = dimension;
    }

    public void setTargetSpawnPoint(BlockPos pos) {
        this.targetSpawnPoint = pos;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putFloat("Radius", this.getRadius());
        pCompound.putInt("Age", this.tickCount);
        if (this.targetDimension != null) {
            pCompound.putString("TargetDimension", this.targetDimension.location().toString());
        }
        if (this.targetSpawnPoint != null) {
            pCompound.putInt("TargetX", this.targetSpawnPoint.getX());
            pCompound.putInt("TargetY", this.targetSpawnPoint.getY());
            pCompound.putInt("TargetZ", this.targetSpawnPoint.getZ());
        }
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.tickCount = pCompound.getInt("Age");
        if (pCompound.getFloat("Radius") > 0) {
            this.setRadius(pCompound.getFloat("Radius"));
        }
        if (pCompound.contains("TargetDimension")) {
            this.targetDimension = ResourceKey.create(Registries.DIMENSION,
                    ResourceLocation.parse(pCompound.getString("TargetDimension")));
        }
        if (pCompound.contains("TargetX")) {
            this.targetSpawnPoint = new BlockPos(
                    pCompound.getInt("TargetX"),
                    pCompound.getInt("TargetY"),
                    pCompound.getInt("TargetZ")
            );
        }
        super.readAdditionalSaveData(pCompound);
    }

    @Override
    public void tick() {
        super.tick();

        int update = Math.max((int)(this.getRadius() / 2.0F), 2);
        if (this.tickCount % update == 0) {
            this.updateTrackingEntities();
        }

        AABB bb = this.getBoundingBox();
        float radius = (float) bb.getXsize();
        boolean hitTick = this.tickCount % 10 == 0;

        for (Entity entity : this.trackingEntities) {
            if (!entity.isSpectator()) {
                Vec3 center = bb.getCenter();
                float distance = (float) center.distanceTo(entity.position());
                if (!(distance > radius)) {
                    float f = 1.0F - distance / radius;
                    float scale = f * f * f * f * 0.25F;

                    Vec3 diff = center.subtract(entity.position()).scale(scale);
                    entity.push(diff.x, diff.y, diff.z);

                    if (hitTick && distance < 9.0F && this.canHitEntity(entity) && entity != this.getOwner()) {
                        DamageSources.applyDamage(entity, this.damage, (SpellRegistry.PERMAFROST_GATE_SPELL.get()).getDamageSource(this, this.getOwner()));
                    }

                    if (hitTick && distance < 1.5F) {
                        teleportEntity(entity);
                    }
                    entity.fallDistance = 0.0F;
                }
            }
        }

        if (!this.level().isClientSide) {
            if (this.tickCount > DURATION_TICKS) {
                this.discard();
                this.playSound(SoundRegistry.BLACK_HOLE_CAST.get(), this.getRadius() / 2.0F, 1.0F);
            } else if ((this.tickCount - 1) % LOOP_SOUND_DURATION == 0) {
                this.playSound(SoundRegistry.BLACK_HOLE_LOOP.get(), this.getRadius() / 3.0F, 1.0F);
            }
        }

        if (this.level().isClientSide) {
            for (int i = 0; i < 5; i++) {
                this.level().addParticle(ParticleHelper.UNSTABLE_ENDER,
                        this.getX() + (this.random.nextDouble() - 0.5) * this.getRadius() * 2,
                        this.getY() + (this.random.nextDouble() - 0.5) * this.getRadius() * 2,
                        this.getZ() + (this.random.nextDouble() - 0.5) * this.getRadius() * 2,
                        0, 0, 0);
            }
        }
    }

    private void teleportEntity(Entity entity) {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        Level targetWorld = serverLevel.getServer().getLevel(this.targetDimension);
        if (targetWorld == null) {
            targetWorld = serverLevel.getServer().getLevel(Level.OVERWORLD);
        }

        if (!(targetWorld instanceof ServerLevel targetServerLevel)) return;

        BlockPos targetPos = this.targetSpawnPoint;
        if (targetPos == null) {
            targetPos = new BlockPos(entity.getBlockX(), findTopBlockY(targetServerLevel, entity.getBlockX(), entity.getBlockZ()) + 10, entity.getBlockZ());
        }

        if (entity instanceof LivingEntity living) {
            // Сохраняем последнюю позицию в портале
            if (living instanceof ServerPlayer player) {
                player.teleportTo((ServerLevel) targetWorld,
                        targetPos.getX() + 0.5, targetPos.getY() + 1, targetPos.getZ() + 0.5,
                        player.getYRot(), player.getXRot());

                MagicManager.spawnParticles(serverLevel, ParticleHelper.UNSTABLE_ENDER,
                        entity.getX(), entity.getY(), entity.getZ(),
                        50, 0.5, 0.5, 0.5, 1.0, true);
            } else {
                living.teleportTo(targetPos.getX() + 0.5, targetPos.getY() + 1, targetPos.getZ() + 0.5);
                living.setPortalCooldown();
            }

            entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
    }

    private int findTopBlockY(ServerLevel level, int x, int z) {
        for (int y = level.getMaxBuildHeight() - 1; y > level.getMinBuildHeight(); y--) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = level.getBlockState(pos);

            if (!state.isAir() && state.isSolidRender(level, pos)) {
                return y;
            }
        }
        return level.getMinBuildHeight();
    }

    private void updateTrackingEntities() {
        this.trackingEntities = this.level().getEntities(this, this.getBoundingBox().inflate(2.0));
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    static {
        DATA_RADIUS = SynchedEntityData.defineId(PermafrostGate.class, EntityDataSerializers.FLOAT);
    }
}