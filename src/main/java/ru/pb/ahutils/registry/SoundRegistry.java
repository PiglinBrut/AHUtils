package ru.pb.ahutils.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.pb.ahutils.AHUtils;

public class SoundRegistry {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, AHUtils.MOD_ID);

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(AHUtils.MOD_ID, name)));
    }

    public static DeferredHolder<SoundEvent, SoundEvent> EARTH_CAST = registerSoundEvent("cast.generic.earth");
    public static DeferredHolder<SoundEvent, SoundEvent> AIR_CAST = registerSoundEvent("cast.generic.air");
}
