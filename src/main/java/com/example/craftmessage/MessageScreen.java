package com.example.craftmessage;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class MessageScreen extends Screen {

    private TextFieldWidget messageField;
    private ButtonWidget sendButton;
    private boolean databaseAvailable;

    public MessageScreen() {
        super(Text.literal("Send Message"));
        // Check if database is available using Hibernate with error handling
        try {
            this.databaseAvailable = DatabaseManager.isDatabaseAvailable();
        } catch (Exception e) {
            // Log error but don't crash - allow screen to open without database
            this.databaseAvailable = false;
        }
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Message input field
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
        this.addDrawableChild(this.messageField);

        // Send button
        this.sendButton = ButtonWidget.builder(
            Text.literal("Send Message"),
            button -> this.sendMessage()
        )
            .dimensions(centerX - 75, centerY + 20, 150, 20)
            .build();
        this.addDrawableChild(this.sendButton);

        // Set initial focus
        this.setInitialFocus(this.messageField);
    }

    private void sendMessage() {
        String messageText = this.messageField.getText().trim();

        if (messageText.isEmpty()) {
            return;
        }

        // Create payload with only the message text
        SimpleMessagePayload payload = new SimpleMessagePayload(messageText);

        // Send to server using simple payload
        ClientPlayNetworking.send(payload);

        // Close screen
        this.close();
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(null);
        }
    }

    @Override
    public void render(
        net.minecraft.client.gui.DrawContext context,
        int mouseX,
        int mouseY,
        float delta
    ) {
        // Fill background with dark color
        this.fillBackground(context);

        // Draw title
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            this.title,
            this.width / 2,
            40,
            0xFFFFFF
        );

        // Draw label for input field
        context.drawTextWithShadow(
            this.textRenderer,
            Text.literal("Message:"),
            this.width / 2 - 150,
            this.height / 2 - 45,
            0xFFFFFF
        );

        // Show PostgreSQL status message if not available
        if (!this.databaseAvailable) {
            context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("PostgreSQL for messages not ready"),
                this.width / 2,
                this.height / 2 + 50,
                0xFF5555
            );
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void fillBackground(net.minecraft.client.gui.DrawContext context) {
        // Fill with semi-transparent dark background
        context.fill(0, 0, this.width, this.height, 0x80000000);
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
}
