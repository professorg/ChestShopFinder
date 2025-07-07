package dev.kosdt.chestshopfinder.neoforge;

import net.neoforged.fml.common.Mod;

import dev.kosdt.chestshopfinder.ChestShopFinder;

@Mod(ChestShopFinder.MOD_ID)
public final class ChestShopFinderNeoForge {
    public ChestShopFinderNeoForge() {
        // Run our common setup.
        ChestShopFinder.init();
    }
}
