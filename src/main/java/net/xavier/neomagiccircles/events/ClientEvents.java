package net.xavier.neomagiccircles.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.xavier.neomagiccircles.MagicCircleManager;
import net.xavier.neomagiccircles.config.ConfigCache;
import net.xavier.neomagiccircles.oculus.OculusCompact;
import net.xavier.neomagiccircles.render.MagicCirclesRender;

public class ClientEvents {
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide && event.getEntity() instanceof Player player) {
            MagicCircleManager.handlePlayerLeaving(player);
        }
    }

    public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        ConfigCache.invalidateCache();
    }

    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        MagicCircleManager.handleClientLeaving();
        MagicCirclesRender.clearCache();
        ConfigCache.invalidateCache();
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        MagicCircleManager.handleOnClientTick();
        OculusCompact.handleOnRenderUpdate();
    }

    public static void onEntityLeavingLevel(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof LivingEntity livingEntity)
            MagicCircleManager.handleEntityLeavingLevel(livingEntity);
    }
}
