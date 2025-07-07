package dev.kosdt.chestshopfinder.fabric;

import net.fabricmc.api.ModInitializer;

import dev.kosdt.chestshopfinder.ChestShopFinder;

public final class ChestShopFinderFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        ChestShopFinder.init();
    }
}
