package net.xavier.neomagiccircles.types.transformations.render;

import com.mojang.math.Axis;

public class RenderAnimations {
    public static RenderTransformExecutable getCurrentRotationExecutable() {
        return (poseStack, entitySnapshot, magicCircleData, partialTick) ->
                poseStack.mulPose(Axis.ZP.rotationDegrees(magicCircleData.getRotation()));
    }

    public static RenderTransformExecutable getCurrentFacingRotationExecutable() {
        return (poseStack, entitySnapshot, magicCircleData, partialTick) -> {
            poseStack.mulPose(Axis.YP.rotationDegrees(magicCircleData.getYRotation()));
            poseStack.mulPose(Axis.XP.rotationDegrees(magicCircleData.getXRotation()));
        };
    }

    public static RenderTransformExecutable getCurrentSizeScalingExecutable() {
        return (poseStack, entitySnapshot, magicCircleData, partialTick) -> {
            float appliedSize = magicCircleData.getCurrentSize();

            poseStack.scale(appliedSize, appliedSize, 1);
        };
    }

    public static RenderTransformExecutable getSyncedPositionedExecutable(boolean includeOffset) {
        return (poseStack, entitySnapshot, magicCircleData, partialTick) -> {
            if (includeOffset)
                poseStack.translate(magicCircleData.getX() + magicCircleData.getXOffset(),
                        magicCircleData.getY() + magicCircleData.getYOffset(),
                        magicCircleData.getZ() + magicCircleData.getZOffset());
            else
                poseStack.translate(magicCircleData.getX(),
                        magicCircleData.getY(),
                        magicCircleData.getZ());
        };
    }
}
