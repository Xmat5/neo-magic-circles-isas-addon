package net.xavier.neomagiccircles.types.transformations.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.xavier.neomagiccircles.types.EntitySnapshot;
import net.xavier.neomagiccircles.types.magiccircle.MagicCircleData;

public interface RenderTransformExecutable {
    void execute(PoseStack poseStack,
                 EntitySnapshot entitySnapshot,
                 MagicCircleData data,
                 float partialTick);
}
