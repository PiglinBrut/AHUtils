package ru.pb.ahutils.registry;

import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.item.armor.UpgradeOrbType;
import net.minecraft.ChatFormatting;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.pb.ahutils.AHUtils;
import ru.pb.ahutils.util.Tags;

import java.util.function.Supplier;

import static io.redspace.ironsspellbooks.api.registry.SchoolRegistry.SCHOOL_REGISTRY_KEY;
import static io.redspace.ironsspellbooks.registries.UpgradeOrbTypeRegistry.UPGRADE_ORB_REGISTRY_KEY;

public class SchoolRegistry {
    private static final DeferredRegister<SchoolType> SCHOOLS = DeferredRegister.create(SCHOOL_REGISTRY_KEY, AHUtils.MOD_ID);

    public static void register(IEventBus eventBus) {
        SCHOOLS.register(eventBus);
    }

    private static Supplier<SchoolType> registerSchool(SchoolType schoolType) {
        return SCHOOLS.register(schoolType.getId().getPath(), () -> schoolType);
    }

    public static final ResourceLocation EARTH_RESOURCE = AHUtils.id("earth");
    public static final Supplier<SchoolType> EARTH = registerSchool(new SchoolType(EARTH_RESOURCE, Tags.EARTH_FOCUS, Component.translatable("school.ahutils.earth").withStyle(ChatFormatting.GOLD), AttributeRegistry.EARTH_SPELL_POWER, AttributeRegistry.EARTH_MAGIC_RESIST, SoundRegistry.EARTH_CAST, DamageTypeRegistry.EARTH_MAGIC));

    public static ResourceKey<UpgradeOrbType> EARTH_SPELL_POWER = ResourceKey.create(UPGRADE_ORB_REGISTRY_KEY, AHUtils.id("earth_power"));

    public static void bootstrap(BootstrapContext<UpgradeOrbType> bootstrap) {
        bootstrap.register(EARTH_SPELL_POWER,
                new UpgradeOrbType(AttributeRegistry.EARTH_SPELL_POWER, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, ItemRegistry.EARTH_UPGRADE_ORB));
    }
}
