package ru.pb.ahutils.mixin;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.spells.nature.StompSpell;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.pb.ahutils.registry.SchoolRegistry;

@Mixin(StompSpell.class)
public class StompSpellMixin {

    @Inject(method = "getDefaultConfig", at = @At("RETURN"), cancellable = true)
    private void getDefaultConfig(CallbackInfoReturnable<DefaultConfig> cir) {
        cir.setReturnValue((new DefaultConfig())
                .setMinRarity(SpellRarity.UNCOMMON)
                .setSchoolResource(SchoolRegistry.EARTH_RESOURCE)
                .setMaxLevel(5)
                .setCooldownSeconds(16.0D)
                .build());
    }
}
