package com.example.craftmessage;

import java.nio.charset.StandardCharsets;

public class Message {

    // Simple message wrapper class without external dependencies

    public static class MessageProto {

        private final String text;

        private MessageProto(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public byte[] toByteArray() {
            // Simple UTF-8 encoding
            return text.getBytes(StandardCharsets.UTF_8);
        }

        public static MessageProto parseFrom(byte[] data) {
            // Simple UTF-8 decoding
            String text = new String(data, StandardCharsets.UTF_8);
            return new MessageProto(text);
        }

        public static class Builder {

            private String text = "";

            public Builder setText(String text) {
                this.text = text;
                return this;
            }

            public MessageProto build() {
                return new MessageProto(text);
            }
        }
    }
}
