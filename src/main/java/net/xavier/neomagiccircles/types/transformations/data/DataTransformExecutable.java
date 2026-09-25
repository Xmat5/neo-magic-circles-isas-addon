package net.xavier.neomagiccircles.types.transformations.data;

import net.xavier.neomagiccircles.types.EntitySnapshot;
import net.xavier.neomagiccircles.types.magiccircle.MagicCircleData;

@FunctionalInterface
public interface DataTransformExecutable {
    void execute(EntitySnapshot entitySnapshot,
                 MagicCircleData data,
                 float ticksDifferenceFromLastCall,
                 float passedTransformFullTicks);
}
