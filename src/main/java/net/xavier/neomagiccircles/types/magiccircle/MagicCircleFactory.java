package net.xavier.neomagiccircles.types.magiccircle;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.xavier.neomagiccircles.MagicCircleManager;
import net.xavier.neomagiccircles.Utils;
import net.xavier.neomagiccircles.config.CircleOverrides;
import net.xavier.neomagiccircles.config.CircleSettings;
import net.xavier.neomagiccircles.types.CirclesStyle;
import net.xavier.neomagiccircles.types.transformations.TransformManager;
import net.xavier.neomagiccircles.types.transformations.data.DataTransformAnimations;
import net.xavier.neomagiccircles.types.transformations.render.RenderAnimations;

import java.util.UUID;

public class MagicCircleFactory {
    private static final ResourceLocation[] TEXTURES_PER_SIZE = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath("neomagiccircles", "textures/circles/old/circle_1.png"),
            ResourceLocation.fromNamespaceAndPath("neomagiccircles", "textures/circles/old/circle_2.png"),
            ResourceLocation.fromNamespaceAndPath("neomagiccircles", "textures/circles/old/circle_3.png"),
            ResourceLocation.fromNamespaceAndPath("neomagiccircles", "textures/circles/old/circle_4.png"),
            ResourceLocation.fromNamespaceAndPath("neomagiccircles", "textures/circles/old/circle_5.png")
    };

    private static final ResourceLocation[] TEXTURES_NEON_PER_SIZE = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath("neomagiccircles", "textures/circles/old/circle_1.png"),
            ResourceLocation.fromNamespaceAndPath("neomagiccircles", "textures/circles/neon/circle_2_neon.png"),
            ResourceLocation.fromNamespaceAndPath("neomagiccircles", "textures/circles/neon/circle_3_neon.png"),
            ResourceLocation.fromNamespaceAndPath("neomagiccircles", "textures/circles/neon/circle_4_neon.png"),
            ResourceLocation.fromNamespaceAndPath("neomagiccircles", "textures/circles/old/circle_5.png")
    };

    private static final float[] SIZE_INDEX_BY_TIME = new float[]{1f, 1.9f, 2.2f, 4f, 6f};

    public static final int HAND_CIRCLE_FADE_IN_TICKS = 4;
    public static final int UNDER_PLAYER_FADE_IN_TICKS = 24;

    public static final int HAND_CIRCLE_FADE_OUT_TICKS = 4;
    public static final int UNDER_PLAYER_FADE_OUT_TICKS = 6;

    public static MagicCircleData buildMagicCircleData(String spellName,
                                                       LivingEntity caster,
                                                       MagicCircleManager.CastInfo castInfo,
                                                       UUID circleEntityUUID) {
        int circleType = Mth.clamp(castInfo.circleType() - 1, 0, 4);
        AbstractSpell spell = castInfo.spell();
        ResourceLocation usedTexture;

        if (CircleSettings.CIRCLES_STYLE == CirclesStyle.NEON) {
            usedTexture = TEXTURES_NEON_PER_SIZE[circleType];
        } else {
            usedTexture = TEXTURES_PER_SIZE[circleType];
        }

        TransformManager animationManager = new TransformManager();
        float usedSize = SIZE_INDEX_BY_TIME[circleType];
        float xOffset, zOffset, yOffset;
        float rotationChangePerTick;
        int usedFadeInTicks, usedFadeOutTicks;
        int color = CircleOverrides.resolveColorOverwrite(spellName, spell.getSchoolType());
        float[] colorRGB = Utils.extractRGBFromHexColor(color);
        float[] brighterColor = Utils.brighten(colorRGB[0], colorRGB[1], colorRGB[2], 0.2f);

        if (circleType > 2) {
            if (caster instanceof LocalPlayer) {
                yOffset = CircleSettings.Y_OFFSET_FROM_PLAYER_BOTTOM;
            } else {
                yOffset = CircleSettings.Y_OFFSET_FROM_ENTITY_BOTTOM;
            }

            xOffset = 0;
            zOffset = 0;
            usedFadeInTicks = UNDER_PLAYER_FADE_IN_TICKS;
            usedFadeOutTicks = UNDER_PLAYER_FADE_OUT_TICKS;
            rotationChangePerTick = 5f;

            int targetSizeScaling;
            if (circleType == 4) {
                targetSizeScaling = 30;
            } else {
                targetSizeScaling = 16;
            }

            animationManager.addInitTransformation(DataTransformAnimations.getGradualOpacityChangeExecutable(1f, 10));
            animationManager.addInitTransformation(DataTransformAnimations.getGradualScalingExecutable(targetSizeScaling, UNDER_PLAYER_FADE_IN_TICKS));
            animationManager.addInitTransformation(DataTransformAnimations.getGradualRotationPerTick(2f, 6, UNDER_PLAYER_FADE_IN_TICKS));

            animationManager.addPermanentDataTransformation(DataTransformAnimations.getGroundFacingExecutable());
            animationManager.addPermanentDataTransformation(DataTransformAnimations.getConstantRotatedCircleExecutable());

            animationManager.addPermanentRenderTransformation(RenderAnimations.getSyncedPositionedExecutable(true));
            animationManager.addPermanentRenderTransformation(RenderAnimations.getCurrentFacingRotationExecutable());
            animationManager.addPermanentRenderTransformation(RenderAnimations.getCurrentSizeScalingExecutable());
            animationManager.addPermanentRenderTransformation(RenderAnimations.getCurrentRotationExecutable());

            animationManager.addFinalDataTransformation(DataTransformAnimations.getGradualOpacityChangeExecutable(0.35f, UNDER_PLAYER_FADE_OUT_TICKS));
        } else {
            if (caster instanceof LocalPlayer) {
                zOffset = CircleSettings.Z_OFFSET_FROM_CROSS;
                xOffset = CircleSettings.X_OFFSET_FROM_CROSS;
                yOffset = CircleSettings.Y_OFFSET_FROM_CROSS;
            } else {
                zOffset = CircleSettings.Z_OFFSET_FROM_VIEW;
                xOffset = CircleSettings.X_OFFSET_FROM_VIEW;
                yOffset = CircleSettings.Y_OFFSET_FROM_VIEW;
            }

            usedFadeInTicks = HAND_CIRCLE_FADE_IN_TICKS;
            usedFadeOutTicks = HAND_CIRCLE_FADE_OUT_TICKS;
            rotationChangePerTick = 3f;

            animationManager.addInitTransformation(DataTransformAnimations.getGradualOpacityChangeExecutable(1f, HAND_CIRCLE_FADE_IN_TICKS));

            animationManager.addPermanentDataTransformation(DataTransformAnimations.getFacingCasterViewExecutable());
            animationManager.addPermanentDataTransformation(DataTransformAnimations.getConstantRotatedCircleExecutable());
            animationManager.addPermanentDataTransformation(DataTransformAnimations.getCasterBillboardPositionExecutable());

            animationManager.addPermanentRenderTransformation(RenderAnimations.getSyncedPositionedExecutable(false));
            animationManager.addPermanentRenderTransformation(RenderAnimations.getCurrentFacingRotationExecutable());
            animationManager.addPermanentRenderTransformation(RenderAnimations.getCurrentSizeScalingExecutable());
            animationManager.addPermanentRenderTransformation(RenderAnimations.getCurrentRotationExecutable());

            animationManager.addFinalDataTransformation(DataTransformAnimations.getGradualOpacityChangeExecutable(0.35f, HAND_CIRCLE_FADE_OUT_TICKS));
        }

        return new MagicCircleData(animationManager,
                caster,
                spellName,
                brighterColor,
                usedTexture,
                usedSize,
                rotationChangePerTick,
                xOffset,
                zOffset,
                yOffset,
                usedFadeInTicks,
                usedFadeOutTicks,
                circleEntityUUID);
    }
}
