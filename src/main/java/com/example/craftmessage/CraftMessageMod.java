package com.example.craftmessage;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CraftMessageMod implements ModInitializer {

    public static final String MOD_ID = "craftmessage";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("CraftMessage mod initializing...");

        // Hibernate will be initialized lazily when needed

        // Register simple message handler
        try {
            SimpleMessageHandler.register();
            LOGGER.info("CraftMessage mod initialized successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize CraftMessage mod", e);
        }
    }
}
