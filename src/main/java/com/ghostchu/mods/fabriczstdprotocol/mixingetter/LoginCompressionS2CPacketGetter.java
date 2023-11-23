package com.ghostchu.mods.fabriczstdprotocol.mixingetter;

import org.apache.commons.lang3.NotImplementedException;

public interface LoginCompressionS2CPacketGetter {
    default boolean isZstd() {
        throw new NotImplementedException();
    }

    default int getCompressionLevel() {
        throw new NotImplementedException();
    }
}
