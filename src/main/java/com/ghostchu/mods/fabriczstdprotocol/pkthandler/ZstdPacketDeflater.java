//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ghostchu.mods.fabriczstdprotocol.pkthandler;

import com.github.luben.zstd.ZstdCompressCtx;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.encoding.VarInts;

public class ZstdPacketDeflater extends MessageToByteEncoder<ByteBuf> {
  //  private final byte[] deflateBuffer = new byte[8192];
   // private final Deflater deflater;
    private int compressionThreshold;

    private ZstdCompressCtx compressCtx;

    public ZstdPacketDeflater(int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
    //    this.deflater = new Deflater();
        this.compressCtx = new ZstdCompressCtx();
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
            System.arraycopy(bs,0,targetData,0,i);
            byteBuf2.writeBytes(compressCtx.compress(targetData));
//            while(!this.deflater.finished()) {
//                int j = this.deflater.deflate(this.deflateBuffer);
//                byteBuf2.writeBytes(this.deflateBuffer, 0, j);
//            }

    //        this.deflater.reset();
        }

    }

    public int getCompressionThreshold() {
        return this.compressionThreshold;
    }

    public void setCompressionThreshold(int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
    }
}
