package net.xavier.neomagiccircles.config;

import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SchoolType;

public class CircleOverrides {
    public static int resolveColorOverwrite(String spellName, SchoolType school) {
        return ConfigCache.resolveColorOverwrite(spellName, school);
    }

    public static int resolveCircleTypeOverwrite(String spellId,
                                                  SchoolType school,
                                                  int castTime,
                                                  CastType castType) {
        return ConfigCache.resolveCircleTypeOverwrite(spellId, school, castTime, castType);
    }

    private CircleOverrides() {}
}
