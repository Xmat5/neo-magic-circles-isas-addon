package net.xavier.neomagiccircles.render;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.xavier.neomagiccircles.MagicCircleManager;
import net.xavier.neomagiccircles.entity.MagicCircleEntity;
import net.xavier.neomagiccircles.oculus.OculusCompact;
import net.xavier.neomagiccircles.types.EntitySnapshot;
import net.xavier.neomagiccircles.types.magiccircle.MagicCircleData;
import net.xavier.neomagiccircles.Utils;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class MagicCirclesRender extends RenderType {
    private static final Map<ResourceLocation, RenderType> RENDER_CACHE = new HashMap<>();
    private static final Map<ResourceLocation, RenderType> WATER_MASK_BLOCKER_CACHE = new HashMap<>();

    private MagicCirclesRender(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                               boolean affectsCrumbling, boolean sortOnUpload,
                               Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    /**
     * RenderType's own {@code create(..., CompositeState)} factory is private as of 1.21.1, so
     * composite render types can no longer be built from outside the package. This replicates it:
     * every shard is applied via its own {@code setupRenderState()}/{@code clearRenderState()},
     * in the same fixed order RenderType.CompositeState itself uses internally
     * (texture, shader, transparency, depthTest, cull, lightmap, overlay — the rest left at their
     * no-op defaults since this mod never sets them).
     */
    private static RenderType createFromShards(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                                                boolean affectsCrumbling, boolean sortOnUpload,
                                                RenderStateShard.EmptyTextureStateShard textureState,
                                                RenderStateShard.ShaderStateShard shaderState,
                                                RenderStateShard.TransparencyStateShard transparencyState,
                                                RenderStateShard.DepthTestStateShard depthTestState,
                                                RenderStateShard.CullStateShard cullState,
                                                RenderStateShard.LightmapStateShard lightmapState,
                                                RenderStateShard.OverlayStateShard overlayState,
                                                RenderStateShard.WriteMaskStateShard writeMaskState) {
        Runnable setupState = () -> {
            textureState.setupRenderState();
            shaderState.setupRenderState();
            transparencyState.setupRenderState();
            depthTestState.setupRenderState();
            cullState.setupRenderState();
            lightmapState.setupRenderState();
            overlayState.setupRenderState();
            writeMaskState.setupRenderState();
        };
        Runnable clearState = () -> {
            textureState.clearRenderState();
            shaderState.clearRenderState();
            transparencyState.clearRenderState();
            depthTestState.clearRenderState();
            cullState.clearRenderState();
            lightmapState.clearRenderState();
            overlayState.clearRenderState();
            writeMaskState.clearRenderState();
        };

        return new MagicCirclesRender(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static RenderType cachedCreateRenderType(ResourceLocation texture) {
        return RENDER_CACHE.computeIfAbsent(texture, tex -> {
            if (OculusCompact.isShaderPackInUse()) {
                return createFromShards(
                        "magic_circle_neo_shader",
                        DefaultVertexFormat.NEW_ENTITY,
                        VertexFormat.Mode.QUADS,
                        256,
                        false,
                        false,
                        new RenderStateShard.TextureStateShard(tex, false, false),
                        RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER,
                        ADDITIVE_TRANSPARENCY,
                        LEQUAL_DEPTH_TEST,
                        NO_CULL,
                        NO_LIGHTMAP,
                        NO_OVERLAY,
                        COLOR_DEPTH_WRITE
                );
            } else {
                return createFromShards(
                        "magic_circle_vanilla_shader",
                        DefaultVertexFormat.NEW_ENTITY,
                        VertexFormat.Mode.QUADS,
                        256,
                        false,
                        false,
                        new RenderStateShard.TextureStateShard(tex, false, false),
                        RENDERTYPE_ENTITY_DECAL_SHADER,
                        NO_TRANSPARENCY,
                        LEQUAL_DEPTH_TEST,
                        NO_CULL,
                        LIGHTMAP,
                        NO_OVERLAY,
                        COLOR_DEPTH_WRITE
                );
            }
        });
    }

    public static void renderMagicCircleForClient(MagicCircleEntity entity,
                                                  float newPartialTick,
                                                  PoseStack poseStack,
                                                  MultiBufferSource bufferSource) {
        MagicCircleData magicCircleData = MagicCircleManager.CIRCLES_DATA_BY_ID.get(entity.getUUID());

        if (magicCircleData == null || magicCircleData.isConcealed()) return;

        LivingEntity casterEntity = entity.getCaster();
        EntitySnapshot entitySnapshot = magicCircleData.caster;
        boolean isUsingShaders = OculusCompact.isShaderPackInUse();
        RenderType circleRenderType = MagicCirclesRender.cachedCreateRenderType(magicCircleData.usedTexture);

        if (!magicCircleData.isFadingOut())
            entitySnapshot.capture(casterEntity);

        magicCircleData.executeInitTransforms(entitySnapshot, newPartialTick);
        magicCircleData.executeFinalDataTransforms(entitySnapshot, newPartialTick);
        magicCircleData.executePermanentDataTransforms(entitySnapshot, newPartialTick);

        poseStack.pushPose();
        EntitySnapshot casterSnapshot = magicCircleData.caster;

        magicCircleData.executeUntilFinalRenderTransforms(poseStack, casterSnapshot, newPartialTick);
        magicCircleData.executeFinalRenderTransforms(poseStack, casterSnapshot, newPartialTick);
        magicCircleData.executePermanentRenderTransforms(poseStack, casterSnapshot, newPartialTick);

        if (isUsingShaders) {
            RenderType waterMaskRenderType = MagicCirclesRender.cachedCreateDepthRenderType(magicCircleData.usedTexture);
            MagicCirclesRender.drawWaterMaskCircle(magicCircleData, waterMaskRenderType, poseStack, bufferSource);
        }

        MagicCirclesRender.drawCircle(magicCircleData, circleRenderType, poseStack, bufferSource, !isUsingShaders);

        poseStack.popPose();

        magicCircleData.setLastFullTicks(magicCircleData.getTicks() + newPartialTick);
    }

    public static void drawCircle(MagicCircleData magicCircleData,
                                  RenderType renderType,
                                  PoseStack poseStack,
                                  MultiBufferSource bufferSource,
                                  boolean toUseAlwaysGlowingNormal) {
        float[] color = magicCircleData.getColor(true);

        Vector3f usedNormal;
        if (toUseAlwaysGlowingNormal)
            usedNormal = RendererUtils.getNormalForAlwaysGlowing(magicCircleData);
        else {
            usedNormal = new Vector3f();
            poseStack.last().normal().transform(usedNormal);
        }

        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
        RendererUtils.drawQuad(poseStack,
                vertexConsumer,
                color[0],
                color[1],
                color[2],
                color[3],
                usedNormal);
    }

    public static void drawWaterMaskCircle(MagicCircleData magicCircleData,
                                           RenderType renderType,
                                           PoseStack poseStack,
                                           MultiBufferSource bufferSource) {
        Vector3f usedNormal = new Vector3f();
        poseStack.last().normal().transform(usedNormal);
        float[] color = magicCircleData.getColor(true);
        color = Utils.getColorDesaturationByOpacity(color[0], color[1], color[2], 0.6f);

        VertexConsumer vc = bufferSource.getBuffer(renderType);
        RendererUtils.drawQuad(poseStack, vc, color[0], color[1], color[2], 1, usedNormal);
    }

    public static void clearCache() {
        RENDER_CACHE.clear();
        WATER_MASK_BLOCKER_CACHE.clear();
    }

    public static RenderType cachedCreateDepthRenderType(ResourceLocation texture) {
        return WATER_MASK_BLOCKER_CACHE.computeIfAbsent(texture, tex -> createFromShards(
                "magic_circle_neo_depth_shader",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                256,
                false,
                false,
                new RenderStateShard.TextureStateShard(tex, false, false),
                RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER,
                NO_TRANSPARENCY,
                LEQUAL_DEPTH_TEST,
                NO_CULL,
                NO_LIGHTMAP,
                NO_OVERLAY,
                COLOR_DEPTH_WRITE
        ));
    }
}
