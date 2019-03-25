package de.syscy.warpsystem.transfer.utils;

import de.syscy.warpsystem.transfer.packets.utils.Packet;

public interface PacketListener {
    void onReceive(Packet packet, String extra);
    boolean onSend(Packet packet);
}
