package ru.pb.ahutils.mixin;

import com.github.L_Ender.cataclysm.blocks.Statue_Block;
import com.github.L_Ender.cataclysm.client.model.entity.Ignis_Model;
import com.github.L_Ender.cataclysm.client.render.blockentity.Goddess_Statue_Renderer;
import com.github.L_Ender.cataclysm.blockentities.Statue_Block_Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Random;

@Mixin(Goddess_Statue_Renderer.class)
public class GoddessStatueRendererMixin {

    @Unique
    private static final ResourceLocation RARE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("cataclysm", "textures/entity/ignis/ignis_idle_0.png");

    @Unique
    private static final Ignis_Model RARE_MODEL = new Ignis_Model();

    @Unique
    private static final Random STATIC_RANDOM = new Random();

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(Statue_Block_Entity entity, float delta, PoseStack matrixStackIn,
                          MultiBufferSource buffer, int packedLight, int overlay, CallbackInfo ci) {
        if (isRareStatue(entity)) {
            ci.cancel();
            renderRareStatue(entity, matrixStackIn, buffer, packedLight, overlay);
        }
    }

    @Unique
    private boolean isRareStatue(Statue_Block_Entity entity) {
        BlockPos pos = entity.getBlockPos();

        if (entity.getBlockState().getValue(Statue_Block.HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.below();
        }

        long seed = pos.asLong();
        long xorshift = seed;
        xorshift ^= xorshift << 13;
        xorshift ^= xorshift >>> 17;
        xorshift ^= xorshift << 5;

        return (Math.abs(xorshift) % 100) < 10;
    }

    @Unique
    private void renderRareStatue(Statue_Block_Entity entity, PoseStack matrixStackIn,
                                  MultiBufferSource buffer, int packedLight, int overlay) {
        if (entity.getBlockState().getValue(Statue_Block.HALF) == DoubleBlockHalf.LOWER) {
            matrixStackIn.pushPose();
            float f = entity.getBlockState().getValue(Statue_Block.FACING).toYRot();
            matrixStackIn.translate(0.5F, 1.5F, 0.5F);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-f + 180.0F));
            matrixStackIn.scale(-1.0F, -1.0F, 1.0F);

            RARE_MODEL.renderToBuffer(
                    matrixStackIn,
                    buffer.getBuffer(RenderType.entityCutoutNoCull(RARE_TEXTURE)),
                    packedLight,
                    overlay
            );

            matrixStackIn.popPose();
        }
    }
}
