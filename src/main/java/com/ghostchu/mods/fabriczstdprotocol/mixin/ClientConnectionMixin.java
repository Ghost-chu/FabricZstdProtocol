package com.ghostchu.mods.fabriczstdprotocol.mixin;

import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.handler.PacketDeflater;
import net.minecraft.network.handler.PacketInflater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Shadow private Channel channel;

    public Channel getChannel() {
        ClientConnection instance = ((ClientConnection) ((Object) this));
        ClientConnectionAccessor channelGetter = ((ClientConnectionAccessor) instance);
        return channelGetter.getChannel();
    }
    @Inject(method = "setCompressionThreshold", at = @At("HEAD"))
    public void setCompressionThreshold(int compressionThreshold, boolean rejectsBadPackets, CallbackInfo ci) {
        if (channel.pipeline().get("decompress") instanceof PacketInflater) {
            channel.pipeline().remove("decompress");
        }
        if (channel.pipeline().get("compress") instanceof PacketDeflater) {
            channel.pipeline().remove("compress");
        }
    }
}
