package net.xavier.neomagiccircles.events;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.xavier.neomagiccircles.entity.MagicCircleEntityRenderer;
import net.xavier.neomagiccircles.registry.ModEntities;

public class ModEvents {
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        ModEntities.CACHED_MAGIC_CIRCLE = ModEntities.MAGIC_CIRCLE.get();

        event.registerEntityRenderer(
                ModEntities.CACHED_MAGIC_CIRCLE,
                MagicCircleEntityRenderer::new
        );
    }
}
