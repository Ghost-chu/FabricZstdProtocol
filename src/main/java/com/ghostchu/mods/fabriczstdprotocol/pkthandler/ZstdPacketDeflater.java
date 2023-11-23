package com.ghostchu.mods.fabriczstdprotocol.pkthandler;

import com.github.luben.zstd.ZstdCompressCtx;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.encoding.VarInts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZstdPacketDeflater extends MessageToByteEncoder<ByteBuf> {
    private static final Logger LOGGER = LoggerFactory.getLogger("ZstdPacketDeflater");
    private final ZstdCompressCtx compressCtx;
    private int compressionThreshold;

    public ZstdPacketDeflater(int compressionThreshold, int level, byte[] dict) {
        this.compressionThreshold = compressionThreshold;
        this.compressCtx = new ZstdCompressCtx();
        this.compressCtx.setLevel(level);
        if (dict != null && dict.length > 0) {
            LOGGER.info("Server sent the Zstd dict, installing it!");
            this.compressCtx.loadDict(dict);
        } else {
            LOGGER.info("Didn't receive the dict from server, fallback to zstd default.");
            this.compressCtx.loadDict((byte[]) null);
        }
    }

    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) {
        int i = byteBuf.readableBytes();
        if (i < this.compressionThreshold) {
            VarInts.write(byteBuf2, 0);
            byteBuf2.writeBytes(byteBuf);
        } else {
            byte[] bs = new byte[i];
            byteBuf.readBytes(bs);
            VarInts.write(byteBuf2, bs.length);
            byte[] targetData = new byte[i];
            System.arraycopy(bs, 0, targetData, 0, i);
            byteBuf2.writeBytes(compressCtx.compress(targetData));
        }
    }

    public int getCompressionThreshold() {
        return this.compressionThreshold;
    }

    public void setCompressionThreshold(int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
    }
}
