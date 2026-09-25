package net.xavier.neomagiccircles;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.xavier.neomagiccircles.events.ClientEvents;
import net.xavier.neomagiccircles.events.ModEvents;
import net.xavier.neomagiccircles.registry.ModEntities;

public final class MagicCirclesIntegration {
    private MagicCirclesIntegration() {}

    public static void init(IEventBus modBus, ModContainer modContainer) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ModEntities.ENTITY_TYPES.register(modBus);
            modBus.addListener(ModEvents::onRegisterRenderers);

            NeoForge.EVENT_BUS.addListener(ClientEvents::onEntityLeaveLevel);
            NeoForge.EVENT_BUS.addListener(ClientEvents::onClientLogout);
            NeoForge.EVENT_BUS.addListener(ClientEvents::onClientTick);
            NeoForge.EVENT_BUS.addListener(ClientEvents::onEntityLeavingLevel);
        }
    }
}
