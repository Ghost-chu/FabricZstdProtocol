package com.ghostchu.mods.fabriczstdprotocol.client;

import com.ghostchu.mods.fabriczstdprotocol.mixin.ClientConnectionAccessor;
import com.ghostchu.mods.fabriczstdprotocol.pkthandler.ZstdPacketDeflater;
import com.ghostchu.mods.fabriczstdprotocol.pkthandler.ZstdPacketInflater;
import io.netty.channel.Channel;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.handler.PacketDeflater;
import net.minecraft.network.handler.PacketInflater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FabricZSTDProtocolClient implements ClientModInitializer {
    public static FabricZSTDProtocolClient INSTANCE;
    private static final Logger LOGGER = LoggerFactory.getLogger("FabricZSTDProtocolClient");

    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        INSTANCE = this;
    }


    public void changeEncoderDecoderToZstdVersion(int compressionThreshold, int level, byte[] dict, ClientConnection clientConnection) {
        LOGGER.debug("Selected connection: " + clientConnection);
        ClientConnectionAccessor accessor = (ClientConnectionAccessor) clientConnection;
        Channel channel = accessor.getChannel();
        if (channel.pipeline().get("decompress") instanceof PacketInflater) {
            channel.pipeline().remove("decompress");
        }
        if (channel.pipeline().get("compress") instanceof PacketDeflater) {
            channel.pipeline().remove("compress");
        }
        channel.pipeline().addBefore("decoder", "decompress", new ZstdPacketInflater(compressionThreshold, false));
        channel.pipeline().addBefore("encoder", "compress", new ZstdPacketDeflater(compressionThreshold, level, dict));
    }

    public void printChannelHandlers(ClientConnection connection) {
        if (connection == null) {
            return;
        }
        ClientConnectionAccessor accessor = ((ClientConnectionAccessor) connection);
        Channel channel = accessor.getChannel();
        channel.pipeline().toMap().forEach((k, v) -> LOGGER.debug("Pipeline Handler: [" + k + "] -> " + v.getClass().getName()));
    }

    public void changeEncoderDecoderToGzipVersion(int compressionThreshold, ClientConnection clientConnection) {
        LOGGER.debug("Selected connection: " + clientConnection);
        ClientConnectionAccessor accessor = ((ClientConnectionAccessor) clientConnection);
        Channel channel = accessor.getChannel();
        if (channel.pipeline().get("decompress") instanceof PacketInflater) {
            channel.pipeline().remove("decompress");
        }
        if (channel.pipeline().get("compress") instanceof PacketDeflater) {
            channel.pipeline().remove("compress");
        }
        channel.pipeline().addBefore("decoder", "decompress", new PacketInflater(compressionThreshold, true));
        channel.pipeline().addBefore("encoder", "compress", new PacketDeflater(compressionThreshold));
    }

}
