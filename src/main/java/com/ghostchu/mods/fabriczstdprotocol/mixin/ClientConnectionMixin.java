package com.ghostchu.mods.fabriczstdprotocol.mixin;

import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Shadow private Channel channel;

    public Channel getChannel() {
        ClientConnection instance = ((ClientConnection) ((Object) this));
        ClientConnectionAccessor channelGetter = ((ClientConnectionAccessor) instance);
        return channelGetter.getChannel();
    }
}
