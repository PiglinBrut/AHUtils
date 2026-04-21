package ru.pb.ahutils.util.item;

import com.bobmowzie.mowziesmobs.server.item.ItemHandler;
import com.bobmowzie.mowziesmobs.server.item.MaterialHandler;
import io.redspace.ironsspellbooks.item.armor.IDisableJacket;
import io.redspace.ironsspellbooks.item.armor.ImbuableChestplateArmorItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import ru.pb.ahutils.client.render.RenderGeomancerArmor;
import ru.pb.ahutils.registry.AttributeRegistry;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.List;

public class GeomancerArmorItem extends ImbuableChestplateArmorItem implements IDisableJacket {
    public GeomancerArmorItem(ArmorItem.Type slot, Item.Properties settings) {
        super(MaterialHandler.GEOMANCER_ARMOR_MATERIAL, slot, settings, schoolAttributes(AttributeRegistry.EARTH_SPELL_POWER));
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        tooltip.add(Component.translatable(this.getDescriptionId().replace("ahutils", "mowziesmobs") + ".text.0").setStyle(ItemHandler.TOOLTIP_STYLE));
    }

    @OnlyIn(Dist.CLIENT)
    public GeoArmorRenderer<?> supplyRenderer() {
        return new RenderGeomancerArmor();
    }
}
