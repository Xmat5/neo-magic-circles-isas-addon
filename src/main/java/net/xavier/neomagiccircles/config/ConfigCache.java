package net.xavier.neomagiccircles.config;

import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.xavier.neomagiccircles.Utils;
import net.xavier.neomagiccircles.logger.ModLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalInt;

public class ConfigCache {
    private static final String IRONS_NAMESPACE = "irons_spellbooks";

    private static volatile Map<String, Integer> cachedColorOverrides = null;
    private static volatile Map<String, Integer> cachedCircleOverrides = null;

    public static void invalidateCache() {
        cachedColorOverrides = null;
        cachedCircleOverrides = null;
    }

    public static int resolveColorOverwrite(String spellName, SchoolType school) {
        ensureColorCacheBuilt();

        Integer color;
        if (spellName != null && (color = cachedColorOverrides.get(normalizeKey(spellName))) != null) {
            return color;
        }
        if (school != null && (color = cachedColorOverrides.get(schoolKey(school))) != null) {
            return color;
        }
        return Utils.getColorFromSchool(school);
    }

    public static int resolveCircleTypeOverwrite(String spellId, SchoolType school, int castTime, CastType castType) {
        ensureCircleCacheBuilt();

        Integer idx;
        if (spellId != null && (idx = cachedCircleOverrides.get(normalizeKey(spellId))) != null) {
            return idx;
        }
        idx = cachedCircleOverrides.get(schoolKey(school));
        if (idx != null) {
            return idx;
        }
        return resolveCircleTypeByCastTimeAndType(castTime, castType);
    }

    private static int resolveCircleTypeByCastTimeAndType(int totalCastTime, CastType castType) {
        int circleType = Mth.clamp(totalCastTime / 20, 1, 5);
        if (castType != CastType.LONG) {
            circleType = Math.min(3, circleType);
        }
        return circleType;
    }

    public static void ensureColorCacheBuilt() {
        if (cachedColorOverrides != null) {
            return;
        }

        List<? extends String> originalEntries = NeoMagicCirclesConfig.COLOR_OVERWRITES.get();
        ArrayList<String> correctedEntries = new ArrayList<>(originalEntries);
        boolean anyKeyCorrection = false;
        HashMap<String, Integer> map = new HashMap<>();

        for (int i = 0; i < originalEntries.size(); i++) {
            String entry = originalEntries.get(i);
            int commaIdx = entry.indexOf(',');
            if (commaIdx < 0) {
                ModLogger.warn("[magiccircles-config] colorOverwrites: entry \"{}\" has no ',' separator, skipping.", entry);
                continue;
            }

            String key = normalizeKey(entry.substring(0, commaIdx));
            String rawValue = entry.substring(commaIdx + 1).trim();

            if (!isValidResourceLocation(key)) {
                String correctedKey = IRONS_NAMESPACE + ":" + key;
                String correctedEntry = correctedKey + "," + rawValue;
                ModLogger.info("[magiccircles-config] colorOverwrites: entry \"{}\" - key '{}' has no namespace; auto-correcting to \"{}\". The config file will be updated.",
                        entry, key, correctedEntry);
                correctedEntries.set(i, correctedEntry);
                key = correctedKey;
                anyKeyCorrection = true;
            }

            OptionalInt colorOpt = parseHexColor(rawValue, entry);
            if (colorOpt.isEmpty()) continue;

            map.put(key, colorOpt.getAsInt());
        }

        if (anyKeyCorrection) {
            NeoMagicCirclesConfig.COLOR_OVERWRITES.set(correctedEntries);
        }

        cachedColorOverrides = Collections.unmodifiableMap(map);
    }

    public static void ensureCircleCacheBuilt() {
        if (cachedCircleOverrides != null) {
            return;
        }

        List<? extends String> originalEntries = NeoMagicCirclesConfig.CIRCLE_TYPE_OVERWRITES.get();
        ArrayList<String> correctedEntries = new ArrayList<>(originalEntries);
        boolean anyKeyCorrection = false;
        HashMap<String, Integer> map = new HashMap<>();

        for (int i = 0; i < originalEntries.size(); i++) {
            String entry = originalEntries.get(i);
            int commaIdx = entry.indexOf(',');
            if (commaIdx < 0) {
                ModLogger.warn("[magiccircles-config] circleTypeOverwrites: entry \"{}\" has no ',' separator, skipping.", entry);
                continue;
            }

            String key = normalizeKey(entry.substring(0, commaIdx));
            String rawValue = entry.substring(commaIdx + 1).trim();

            if (!isValidResourceLocation(key)) {
                String correctedKey = IRONS_NAMESPACE + ":" + key;
                String correctedEntry = correctedKey + "," + rawValue;
                ModLogger.warn("[magiccircles-config] circleTypeOverwrites: entry \"{}\" - key '{}' has no namespace; auto-correcting to \"{}\". The config file will be updated.",
                        entry, key, correctedEntry);
                correctedEntries.set(i, correctedEntry);
                key = correctedKey;
                anyKeyCorrection = true;
            }

            int circleType;
            try {
                circleType = Integer.parseInt(rawValue);
            } catch (NumberFormatException e) {
                ModLogger.warn("[magiccircles-config] circleTypeOverwrites: entry \"{}\" has non-numeric value '{}', skipping.", entry, rawValue);
                continue;
            }

            map.put(key, circleType);
        }

        if (anyKeyCorrection) {
            NeoMagicCirclesConfig.CIRCLE_TYPE_OVERWRITES.set(correctedEntries);
        }

        cachedCircleOverrides = Collections.unmodifiableMap(map);
    }

    private static boolean isValidResourceLocation(String normalizedKey) {
        return normalizedKey.contains(":");
    }

    private static String normalizeKey(String raw) {
        return raw.trim().toLowerCase(Locale.ROOT);
    }

    private static String schoolKey(SchoolType school) {
        ResourceLocation rl = school.getId();
        if (rl != null) {
            return rl.toString().toLowerCase(Locale.ROOT);
        }
        ModLogger.warn("[magiccircles-config] SchoolType '{}' has no registry name; school-based overwrites will not match for this school.",
                school.getDisplayName().getString());
        return school.getDisplayName().getString().trim().toLowerCase(Locale.ROOT);
    }

    private static OptionalInt parseHexColor(String rawValue, String fullEntry) {
        String hex = rawValue;
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        } else if (hex.toLowerCase(Locale.ROOT).startsWith("0x")) {
            hex = hex.substring(2);
        }

        if (hex.length() != 6) {
            ModLogger.warn("[magiccircles-config] colorOverwrites: entry \"{}\" - '{}' is not a valid 6-digit hex color (expected RRGGBB, #RRGGBB, or 0xRRGGBB). Entry will be ignored.",
                    fullEntry, rawValue);
            return OptionalInt.empty();
        }

        try {
            return OptionalInt.of(Integer.parseInt(hex, 16));
        } catch (NumberFormatException e) {
            ModLogger.warn("[magiccircles-config] colorOverwrites: entry \"{}\" - '{}' contains invalid hex characters. Entry will be ignored.",
                    fullEntry, rawValue);
            return OptionalInt.empty();
        }
    }

    private ConfigCache() {}
}
