package de.syscy.warpsystem.transfer.packets.spigot;

import de.syscy.warpsystem.transfer.packets.utils.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RequestGlobalWarpNamesPacket implements Packet {
    @Override
    public void write(DataOutputStream out) throws IOException { }

    @Override
    public void read(DataInputStream in) throws IOException { }
}
