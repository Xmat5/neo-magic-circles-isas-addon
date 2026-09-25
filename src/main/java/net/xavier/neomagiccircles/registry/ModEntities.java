package net.xavier.neomagiccircles.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.xavier.neomagiccircles.NeoMagicCirclesMod;
import net.xavier.neomagiccircles.entity.MagicCircleEntity;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, NeoMagicCirclesMod.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<MagicCircleEntity>> MAGIC_CIRCLE =
            ENTITY_TYPES.register("magic_circle",
                    () -> EntityType.Builder.<MagicCircleEntity>of(MagicCircleEntity::new, MobCategory.MISC)
                            .sized(0.0f, 0.0f)
                            .clientTrackingRange(64)
                            .updateInterval(Integer.MAX_VALUE)
                            .build("magic_circle")
            );

    public static EntityType<MagicCircleEntity> CACHED_MAGIC_CIRCLE;
}
