package com.ghostchu.mods.fabriczstdprotocol.mixin;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.handshake.ConnectionIntent;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandshakeC2SPacket.class)
public class HandshakeC2SPacketMixin {
    @Shadow
    @Final
    private int protocolVersion;
    @Shadow
    @Final
    private String address;
    @Shadow
    @Final
    private int port;
    @Shadow
    @Final
    private ConnectionIntent intendedState;
    private static final Logger LOGGER = LoggerFactory.getLogger("HandshakeC2SPacketMixin");

    @Inject(method = "write", at = @At("HEAD"), cancellable = true)
    public void write(PacketByteBuf buf, CallbackInfo ci) {
        buf.writeVarInt(protocolVersion);
        buf.writeString(address + "\0zstd-protocol");
        buf.writeShort(port);
        buf.writeVarInt(intendedState.getId());
        LOGGER.info("[Handshake Trick] Appended zstd-protocol-request at the host string tail.");
        ci.cancel();
    }
}
