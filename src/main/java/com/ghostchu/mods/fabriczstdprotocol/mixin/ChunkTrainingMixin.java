package com.ghostchu.mods.fabriczstdprotocol.mixin;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.LightData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

@Mixin(ChunkDataS2CPacket.class)
public class ChunkTrainingMixin {
    @Shadow @Final private int chunkX;
    @Shadow @Final private int chunkZ;
    @Shadow @Final private ChunkData chunkData;
    @Shadow @Final private LightData lightData;
    @Unique
    private static final File SAMPLES_ROOT = new File("./samples");
    @Unique
    private static final Logger LOGGER = LoggerFactory.getLogger("ChunkTrainingMixin");
    @Inject(method = "apply(Lnet/minecraft/network/listener/ClientPlayPacketListener;)V", at = @At("HEAD"))
    private void applying(ClientPlayPacketListener clientPlayPacketListener, CallbackInfo ci){
        ByteBuf buffer = Unpooled.buffer();
        PacketByteBuf packetByteBuf = new PacketByteBuf(buffer);
        packetByteBuf.writeInt(this.chunkX);
        packetByteBuf.writeInt(this.chunkZ);
        this.chunkData.write(packetByteBuf);
        this.lightData.write(packetByteBuf);
        try {
            File file = new File(SAMPLES_ROOT, "c." + chunkX + "." + chunkZ + ".bin");
            if (file.exists()) {
                file.delete();
            }
            if(!file.getParentFile().exists()){
                file.mkdirs();
            }
            try {
                file.createNewFile();
                byte[] dat = new byte[packetByteBuf.readableBytes()];
                packetByteBuf.readBytes(dat);
                CompletableFuture.supplyAsync(() -> {
                    try {
                        Files.write(file.toPath(), dat);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return file;
                }).thenAccept(file2 -> LOGGER.info("Saved chunk packet binary at {}", file2.getAbsolutePath()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }finally {
            packetByteBuf.release();
        }
    }
}
