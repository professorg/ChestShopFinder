package dev.kosdt.chestshopfinder;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ChestShopFinder {
    public static final String MOD_ID = "chestshopfinder";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final KeyBinding OPEN_CHEST_SHOP_FINDER = new KeyBinding(
            "key." + MOD_ID + ".open_chest_shop_finder",
            InputUtil.Type.KEYSYM,
            InputUtil.GLFW_KEY_BACKSLASH,
            "category." + MOD_ID + ".keybind"
    );

    public static void init() {
        // Write common init code here.
        KeyMappingRegistry.register(OPEN_CHEST_SHOP_FINDER);

        ClientTickEvent.CLIENT_POST.register(client -> {
            while (OPEN_CHEST_SHOP_FINDER.wasPressed()) {
                client.setScreenAndRender(new ChestShopFinderGui());
            }
        });
    }
}
