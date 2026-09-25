package net.xavier.neomagiccircles.oculus;

import net.irisshaders.iris.api.v0.IrisApi;
import net.neoforged.fml.ModList;
import net.xavier.neomagiccircles.render.MagicCirclesRender;

public class OculusCompact {
    private static final boolean OCULUS_LOADED = ModList.get().isLoaded("iris");
    private static boolean lastShaderState = false;

    public static boolean isOculusLoaded() {
        return OCULUS_LOADED;
    }

    public static boolean isShaderPackInUse() {
        return isOculusLoaded() && IrisApi.getInstance().isShaderPackInUse();
    }

    public static void handleOnRenderUpdate() {
        if (!isOculusLoaded()) return;

        boolean currentState = isShaderPackInUse();

        if (currentState != lastShaderState) {
            onShaderToggle(currentState);
            lastShaderState = currentState;
        }
    }

    private static void onShaderToggle(boolean enabled) {
        MagicCirclesRender.clearCache();
    }
}
