package us.kosdt.chestshopfinder;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

@EventBusSubscriber
public class KeyInputEventHandler {

    private static Map<Actions, KeyBinding> keyBindings;

    private static enum Actions {
        OPENGUI
    }

    public static void registerKeyBindings() {
        keyBindings = new HashMap<Actions, KeyBinding>();
        keyBindings.put(Actions.OPENGUI, new KeyBinding("key.opengui", Keyboard.KEY_P, "key.chestshopfindergui"));

        keyBindings.values().forEach(keyBinding -> ClientRegistry.registerKeyBinding(keyBinding));
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled = true)
    public static void onEvent(KeyInputEvent event) {
        
        keyBindings.keySet().forEach(actionType -> {
            KeyBinding keyBinding = keyBindings.get(actionType);
            if (keyBinding == null) {
                return;
            }
            if (!keyBinding.isPressed()) return;
            switch(actionType) {
                case OPENGUI:
                    Minecraft.getMinecraft().displayGuiScreen(new ChestShopFinderGui());
                    break;
            }
        });

    }



}