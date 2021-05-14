package org.arkadst.discordauth;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import github.scarsz.discordsrv.DiscordSRV;

import java.util.UUID;

public class EventListener implements Listener {

    Main main;
    public EventListener(Main main){
        this.main = main;
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        String discordId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(uuid);
        if (discordId != null) {
            if (!Main.sessionActive(uuid) && !Main.extendedSessionActive(uuid, event.getAddress())) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        Component.text(Main.config.getString("no_active_session_kick_reason")).color(NamedTextColor.RED));
            }
        }
    }

    public void onPlayerJoin(PlayerJoinEvent event){
        Main.stopSession(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!event.getReason().equals(PlayerQuitEvent.QuitReason.KICKED)){
            Player player = event.getPlayer();
            Main.stopSession(player.getUniqueId());
            Main.extended_session_array.put(player.getUniqueId(), new ExtendedSession(event.getPlayer()));
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event){
        if (!event.reason().equals(Component.text(Main.config.getString("session_stopped_kick_reason")).color(NamedTextColor.RED))){
            Player player = event.getPlayer();
            Main.stopSession(player.getUniqueId());
            Main.extended_session_array.put(player.getUniqueId(), new ExtendedSession(event.getPlayer()));
        }
    }

}
