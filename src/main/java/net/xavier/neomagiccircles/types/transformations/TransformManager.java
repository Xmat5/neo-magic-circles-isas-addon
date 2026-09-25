package net.xavier.neomagiccircles.types.transformations;

import com.mojang.blaze3d.vertex.PoseStack;
import net.xavier.neomagiccircles.types.EntitySnapshot;
import net.xavier.neomagiccircles.types.magiccircle.MagicCircleData;
import net.xavier.neomagiccircles.types.transformations.data.DataTransformExecutable;
import net.xavier.neomagiccircles.types.transformations.render.RenderTransformExecutable;

import java.util.ArrayList;

public class TransformManager {
    private final ArrayList<DataTransformExecutable> initTransformations = new ArrayList<>();

    private final ArrayList<DataTransformExecutable> finalDataTransformations = new ArrayList<>();

    private final ArrayList<DataTransformExecutable> dataTransformations = new ArrayList<>();

    private final ArrayList<RenderTransformExecutable> untilFinalRenderTransformations = new ArrayList<>();

    private final ArrayList<RenderTransformExecutable> finalRenderTransformations = new ArrayList<>();

    private final ArrayList<RenderTransformExecutable> renderTransformations = new ArrayList<>();

    public void executeInitTransformations(EntitySnapshot entitySnapshot,
                                           MagicCircleData data,
                                           float ticksDifferenceFromLastCall,
                                           float passedTransformFullTicks) {
        this.initTransformations.forEach(animation -> animation.execute(
                entitySnapshot,
                data,
                ticksDifferenceFromLastCall,
                passedTransformFullTicks));
    }

    public void executeFinalDataTransformations(EntitySnapshot entitySnapshot,
                                                MagicCircleData data,
                                                float ticksDifferenceFromLastCall,
                                                float passedTransformFullTicks) {
        this.finalDataTransformations.forEach(animation -> animation.execute(
                entitySnapshot,
                data,
                ticksDifferenceFromLastCall,
                passedTransformFullTicks));
    }

    public void executePermanentDataTransformations(EntitySnapshot entitySnapshot,
                                                    MagicCircleData data,
                                                    float ticksDifferenceFromLastCall,
                                                    float passedTransformFullTicks) {
        this.dataTransformations.forEach(animation -> animation.execute(
                entitySnapshot,
                data,
                ticksDifferenceFromLastCall,
                passedTransformFullTicks));
    }

    public void executeUntilFinalRenderTransformations(PoseStack poseStack,
                                                       EntitySnapshot entitySnapshot,
                                                       MagicCircleData data,
                                                       float partialTicks) {
        this.untilFinalRenderTransformations.forEach(animation -> animation.execute(
                poseStack,
                entitySnapshot,
                data,
                partialTicks));
    }

    public void executeFinalRenderTransformations(PoseStack poseStack,
                                             EntitySnapshot entitySnapshot,
                                             MagicCircleData data,
                                             float partialTicks) {
        this.finalRenderTransformations.forEach(animation -> animation.execute(
                poseStack,
                entitySnapshot,
                data,
                partialTicks));
    }

    public void executeRenderTransformations(PoseStack poseStack,
                                             EntitySnapshot entitySnapshot,
                                             MagicCircleData data,
                                             float partialTicks) {
        this.renderTransformations.forEach(animation -> animation.execute(
                poseStack,
                entitySnapshot,
                data,
                partialTicks));
    }

    public void addInitTransformation(DataTransformExecutable executable) {
        this.initTransformations.add(executable);
    }

    public void addFinalDataTransformation(DataTransformExecutable executable) {
        this.finalDataTransformations.add(executable);
    }

    public void addPermanentDataTransformation(DataTransformExecutable executable) {
        this.dataTransformations.add(executable);
    }

    public void addPermanentRenderTransformation(RenderTransformExecutable executable) {
        this.renderTransformations.add(executable);
    }

    public void addUntilFinalRenderTransformation(RenderTransformExecutable executable) {
        this.untilFinalRenderTransformations.add(executable);
    }

    public void addFinalRenderTransformation(RenderTransformExecutable executable) {
        this.finalRenderTransformations.add(executable);
    }
}
