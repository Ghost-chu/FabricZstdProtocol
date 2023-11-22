package com.ghostchu.mods.fabriczstdprotocol.mixin;

import com.ghostchu.mods.fabriczstdprotocol.client.FabricZSTDProtocolClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket.class)
public class LoginCompressionC2SPacketMixin {
    @Shadow @Final private int compressionThreshold;
    private boolean zstd = false;
    private int level = Integer.MIN_VALUE;
    private static final Logger LOGGER = LoggerFactory.getLogger("LoginCompressionS2CPacketMixin");
    @Inject(method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V", at = @At("TAIL"))
    public void init(PacketByteBuf buf, CallbackInfo ci){
        this.zstd = readVarIntSafely(buf) == 1;
        this.level = readVarIntSafely(buf);
        if(this.zstd){
            FabricZSTDProtocolClient.INSTANCE.changeEncoderDecoderToZstdVersion(compressionThreshold, level);
            LOGGER.info("Server response ACK! Switched to ZSTD protocol!");
        }
    }

    @Inject(method = "write", at = @At("TAIL"))
    public void write(PacketByteBuf buf, CallbackInfo ci){
        buf.writeVarInt(this.zstd ? 1 : 0);
        buf.writeVarInt(this.level);
        LOGGER.info("Request server to use Zstd protocol");
    }



    private static int readVarIntSafely(ByteBuf buf) {
        int i = 0;
        int maxRead = Math.min(5, buf.readableBytes());
        for (int j = 0; j < maxRead; j++) {
            int k = buf.readByte();
            i |= (k & 0x7F) << j * 7;
            if ((k & 0x80) != 128) {
                return i;
            }
        }
        return Integer.MIN_VALUE;
    }
}
