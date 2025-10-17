package com.example.craftmessage;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CraftMessageClient implements ClientModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CraftMessageClient.class
    );
    private static KeyBinding openMessageScreenKey;

    @Override
    public void onInitializeClient() {
        LOGGER.info("CraftMessage client initializing...");

        try {
            // Register key binding
            openMessageScreenKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                    "key.craftmessage.open_message_screen",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_M,
                    "category.craftmessage.general"
                )
            );

            // Register tick event to check for key press
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (openMessageScreenKey.wasPressed()) {
                    if (client.player != null) {
                        client.setScreen(new MessageScreen());
                    }
                }
            });

            LOGGER.info("CraftMessage client initialized successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize CraftMessage client", e);
        }
    }
}
