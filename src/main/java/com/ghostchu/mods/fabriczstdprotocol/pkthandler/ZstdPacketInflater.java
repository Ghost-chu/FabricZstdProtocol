package com.ghostchu.mods.fabriczstdprotocol.pkthandler;

import com.github.luben.zstd.ZstdDecompressCtx;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.encoding.VarInts;

import java.util.List;

public class ZstdPacketInflater extends ByteToMessageDecoder {
    public static final int field_34057 = 2097152;
    public static final int MAXIMUM_PACKET_SIZE = 8388608;
    private int compressionThreshold;
    private boolean rejectsBadPackets;
    private ZstdDecompressCtx decompressCtx;

    public ZstdPacketInflater(int compressionThreshold, boolean rejectsBadPackets) {
        this.compressionThreshold = compressionThreshold;
        this.rejectsBadPackets = rejectsBadPackets;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> objects) throws Exception {
        if (buf.readableBytes() != 0) {
            int i = VarInts.read(buf);
            if (i == 0) {
                objects.add(buf.readBytes(buf.readableBytes()));
            } else {
                if (this.rejectsBadPackets) {
                    if (i < this.compressionThreshold) {
                        throw new DecoderException("Badly compressed packet - size of " + i + " is below server threshold of " + this.compressionThreshold);
                    }

                    if (i > MAXIMUM_PACKET_SIZE) {
                        throw new DecoderException("Badly compressed packet - size of " + i + " is larger than protocol maximum of 8388608");
                    }
                }
                try {
                    objects.add(Unpooled.wrappedBuffer(decompressCtx.decompress(buf.nioBuffer(),i)));
                }finally {
                    decompressCtx.reset();
                }
            }
        }
    }
}
