package ru.pb.ahutils;

import com.bobmowzie.mowziesmobs.server.creativetab.CreativeTabHandler;
import com.bobmowzie.mowziesmobs.server.item.ItemHandler;
import io.redspace.ironsspellbooks.registries.CreativeTabRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import ru.pb.ahutils.client.render.PermafrostGateRenderer;
import ru.pb.ahutils.registry.*;
import ru.pb.ahutils.util.item.SpearItem;

import static io.redspace.ironsspellbooks.registries.ItemRegistry.*;

@Mod(AHUtils.MOD_ID)
public class AHUtils {
    public static final String MOD_ID = "ahutils";
    public static final Logger LOGGER = LogUtils.getLogger();
    public AHUtils(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);

        ItemRegistry.register(modEventBus);

        SchoolRegistry.register(modEventBus);

        SpellRegistry.register(modEventBus);

        SoundRegistry.register(modEventBus);

        AttributeRegistry.register(modEventBus);

        TabRegistry.register(modEventBus);

        EntityRegistry.register(modEventBus);
    }

    @SubscribeEvent
    public void onItemAttributeModifiers(ItemAttributeModifierEvent event) {
        new SpearItem.SpearAttributeHandler().onItemAttributeModifiers(event);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeTabHandler.CREATIVE_TAB.getKey()) {
            event.insertAfter(new ItemStack(ItemHandler.EARTHREND_GAUNTLET), ItemRegistry.SCULPTOR_STAFF.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(ItemHandler.SAND_RAKE), ItemRegistry.GEOMANCER_SANDALS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(ItemHandler.SAND_RAKE), ItemRegistry.GEOMANCER_BELT.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(ItemHandler.SAND_RAKE), ItemRegistry.GEOMANCER_ROBE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(ItemHandler.SAND_RAKE), ItemRegistry.GEOMANCER_BEADS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }if (event.getTabKey() == CreativeTabRegistry.EQUIPMENT_TAB.getKey()) {
            event.insertAfter(new ItemStack(LIGHTNING_ROD_STAFF), ItemRegistry.SCULPTOR_STAFF.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(PLAGUED_BOOTS), ItemRegistry.GEOMANCER_SANDALS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(PLAGUED_BOOTS), ItemRegistry.GEOMANCER_BELT.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(PLAGUED_BOOTS), ItemRegistry.GEOMANCER_ROBE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(PLAGUED_BOOTS), ItemRegistry.GEOMANCER_BEADS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        if (event.getTabKey() == CreativeTabRegistry.MATERIALS_TAB.getKey()) {
            event.insertAfter(new ItemStack(NATURE_RUNE), ItemRegistry.EARTH_RUNE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(new ItemStack(NATURE_UPGRADE_ORB), ItemRegistry.EARTH_UPGRADE_ORB.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onEntityRenderersEvent(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(EntityRegistry.PERMAFROST_GATE.get(), PermafrostGateRenderer::new);
        }
    }
}
