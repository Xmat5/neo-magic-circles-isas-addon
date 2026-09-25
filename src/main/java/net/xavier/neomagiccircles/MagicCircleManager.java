package net.xavier.neomagiccircles;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.xavier.neomagiccircles.config.CircleOverrides;
import net.xavier.neomagiccircles.entity.MagicCircleEntity;
import net.xavier.neomagiccircles.logger.ModLogger;
import net.xavier.neomagiccircles.registry.ModEntities;
import net.xavier.neomagiccircles.types.magiccircle.MagicCircleData;
import net.xavier.neomagiccircles.types.magiccircle.MagicCircleFactory;

import java.util.*;

public class MagicCircleManager {
    public record CastInfo(AbstractSpell spell, int castTime, CastType castType, int circleType) {
    }

    public static final HashMap<UUID, MagicCircleData> CIRCLES_DATA_BY_ID = new HashMap<>();

    private static final HashMap<UUID, MagicCircleEntity> CIRCLES_ENTITIES_BY_ID = new HashMap<>();
    private static final HashMap<LivingEntity, UUID> CIRCLES_IDS_BY_CASTER = new HashMap<>();
    private static final ArrayDeque<UUID> FADE_OUT_CIRCLES_IDS = new ArrayDeque<>();

    public static void handlePlayerLeaving(Player player) {
        MagicCircleData magicCircle = getMagicCircleDataFromCaster(player);

        if (magicCircle != null)
            MagicCircleManager.startMagicCircleTermination(player, magicCircle);
    }

    public static void handleClientLeaving() {
        CIRCLES_ENTITIES_BY_ID.keySet().forEach(MagicCircleManager::removeCircleEntity);

        CIRCLES_DATA_BY_ID.clear();
        CIRCLES_ENTITIES_BY_ID.clear();
        CIRCLES_IDS_BY_CASTER.clear();
        FADE_OUT_CIRCLES_IDS.clear();
    }

    public static void handleEntityLeavingLevel(LivingEntity livingEntity) {
        MagicCircleData magicCircle = getMagicCircleDataFromCaster(livingEntity);

        if (magicCircle != null)
            MagicCircleManager.startMagicCircleTermination(livingEntity, magicCircle);
    }

    public static void handleOnClientTick() {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel clientLevel = mc.level;

        if (clientLevel == null)
            return;

        updateMagicCirclesPerTick();

        for (Entity entity : clientLevel.entitiesForRendering()) {
            if (entity instanceof LivingEntity livingEntity) {
                updateMagicCircleState(livingEntity, clientLevel);
            }
        }
    }

    private static CastInfo getCastingInfo(LivingEntity entity) {
        if (entity instanceof Player player && player.equals(Minecraft.getInstance().player)) {
            if (!ClientMagicData.isCasting())
                return null;

            String spellId = ClientMagicData.getCastingSpellId();
            AbstractSpell spell = SpellRegistry.getSpell(spellId);
            int spellLevel = ClientMagicData.getCastingSpellLevel();
            int castDuration = spell.getCastTime(spellLevel);
            SchoolType schoolType = spell.getSchoolType();
            CastType castType = spell.getCastType();

            if (isShortCastSpell(castDuration, castType))
                return null;

            int circleType = CircleOverrides.resolveCircleTypeOverwrite(spellId, schoolType, castDuration, castType);

            if (isCircleRenderingDisabled(circleType))
                return null;

            return new CastInfo(spell, castDuration, castType, circleType);
        }

        SyncedSpellData syncedSpellData = ClientMagicData.getSyncedSpellData(entity);

        if (syncedSpellData == null || !syncedSpellData.isCasting())
            return null;

        String spellId = syncedSpellData.getCastingSpellId();

        if (spellId == null || spellId.isEmpty())
            return null;

        AbstractSpell spell = SpellRegistry.getSpell(spellId);

        if (spell == null || SpellRegistry.none() == spell)
            return null;

        int spellLevel = syncedSpellData.getCastingSpellLevel();
        CastType castType = spell.getCastType();
        SchoolType schoolType = spell.getSchoolType();
        int castDuration = spell.getCastTime(spellLevel);

        if (isShortCastSpell(castDuration, castType))
            return null;

        int circleType = CircleOverrides.resolveCircleTypeOverwrite(spellId, schoolType, castDuration, castType);

        if (isCircleRenderingDisabled(circleType))
            return null;

        return new CastInfo(spell, castDuration, castType, circleType);
    }

    private static boolean isShortCastSpell(int castDuration, CastType castType) {
        return castType == CastType.INSTANT || castDuration <= MagicCircleFactory.HAND_CIRCLE_FADE_IN_TICKS;
    }

    private static boolean isCircleRenderingDisabled(int circleType) {
        return circleType == 0;
    }

    private static void updateMagicCircleState(LivingEntity caster, ClientLevel clientLevel) {
        if (caster == null) return;

        CastInfo castInfo = getCastingInfo(caster);

        MagicCircleData currentActiveCircle = getMagicCircleDataFromCaster(caster);

        if (castInfo != null) {
            AbstractSpell spell = castInfo.spell();
            String usedSpellName = spell.getSpellId();

            boolean isMagicCircleRenderedForSpell = currentActiveCircle != null
                    && currentActiveCircle.castedSpellName.equals(usedSpellName);

            if (!isMagicCircleRenderedForSpell) {
                if (currentActiveCircle != null) {
                    MagicCircleManager.startMagicCircleTermination(caster, currentActiveCircle);
                }

                createNewMagicCircle(caster, castInfo, usedSpellName, clientLevel);
            } else {
                updateMagicCircleEntityPosition(currentActiveCircle.ID, caster);
            }
        } else {
            if (currentActiveCircle != null)
                MagicCircleManager.startMagicCircleTermination(caster, currentActiveCircle);
        }
    }

    private static void updateMagicCircleEntityPosition(UUID magicCircleEntityId,
                                                        LivingEntity caster) {
        Entity entity = CIRCLES_ENTITIES_BY_ID.get(magicCircleEntityId);

        if (entity == null) {
            ModLogger.error("updateMagicCircleEntityPosition: couldn't find entity");
            return;
        }

        Vec3 pos = caster.getPosition(1f);
        entity.setPosRaw(pos.x, pos.y, pos.z);
    }

    private static void updateMagicCirclesPerTick() {
        CIRCLES_DATA_BY_ID.values().forEach(MagicCircleData::updatePerTick);

        FADE_OUT_CIRCLES_IDS.removeIf(uuid -> {
            MagicCircleData magicCircleData = CIRCLES_DATA_BY_ID.get(uuid);

            boolean isConcealed = magicCircleData.isConcealed();

            if (isConcealed) {
                removeCircleEntity(magicCircleData.ID);
                CIRCLES_DATA_BY_ID.remove(magicCircleData.ID);
            }

            return isConcealed;
        });
    }

    private static void startMagicCircleTermination(LivingEntity caster, MagicCircleData magicCircleData) {
        CIRCLES_IDS_BY_CASTER.remove(caster);
        magicCircleData.startInitElseTermination(false);
        FADE_OUT_CIRCLES_IDS.add(magicCircleData.ID);
    }

    private static void createNewMagicCircle(LivingEntity caster,
                                             CastInfo castInfo,
                                             String usedSpellName,
                                             ClientLevel clientLevel) {
        MagicCircleEntity circleEntity = new MagicCircleEntity(ModEntities.CACHED_MAGIC_CIRCLE, clientLevel);
        circleEntity.setCaster(caster);
        UUID circleEntityUUID = circleEntity.getUUID();

        CIRCLES_ENTITIES_BY_ID.put(circleEntityUUID, circleEntity);

        updateMagicCircleEntityPosition(circleEntityUUID, caster);

        MagicCircleData newCircle = MagicCircleFactory.buildMagicCircleData(
                usedSpellName,
                caster,
                castInfo,
                circleEntityUUID
        );

        newCircle.startInitElseTermination(true);
        CIRCLES_IDS_BY_CASTER.put(caster, circleEntityUUID);
        CIRCLES_DATA_BY_ID.put(circleEntityUUID, newCircle);

        clientLevel.addEntity(circleEntity);
    }

    private static void removeCircleEntity(UUID entityId) {
        Entity entity = CIRCLES_ENTITIES_BY_ID.get(entityId);

        if (entity != null)
            entity.discard();
    }

    private static MagicCircleData getMagicCircleDataFromCaster(LivingEntity caster) {
        MagicCircleData result = null;

        UUID id = CIRCLES_IDS_BY_CASTER.get(caster);

        if (id != null)
            result = CIRCLES_DATA_BY_ID.get(id);

        return result;
    }
}
