package net.xavier.neomagiccircles;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(NeoMagicCirclesMod.MODID)
public class NeoMagicCirclesMod {
    public static final String MODID = "neomagiccircles";

    public NeoMagicCirclesMod(IEventBus modBus, ModContainer modContainer) {
        MagicCirclesIntegration.init(modBus, modContainer);
    }
}
