package com.ghostchu.mods.fabriczstdprotocol.packet;

public class ServerResponseSupportZstdProtocolPacket extends ModPluginMessagingPacket {
    private int compressionThreshold;

    public ServerResponseSupportZstdProtocolPacket(int compressionThreshold, int compressionLevel) {
        super("SERVER_SUPPORT_ZSTD");
        this.compressionThreshold = compressionThreshold;
        this.compressionLevel = compressionLevel;
    }

    private int compressionLevel;
    public int getCompressionThreshold() {
        return compressionThreshold;
    }

    public void setCompressionThreshold(int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
    }

    public int getCompressionLevel() {
        return compressionLevel;
    }

    public void setCompressionLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

}
