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
    @Shadow @Final private ClientConnection connection;
    private static final Logger LOGGER = LoggerFactory.getLogger("ClientLoginNetworkHandlerMixin");

    @Inject(method = "onCompression", at = @At("TAIL"))
    public void onCompression(LoginCompressionS2CPacket packet, CallbackInfo ci){
        if (!connection.isLocal()) {
            LOGGER.info("Printing the original channel handlers...");
            FabricZSTDProtocolClient.INSTANCE.printChannelHandlers(connection);
            if(packet.isZstd()){
                LOGGER.info("Changing connection {} to Zstd protocol..." ,connection);
                FabricZSTDProtocolClient.INSTANCE.changeEncoderDecoderToZstdVersion(packet.getCompressionThreshold(),6, connection);
                LOGGER.info("Changed connection {} to Zstd protocol..." ,connection);
            }else{
                LOGGER.info("Changing connection {} to Gzip protocol..." ,connection);
                FabricZSTDProtocolClient.INSTANCE.changeEncoderDecoderToGzipVersion(packet.getCompressionThreshold(),connection);
                LOGGER.info("Changed connection {} to Gzip protocol..." ,connection);
            }
            LOGGER.info("Printing the modified channel handlers...");
            FabricZSTDProtocolClient.INSTANCE.printChannelHandlers(this.connection);
        }
    }
}
