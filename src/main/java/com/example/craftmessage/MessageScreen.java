package com.example.craftmessage;

import java.util.UUID;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class MessageScreen extends Screen {

    private TextFieldWidget messageField;
    private ButtonWidget sendButton;
    private ButtonWidget cancelButton;

    public MessageScreen() {
        super(Text.literal("Send Message"));
    }

    @Override
    protected void init() {
        super.init();

        // Calculate positions
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Create message input field
        this.messageField = new TextFieldWidget(
            this.textRenderer,
            centerX - 150,
            centerY - 30,
            300,
            20,
            Text.literal("Enter your message")
        );
        this.messageField.setMaxLength(256);
        this.messageField.setPlaceholder(
            Text.literal("Type your message here...")
        );
        this.addSelectableChild(this.messageField);
        this.setInitialFocus(this.messageField);

        // Create send button
        this.sendButton = ButtonWidget.builder(
            Text.literal("Send Message"),
            button -> sendMessage()
        )
            .dimensions(centerX - 152, centerY + 10, 150, 20)
            .build();
        this.addDrawableChild(this.sendButton);

        // Create cancel button
        this.cancelButton = ButtonWidget.builder(
            Text.literal("Cancel"),
            button -> this.close()
        )
            .dimensions(centerX + 2, centerY + 10, 150, 20)
            .build();
        this.addDrawableChild(this.cancelButton);
    }

    @Override
    public void render(
        net.minecraft.client.gui.DrawContext context,
        int mouseX,
        int mouseY,
        float delta
    ) {
        // Fill the screen with a semi-transparent dark background
        context.fill(0, 0, this.width, this.height, 0x80000000);

        // Draw title
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            this.title,
            this.width / 2,
            this.height / 2 - 60,
            0xFFFFFF
        );

        // Draw message field label
        context.drawTextWithShadow(
            this.textRenderer,
            Text.literal("Message:"),
            this.width / 2 - 150,
            this.height / 2 - 45,
            0xFFFFFF
        );

        // Render the message field
        this.messageField.render(context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            // ESC key
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void sendMessage() {
        String messageText = this.messageField.getText().trim();

        if (messageText.isEmpty()) {
            if (this.client != null && this.client.player != null) {
                this.client.player.sendMessage(
                    Text.literal("Message cannot be empty!"),
                    false
                );
            }
            return;
        }

        // Check database connection before attempting to send
        if (!DatabaseManager.isDatabaseAvailable()) {
            if (this.client != null && this.client.player != null) {
                this.client.player.sendMessage(
                    Text.literal(
                        "Database connection unavailable. Message not sent."
                    ),
                    false
                );
            }
            this.close();
            return;
        }

        try {
            // Create message
            Message.MessageProto messageProto =
                Message.MessageProto.newBuilder().setText(messageText).build();

            // Get player UUID
            UUID playerUuid = this.client.player.getUuid();

            // Save to database
            boolean success = DatabaseManager.saveMessage(
                playerUuid,
                messageText
            );

            // Show result message to player
            if (success) {
                if (this.client != null && this.client.player != null) {
                    this.client.player.sendMessage(
                        Text.literal("Message sent"),
                        false
                    );
                }
            } else {
                if (this.client != null && this.client.player != null) {
                    this.client.player.sendMessage(
                        Text.literal("Message not sent"),
                        false
                    );
                }
            }

            // Close the screen
            this.close();
        } catch (Exception e) {
            CraftMessageMod.LOGGER.error(
                "Failed to send message: " + e.getMessage()
            );
            if (this.client != null && this.client.player != null) {
                this.client.player.sendMessage(
                    Text.literal("Error sending message"),
                    false
                );
            }
        }
    }
}
