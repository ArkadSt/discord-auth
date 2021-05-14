package org.arkadst.discordauth;

import org.bukkit.entity.Player;

import java.net.InetAddress;

public class ExtendedSession {
    long session_start_time;
    InetAddress ip;

    public ExtendedSession(Player player) {
        this.ip = player.getAddress().getAddress();
        session_start_time = System.currentTimeMillis();
    }
}
