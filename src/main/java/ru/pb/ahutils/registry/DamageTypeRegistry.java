package ru.pb.ahutils.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import ru.pb.ahutils.AHUtils;

public class DamageTypeRegistry {

    public static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(AHUtils.MOD_ID, name));
    }

    public static final ResourceKey<DamageType> EARTH_MAGIC = register("earth_magic");

    public static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(EARTH_MAGIC, new DamageType(EARTH_MAGIC.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
    }
}
