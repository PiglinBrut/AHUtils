package ru.pb.ahutils.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import ru.pb.ahutils.AHUtils;
import ru.pb.ahutils.entity.PermafrostGate;

public class PermafrostGateRenderer extends EntityRenderer<PermafrostGate> {

    private static final ResourceLocation PORTAL_TEXTURE = AHUtils.id("textures/entity/permafrost_gate.png");
    private static final ResourceLocation BEAM_TEXTURE = AHUtils.id("textures/entity/permafrost_beam.png");
    private static final float HALF_SQRT_3 = (float) (Math.sqrt(3.0F) / 2.0F);

    public PermafrostGateRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(PermafrostGate entity, float pEntityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int pPackedLight) {

        // Центральная сфера
        poseStack.pushPose();
        poseStack.translate(0.0F, entity.getBoundingBox().getYsize() / 2.0F, 0.0F);
        float entityScale = entity.getBbWidth() * 0.025F;
        PoseStack.Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        poseStack.scale(0.5F * entityScale, 0.5F * entityScale, 0.5F * entityScale);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        poseStack.translate(5.0F, 0.0F, 0.0F);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(PORTAL_TEXTURE));

        // Рисуем центральный шар
        consumer.addVertex(poseMatrix, 0.0F, -8.0F, -8.0F).setColor(100, 150, 255, 200).setUv(0.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(poseMatrix, 0.0F, 8.0F, -8.0F).setColor(100, 150, 255, 200).setUv(0.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(poseMatrix, 0.0F, 8.0F, 8.0F).setColor(100, 150, 255, 200).setUv(1.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(poseMatrix, 0.0F, -8.0F, 8.0F).setColor(100, 150, 255, 200).setUv(1.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0.0F, 1.0F, 0.0F);

        poseStack.popPose();

        // Энергетические лучи
        poseStack.pushPose();
        poseStack.translate(0.0F, entity.getBoundingBox().getYsize() / 2.0F, 0.0F);

        float animationProgress = ((float) entity.tickCount + partialTicks) / 200.0F;
        RandomSource randomSource = RandomSource.create(432L);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.energySwirl(BEAM_TEXTURE, 0.0F, 0.0F));

        for (int i = 0; i < 60; i++) {
            poseStack.mulPose(Axis.XP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(randomSource.nextFloat() * 360.0F + animationProgress * 90.0F));

            float size = (randomSource.nextFloat() * 15.0F + 5.0F) * entityScale * 0.6F;
            Matrix4f matrix = poseStack.last().pose();
            Matrix3f normalMatrix2 = poseStack.last().normal();

            drawTriangle(vertexConsumer, matrix, normalMatrix2, size);
        }

        poseStack.popPose();
        super.render(entity, pEntityYaw, partialTicks, poseStack, bufferSource, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(PermafrostGate pEntity) {
        return PORTAL_TEXTURE;
    }

    private static void drawTriangle(VertexConsumer consumer, Matrix4f poseMatrix, Matrix3f normalMatrix, float size) {
        consumer.addVertex(poseMatrix, 0.0F, 0.0F, 0.0F).setColor(100, 150, 255, 200).setUv(0.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(poseMatrix, 0.0F, 3.0F * size, -1.0F * size).setColor(50, 100, 200, 100).setUv(0.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(poseMatrix, 0.0F, 3.0F * size, 1.0F * size).setColor(50, 100, 200, 100).setUv(1.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(poseMatrix, 0.0F, 0.0F, 0.0F).setColor(100, 150, 255, 200).setUv(1.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0.0F, 1.0F, 0.0F);
    }
}