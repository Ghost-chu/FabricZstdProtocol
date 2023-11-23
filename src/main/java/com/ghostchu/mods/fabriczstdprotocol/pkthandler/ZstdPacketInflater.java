package com.ghostchu.mods.fabriczstdprotocol.pkthandler;

import com.github.luben.zstd.ZstdDecompressCtx;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.encoding.VarInts;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.zip.DataFormatException;

public class ZstdPacketInflater extends ByteToMessageDecoder {
    /**
     * The maximum size allowed for a compressed packet. Has value {@value}.
     */
    public static final int MAXIMUM_PACKET_SIZE = 0x800000;
    private int compressionThreshold;
    private boolean rejectsBadPackets;
    private final ZstdDecompressCtx decompressCtx;

    public ZstdPacketInflater(int compressionThreshold, boolean rejectsBadPackets) {
        this.compressionThreshold = compressionThreshold;
        this.rejectsBadPackets = rejectsBadPackets;
        this.decompressCtx = new ZstdDecompressCtx();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> objects) throws Exception {
        if (buf.readableBytes() == 0) {
            return;
        }
        int i = VarInts.read(buf);
        if (i == 0) {
            objects.add(buf.readBytes(buf.readableBytes()));
            return;
        }
        if (this.rejectsBadPackets) {
            if (i < this.compressionThreshold) {
                throw new DecoderException("Badly compressed packet - size of " + i + " is below server threshold of " + this.compressionThreshold);
            }
            if (i > MAXIMUM_PACKET_SIZE) {
                throw new DecoderException("Badly compressed packet - size of " + i + " is larger than protocol maximum of 8388608");
            }
        }
        ByteBuffer inputBuffer = getInputBuf(buf);
        ByteBuf byteBuf = this.inflate(ctx, inputBuffer, i);
        objects.add(byteBuf);
    }

    private ByteBuffer getInputBuf(ByteBuf buf) {
        ByteBuffer byteBuffer;
        if (buf.nioBufferCount() > 0) {
            byteBuffer = buf.nioBuffer();
            buf.skipBytes(buf.readableBytes());
        } else {
            byteBuffer = ByteBuffer.allocateDirect(buf.readableBytes());
            buf.readBytes(byteBuffer);
            byteBuffer.flip();
        }
        return byteBuffer;
    }

    private ByteBuf inflate(ChannelHandlerContext context, ByteBuffer inputBuffer, int expectedSize) throws DataFormatException {
        ByteBuf byteBuf = context.alloc().directBuffer(expectedSize);
        try {
            ByteBuffer byteBuffer = byteBuf.internalNioBuffer(0, expectedSize);
            int i = byteBuffer.position();
            decompressCtx.decompress(byteBuffer, inputBuffer);
            int j = byteBuffer.position() - i;
            if (j != expectedSize) {
                throw new DecoderException("Badly compressed packet - actual length of uncompressed payload " + j + " is does not match declared size " + expectedSize);
            }
            byteBuf.writerIndex(byteBuf.writerIndex() + j);
            return byteBuf;
        } catch (Exception exception) {
            byteBuf.release();
            throw exception;
        }
    }

    public void setCompressionThreshold(int compressionThreshold, boolean rejectsBadPackets) {
        this.compressionThreshold = compressionThreshold;
        this.rejectsBadPackets = rejectsBadPackets;
    }
}

