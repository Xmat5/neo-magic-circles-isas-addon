package net.xavier.neomagiccircles.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.xavier.neomagiccircles.render.MagicCirclesRender;

public class MagicCircleEntityRenderer extends EntityRenderer<MagicCircleEntity> {
    public MagicCircleEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(MagicCircleEntity entity,
                       float entityYaw,
                       float newPartialTick,
                       PoseStack poseStack,
                       MultiBufferSource bufferSource,
                       int packedLight) {
        MagicCirclesRender.renderMagicCircleForClient(entity, newPartialTick, poseStack, bufferSource);
    }

    @Override
    public ResourceLocation getTextureLocation(MagicCircleEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/ghast/ghast.png");
    }
}
