package net.xavier.neomagiccircles.config;

import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.xavier.neomagiccircles.Utils;
import net.xavier.neomagiccircles.logger.ModLogger;

import java.util.Locale;
import java.util.Map;

public class CircleOverrides {
    private static final Map<String, Integer> COLOR_OVERWRITES = Map.of();

    private static final Map<String, Integer> CIRCLE_TYPE_OVERWRITES = Map.of(
            "tunes_n_tomes:melody", 0
    );

    public static int resolveColorOverwrite(String spellName, SchoolType school) {
        if (spellName != null) {
            Integer color = COLOR_OVERWRITES.get(normalizeKey(spellName));
            if (color != null) return color;
        }

        if (school != null) {
            Integer color = COLOR_OVERWRITES.get(schoolKey(school));
            if (color != null) return color;
        }

        return Utils.getColorFromSchool(school);
    }

    public static int resolveCircleTypeOverwrite(String spellId,
                                                  SchoolType school,
                                                  int castTime,
                                                  CastType castType) {
        Integer idx;

        if (spellId != null) {
            idx = CIRCLE_TYPE_OVERWRITES.get(normalizeKey(spellId));
            if (idx != null) return idx;
        }

        idx = CIRCLE_TYPE_OVERWRITES.get(schoolKey(school));
        if (idx != null) return idx;

        return resolveCircleTypeByCastTimeAndType(castTime, castType);
    }

    private static int resolveCircleTypeByCastTimeAndType(int totalCastTime, CastType castType) {
        int circleType = Mth.clamp((totalCastTime / 20), 1, 5);

        if (castType != CastType.LONG)
            circleType = Math.min(3, circleType);

        return circleType;
    }

    private static String normalizeKey(String raw) {
        return raw.trim().toLowerCase(Locale.ROOT);
    }

    private static String schoolKey(SchoolType school) {
        ResourceLocation rl = school.getId();
        if (rl != null) {
            return rl.toString().toLowerCase(Locale.ROOT);
        }
        ModLogger.warn("[magiccircles] SchoolType '{}' has no registry name; school-based overwrites will not match for this school.",
                school.getDisplayName().getString());
        return school.getDisplayName().getString().trim().toLowerCase(Locale.ROOT);
    }
}
