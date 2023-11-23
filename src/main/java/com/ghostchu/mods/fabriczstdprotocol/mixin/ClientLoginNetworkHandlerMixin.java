package com.ghostchu.mods.fabriczstdprotocol.mixin;

import com.ghostchu.mods.fabriczstdprotocol.client.FabricZSTDProtocolClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLoginNetworkHandler.class)
public class ClientLoginNetworkHandlerMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("ClientLoginNetworkHandlerMixin");
    @Shadow
    @Final
    private ClientConnection connection;

    @Inject(method = "onCompression", at = @At("TAIL"))
    public void onCompression(LoginCompressionS2CPacket packet, CallbackInfo ci) {
        if (!connection.isLocal()) {
            FabricZSTDProtocolClient.INSTANCE.printChannelHandlers(connection);
            if (packet.isZstd()) {
                FabricZSTDProtocolClient.INSTANCE.changeEncoderDecoderToZstdVersion(packet.getCompressionThreshold(), packet.getCompressionLevel(), connection);
                LOGGER.info("Changed connection {} to Zstd protocol...", connection);
            } else {
                FabricZSTDProtocolClient.INSTANCE.changeEncoderDecoderToGzipVersion(packet.getCompressionThreshold(), connection);
                LOGGER.info("Changed connection {} to Gzip protocol...", connection);
            }
            FabricZSTDProtocolClient.INSTANCE.printChannelHandlers(this.connection);
        }
    }
}
