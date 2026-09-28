package net.xavier.neomagiccircles.config;

import net.xavier.neomagiccircles.types.CirclesStyle;

public class CircleSettings {
    public static CirclesStyle getCircleStyle() {
        String styleName = NeoMagicCirclesConfig.CIRCLES_STYLE.get();

        if (styleName.equalsIgnoreCase(CirclesStyle.OLD.name)) {
            return CirclesStyle.OLD;
        }

        if (!styleName.equalsIgnoreCase(CirclesStyle.NEON.name)) {
            NeoMagicCirclesConfig.CIRCLES_STYLE.set(CirclesStyle.NEON.name);
        }

        return CirclesStyle.NEON;
    }

    public static float xOffsetFromCross() { return NeoMagicCirclesConfig.X_OFFSET_FROM_CROSS.get().floatValue(); }
    public static float yOffsetFromCross() { return NeoMagicCirclesConfig.Y_OFFSET_FROM_CROSS.get().floatValue(); }
    public static float zOffsetFromCross() { return NeoMagicCirclesConfig.Z_OFFSET_FROM_CROSS.get().floatValue(); }

    public static float xOffsetFromView() { return NeoMagicCirclesConfig.X_OFFSET_FROM_VIEW.get().floatValue(); }
    public static float yOffsetFromView() { return NeoMagicCirclesConfig.Y_OFFSET_FROM_VIEW.get().floatValue(); }
    public static float zOffsetFromView() { return NeoMagicCirclesConfig.Z_OFFSET_FROM_VIEW.get().floatValue(); }

    public static float yOffsetFromPlayerBottom() { return NeoMagicCirclesConfig.Y_OFFSET_FROM_PLAYER_BOTTOM.get().floatValue(); }
    public static float yOffsetFromEntityBottom() { return NeoMagicCirclesConfig.Y_OFFSET_FROM_ENTITY_BOTTOM.get().floatValue(); }

    private CircleSettings() {}
}
