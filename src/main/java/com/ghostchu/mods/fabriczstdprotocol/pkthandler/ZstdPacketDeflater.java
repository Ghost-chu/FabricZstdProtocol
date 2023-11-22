package com.ghostchu.mods.fabriczstdprotocol.pkthandler;

import com.github.luben.zstd.ZstdCompressCtx;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.encoding.VarInts;

public class ZstdPacketDeflater extends MessageToByteEncoder<ByteBuf> {
    private final int level;
    private final ZstdCompressCtx compressCtx;
    private int compressionThreshold;

    public ZstdPacketDeflater(int compressionThreshold, int level) {
        this.compressionThreshold = compressionThreshold;
        this.level = level;
        this.compressCtx = new ZstdCompressCtx();

    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) throws Exception {
        int i = byteBuf.readableBytes();
        if (i < this.compressionThreshold) {
            VarInts.write(byteBuf2, 0);
            byteBuf2.writeBytes(byteBuf);
        } else {
            byte[] bs = new byte[i];
            byteBuf.readBytes(bs);
            VarInts.write(byteBuf2, bs.length);
            try {
                this.compressCtx.setLevel(level);
                byteBuf2.writeBytes(compressCtx.compress(bs));
            } finally {
                compressCtx.reset();
            }
        }

    }

    public int getCompressionThreshold() {
        return compressionThreshold;
    }

    public void setCompressionThreshold(int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
    }
}
