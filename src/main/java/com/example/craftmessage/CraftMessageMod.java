package com.example.craftmessage;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CraftMessageMod implements ModInitializer {

    public static final String MOD_ID = "craftmessage";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("CraftMessage mod initialized!");

        // Initialize database connection
        DatabaseManager.init();
    }
}
