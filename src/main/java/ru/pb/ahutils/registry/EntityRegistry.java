package ru.pb.ahutils.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.pb.ahutils.AHUtils;
import ru.pb.ahutils.entity.PermafrostGate;

public class EntityRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, AHUtils.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<PermafrostGate>> PERMAFROST_GATE =
            ENTITIES.register("permafrost_gate", () -> EntityType.Builder
                    .<PermafrostGate>of(PermafrostGate::new, MobCategory.MISC)
                    .sized(1.5f, 1.5f)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build("permafrost_gate"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}