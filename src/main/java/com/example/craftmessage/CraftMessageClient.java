package com.example.craftmessage;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class CraftMessageClient implements ClientModInitializer {
    private static KeyBinding openMessageScreenKey;

    @Override
    public void onInitializeClient() {
        // Register key binding
        openMessageScreenKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.craftmessage.open_message_screen",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            "category.craftmessage.general"
        ));

        // Register tick event to check for key press
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMessageScreenKey.wasPressed()) {
                if (client.player != null) {
                    client.setScreen(new MessageScreen());
                }
            }
        });
    }
}
