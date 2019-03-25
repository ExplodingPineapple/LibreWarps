package de.syscy.warpsystem.transfer.packets.utils;

import de.codingair.warpsystem.transfer.packets.bungee.*;
import de.codingair.warpsystem.transfer.packets.spigot.*;
import de.syscy.warpsystem.transfer.packets.bungee.*;
import de.syscy.warpsystem.transfer.packets.spigot.*;

public enum PacketType {
    ERROR(0, null),
    UploadIconPacket(1, de.syscy.warpsystem.transfer.packets.spigot.UploadIconPacket.class),
    DeployIconPacket(2, de.syscy.warpsystem.transfer.packets.bungee.DeployIconPacket.class),
    InitialPacket(3, de.syscy.warpsystem.transfer.packets.bungee.InitialPacket.class),
    RequestInitialPacket(4, de.syscy.warpsystem.transfer.packets.spigot.RequestInitialPacket.class),
    RequestServerStatusPacket(5, de.syscy.warpsystem.transfer.packets.spigot.RequestServerStatusPacket.class),

    PublishGlobalWarpPacket(10, de.syscy.warpsystem.transfer.packets.spigot.PublishGlobalWarpPacket.class),
    PrepareTeleportPacket(11, de.syscy.warpsystem.transfer.packets.spigot.PrepareTeleportPacket.class),
    TeleportPacket(12, de.syscy.warpsystem.transfer.packets.bungee.TeleportPacket.class),
    DeleteGlobalWarpPacket(13, DeleteGlobalWarpPacket.class),
    RequestGlobalWarpNamesPacket(14, RequestGlobalWarpNamesPacket.class),
    SendGlobalWarpNamesPacket(15, de.syscy.warpsystem.transfer.packets.bungee.SendGlobalWarpNamesPacket.class),
    UpdateGlobalWarpPacket(16, de.syscy.warpsystem.transfer.packets.bungee.UpdateGlobalWarpPacket.class),
    PerformCommandPacket(17, PerformCommandPacket.class),
    RequestUUIDPacket(18, RequestUUIDPacket.class),
    SendUUIDPacket(19, SendUUIDPacket.class),
    TeleportPlayerToPlayerPacket(20, TeleportPlayerToPlayerPacket.class),
    TeleportPlayerToCoordsPacket(21, TeleportPlayerToCoordsPacket.class),
    PrepareServerSwitchPacket(22, PrepareServerSwitchPacket.class),
    PrepareLoginMessagePacket(23, PrepareLoginMessagePacket.class),

    BooleanPacket(100, de.syscy.warpsystem.transfer.packets.general.BooleanPacket.class),
    IntegerPacket(101, de.syscy.warpsystem.transfer.packets.general.IntegerPacket.class),

    AnswerPacket(200, AnswerPacket.class),
    ;

    private int id;
    private Class<?> packet;

    PacketType(int id, Class<?> packet) {
        this.id = id;
        this.packet = packet;
    }

    public int getId() {
        return id;
    }

    public Class<?> getPacket() {
        return packet;
    }

    public static PacketType getById(int id) {
        for(PacketType packetType : values()) {
            if(packetType.getId() == id) return packetType;
        }

        return ERROR;
    }

    public static PacketType getByObject(Object packet) {
        if(packet == null) return ERROR;

        for(PacketType packetType : values()) {
            if(packetType.equals(ERROR)) continue;

            if(packetType.getPacket().equals(packet.getClass())) return packetType;
        }

        return ERROR;
    }
}
