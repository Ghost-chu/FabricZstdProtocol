package com.ghostchu.mods.fabriczstdprotocol.mixin;

import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
 //   @Shadow private Channel channel;

//    @Inject(method = "setCompressionThreshold", at = @At("HEAD"))
//    public void setCompressionThreshold(int compressionThreshold, boolean rejectsBadPackets, CallbackInfo ci) {
//        if (channel.pipeline().get("decompress") instanceof PacketInflater) {
//            channel.pipeline().remove("decompress");
//        }
//        if (channel.pipeline().get("compress") instanceof PacketDeflater) {
//            channel.pipeline().remove("compress");
//        }
//        channel.pipeline().addBefore("decoder", "decompress", new ZstdPacketInflater(compressionThreshold, true));
//        channel.pipeline().addBefore("encoder", "compress", new ZstdPacketDeflater(compressionThreshold,level));
//    }
}
