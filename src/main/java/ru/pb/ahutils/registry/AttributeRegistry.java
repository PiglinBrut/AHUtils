package ru.pb.ahutils.registry;

import io.redspace.ironsspellbooks.api.attribute.MagicPercentAttribute;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.pb.ahutils.AHUtils;

@EventBusSubscriber(modid = AHUtils.MOD_ID)
public class AttributeRegistry {
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, AHUtils.MOD_ID);

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }

    @SubscribeEvent
    public static void modifyEntityAttributes(EntityAttributeModificationEvent e) {
        e.getTypes().forEach((entity) -> ATTRIBUTES.getEntries().forEach((attribute) -> e.add(entity, attribute)));
    }

    private static DeferredHolder<Attribute, Attribute> newResistanceAttribute(String id) {
        return ATTRIBUTES.register(id + "_magic_resist", () -> (new MagicPercentAttribute("attribute.ahutils." + id + "_magic_resist", (double)1.0F, (double)-100.0F, (double)100.0F)).setSyncable(true));
    }

    private static DeferredHolder<Attribute, Attribute> newPowerAttribute(String id) {
        return ATTRIBUTES.register(id + "_spell_power", () -> (new MagicPercentAttribute("attribute.ahutils." + id + "_spell_power", (double)1.0F, (double)-100.0F, (double)100.0F)).setSyncable(true));
    }

    public static final DeferredHolder<Attribute, Attribute> EARTH_MAGIC_RESIST = newResistanceAttribute("earth");
    public static final DeferredHolder<Attribute, Attribute> EARTH_SPELL_POWER = newPowerAttribute("earth");
    public static final DeferredHolder<Attribute, Attribute> AIR_MAGIC_RESIST = newResistanceAttribute("air");
    public static final DeferredHolder<Attribute, Attribute> AIR_SPELL_POWER = newPowerAttribute("air");
}
