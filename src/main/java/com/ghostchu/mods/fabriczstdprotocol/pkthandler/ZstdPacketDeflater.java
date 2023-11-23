package com.ghostchu.mods.fabriczstdprotocol.pkthandler;

import com.github.luben.zstd.ZstdCompressCtx;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.encoding.VarInts;

public class ZstdPacketDeflater extends MessageToByteEncoder<ByteBuf> {
    private final ZstdCompressCtx compressCtx;
    private int compressionThreshold;

    public ZstdPacketDeflater(int compressionThreshold, int level, byte[] dict) {
        this.compressionThreshold = compressionThreshold;
        this.compressCtx = new ZstdCompressCtx();
        this.compressCtx.setLevel(level);
        if (dict != null && dict.length > 0) {
            this.compressCtx.loadDict(dict);
        } else {
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
