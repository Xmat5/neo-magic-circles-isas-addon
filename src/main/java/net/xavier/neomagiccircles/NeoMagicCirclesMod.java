package net.xavier.neomagiccircles;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.xavier.neomagiccircles.config.NeoMagicCirclesConfig;

@Mod(NeoMagicCirclesMod.MODID)
public class NeoMagicCirclesMod {
    public static final String MODID = "neomagiccircles";

    public NeoMagicCirclesMod(IEventBus modBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, NeoMagicCirclesConfig.SPEC, NeoMagicCirclesConfig.FILE_NAME);

        MagicCirclesIntegration.init(modBus, modContainer);
    }
}
