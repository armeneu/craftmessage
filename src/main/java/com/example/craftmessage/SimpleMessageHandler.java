package com.example.craftmessage;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleMessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        SimpleMessageHandler.class
    );

    public static void register() {
        try {
            // Only register on server side - this should avoid client initialization issues
            PayloadTypeRegistry.playC2S().register(
                SimpleMessagePayload.ID,
                SimpleMessagePayload.CODEC
            );

            // Register the server-side handler
            ServerPlayNetworking.registerGlobalReceiver(
                SimpleMessagePayload.ID,
                (payload, context) -> {
                    try {
                        String messageText = payload.text();
                        String playerUuid = context.player().getUuidAsString();

                        LOGGER.info(
                            "Received message from player {}: {}",
                            playerUuid,
                            messageText
                        );

                        // Check if database is available using Hibernate 6.x
                        boolean dbAvailable =
                            DatabaseManager.isDatabaseAvailable();

                        if (dbAvailable) {
                            try {
                                boolean success = DatabaseManager.saveMessage(
                                    playerUuid,
                                    messageText
                                );
                                if (!success) {
                                    LOGGER.error(
                                        "Failed to save message for player {}",
                                        playerUuid
                                    );
                                }
                            } catch (Exception e) {
                                LOGGER.error(
                                    "Failed to save message for player {}",
                                    playerUuid,
                                    e
                                );
                            }
                        } else {
                            LOGGER.warn(
                                "Database not available - message not saved"
                            );
                        }
                    } catch (Exception e) {
                        LOGGER.error("Failed to process message", e);
                    }
                }
            );

            LOGGER.info("Simple message handler registered successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to register simple message handler", e);
        }
    }
}
