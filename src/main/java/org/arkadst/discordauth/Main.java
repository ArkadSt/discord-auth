package org.arkadst.discordauth;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.requests.GatewayIntent;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.UUID;

public class Main extends JavaPlugin {

    public static FileConfiguration config;
    public static final HashMap<UUID, Long> sessions_array;
    public static final HashMap<UUID, ExtendedSession> extended_session_array;
    private DiscordSRVListener discordsrv_listener;

    static {
        sessions_array = new HashMap<>();
        extended_session_array = new HashMap<>();
    }

    @Override
    public void onEnable() {

        if (!(new File(this.getDataFolder(), "config.yml")).exists()) {
            saveDefaultConfig();
        }

        config = getConfig();

        discordsrv_listener = new DiscordSRVListener(this);
        DiscordSRV.api.subscribe(discordsrv_listener);
        DiscordSRV.api.requireIntent(GatewayIntent.GUILD_MESSAGE_REACTIONS);
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
        getCommand("discordauth").setExecutor(new DiscordAuthCommand(this));

    }

    @Override
    public void onDisable() {
        DiscordSRV.api.unsubscribe(discordsrv_listener);
    }

    public static boolean sessionActive(UUID uuid){

        long session_time = config.getLong("session_time") * 1000L;

        if (sessions_array.containsKey(uuid)) {
            long session_start_time = sessions_array.get(uuid);
            return System.currentTimeMillis() - session_start_time <= session_time;
        }

        return false;
    }

    public static boolean extendedSessionActive(UUID uuid, InetAddress ip){

        long extended_session_time = config.getLong("extended_session_time") * 1000L;

        if (extended_session_array.containsKey(uuid)) {
            ExtendedSession extended_session = extended_session_array.get(uuid);
            return System.currentTimeMillis() - extended_session.session_start_time <= extended_session_time
                    && extended_session.ip.equals(ip);
        }
        return false;
    }

    public static void stopSession(UUID uuid){
        sessions_array.remove(uuid);
        extended_session_array.remove(uuid);
    }

    public static void refreshEmojis(){

        long channel_id = config.getLong("channel_id");
        long message_id = config.getLong("message_id");
        String start_session_emoji = config.getString("start_session_emoji");
        String stop_session_emoji = config.getString("stop_session_emoji");

        DiscordUtil.getJda().getTextChannelById(channel_id).clearReactionsById(message_id).complete();
        DiscordUtil.getJda().getTextChannelById(channel_id).addReactionById(message_id, start_session_emoji).complete();
        DiscordUtil.getJda().getTextChannelById(channel_id).addReactionById(message_id, stop_session_emoji).complete();
    }

}
