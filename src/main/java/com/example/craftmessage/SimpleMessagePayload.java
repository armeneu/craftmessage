package com.example.craftmessage;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SimpleMessagePayload(String text) implements CustomPayload {
    public static final CustomPayload.Id<SimpleMessagePayload> ID =
        new CustomPayload.Id<>(Identifier.of("craftmessage", "simple_message"));

    public static final PacketCodec<PacketByteBuf, SimpleMessagePayload> CODEC =
        PacketCodec.of(SimpleMessagePayload::write, SimpleMessagePayload::read);

    private static void write(SimpleMessagePayload payload, PacketByteBuf buf) {
        buf.writeString(payload.text);
    }

    private static SimpleMessagePayload read(PacketByteBuf buf) {
        String text = buf.readString();
        return new SimpleMessagePayload(text);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
