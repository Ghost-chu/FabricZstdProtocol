package com.ghostchu.mods.fabriczstdprotocol.client;

import com.ghostchu.mods.fabriczstdprotocol.mixin.ClientConnectionAccessor;
import com.ghostchu.mods.fabriczstdprotocol.pkthandler.ZstdPacketDeflater;
import com.ghostchu.mods.fabriczstdprotocol.pkthandler.ZstdPacketInflater;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.channel.Channel;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.handler.PacketDeflater;
import net.minecraft.network.handler.PacketInflater;
import net.minecraft.util.Identifier;

public class FabricZSTDProtocolClient implements ClientModInitializer {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    private static final Identifier PKT_ID = new Identifier("fabriczstdprotocol", "handshake");
    public static FabricZSTDProtocolClient INSTANCE;
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        INSTANCE = this;
    }


    public void changeEncoderDecoderToZstdVersion(int compressionThreshold, int level) {
        ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
        if (handler == null) return;
        ClientConnection clientConnection = handler.getConnection();
        if (clientConnection == null) return;
        ClientConnectionAccessor accessor = ((ClientConnectionAccessor) clientConnection);
        Channel channel = accessor.getChannel();
        if (channel.pipeline().get("decompress") instanceof PacketInflater) {
            channel.pipeline().remove("decompress");
        }
        if (channel.pipeline().get("compress") instanceof PacketDeflater) {
            channel.pipeline().remove("compress");
        }
        channel.pipeline().addBefore("decoder", "decompress", new ZstdPacketInflater(compressionThreshold, true));
        channel.pipeline().addBefore("encoder", "compress", new ZstdPacketDeflater(compressionThreshold,level));
    }

    public void changeEncoderDecoderToGzipVersion(int compressionThreshold) {
        ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
        if (handler == null) return;
        ClientConnection clientConnection = handler.getConnection();
        if (clientConnection == null) return;
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
