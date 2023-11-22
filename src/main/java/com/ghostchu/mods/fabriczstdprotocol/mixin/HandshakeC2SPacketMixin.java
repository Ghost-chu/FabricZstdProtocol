package com.ghostchu.mods.fabriczstdprotocol.mixin;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandshakeC2SPacket.class)
public class HandshakeC2SPacketMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("HandshakeC2SPacketMixin");
    @Inject(method = "write", at = @At("TAIL"), cancellable = true)
    public void write(PacketByteBuf buf, CallbackInfo ci){
        buf.writeVarInt(0x7355608);
        LOGGER.info("Appended zstd-protocol-request at the packet end.");
        ci.cancel();
    }
}
