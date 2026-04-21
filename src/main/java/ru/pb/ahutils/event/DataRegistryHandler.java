package ru.pb.ahutils.event;

import io.redspace.ironsspellbooks.item.armor.UpgradeOrbType;
import io.redspace.ironsspellbooks.registries.UpgradeOrbTypeRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import ru.pb.ahutils.AHUtils;
import ru.pb.ahutils.registry.DamageTypeRegistry;
import ru.pb.ahutils.registry.SchoolRegistry;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

//@EventBusSubscriber(modid = AHUtils.MOD_ID)
public class DataRegistryHandler extends DatapackBuiltinEntriesProvider {

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, DamageTypeRegistry::bootstrap)
            .add(UpgradeOrbTypeRegistry.UPGRADE_ORB_REGISTRY_KEY, SchoolRegistry::bootstrap);

    public DataRegistryHandler(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, BUILDER, Set.of("minecraft", AHUtils.MOD_ID));
    }
}
