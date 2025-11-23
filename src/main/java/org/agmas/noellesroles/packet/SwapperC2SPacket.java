package org.agmas.noellesroles.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;

import java.util.UUID;

public record SwapperC2SPacket(UUID player, UUID player2) implements CustomPayload {
    public static final Identifier MORPH_PAYLOAD_ID = Identifier.of(Noellesroles.MOD_ID, "swapper");
    public static final Id<SwapperC2SPacket> ID = new Id<>(MORPH_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, SwapperC2SPacket> CODEC;

    public SwapperC2SPacket(UUID player, UUID player2) {
        this.player = player;
        this.player2 = player2;
    }

    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.player);
        buf.writeUuid(this.player2);
    }

    public static SwapperC2SPacket read(PacketByteBuf buf) {
        return new SwapperC2SPacket(buf.readUuid(), buf.readUuid());
    }


    public UUID player() {
        return this.player;
    }
    public UUID player2() {
        return this.player2;
    }


    static {
        CODEC = PacketCodec.of(SwapperC2SPacket::write, SwapperC2SPacket::read);
    }
}