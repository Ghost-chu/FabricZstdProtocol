package com.ghostchu.mods.fabriczstdprotocol.packet;

public class ModPluginMessagingPacket {

    public ModPluginMessagingPacket(String event) {
        this.event = event;
    }
    private final int version = 1;
    private String event;

    public int getVersion() {
        return version;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
