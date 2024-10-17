package de.tomalbrc.microfighters;

import de.tomalbrc.microfighters.registry.ItemRegistry;
import de.tomalbrc.microfighters.registry.MobRegistry;
import net.fabricmc.api.ModInitializer;

public class MicroFighters implements ModInitializer {
    public static final float SCALE = 0.3f;
    public static final String MOD_ID = "microfighters";

    @Override
    public void onInitialize() {
        ItemRegistry.register();
        MobRegistry.register();
    }
}
