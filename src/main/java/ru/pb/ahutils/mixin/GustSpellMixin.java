package ru.pb.ahutils.mixin;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.spells.evocation.GustSpell;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.pb.ahutils.registry.SchoolRegistry;

@Mixin(GustSpell.class)
public class GustSpellMixin {

    @Inject(method = "getDefaultConfig", at = @At("RETURN"), cancellable = true)
    private void getDefaultConfig(CallbackInfoReturnable<DefaultConfig> cir) {
        cir.setReturnValue((new DefaultConfig())
                .setMinRarity(SpellRarity.UNCOMMON)
                .setSchoolResource(SchoolRegistry.AIR_RESOURCE)
                .setMaxLevel(10)
                .setCooldownSeconds(12D)
                .build());
    }
}
