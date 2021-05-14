package org.arkadst.discordauth;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.PrivateChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.dependencies.jda.api.requests.RestAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class JDAListener extends ListenerAdapter {

    Main main;

    public JDAListener (Main main){
        this.main = main;
    }

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {

        User user = event.getUser();

        if (user.isBot()){
            return;
        }

        long message_id = event.getMessageIdLong();

        if (message_id == Main.config.getLong("message_id")){
            RestAction<PrivateChannel> private_channel = user.openPrivateChannel();
            UUID uuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(event.getUserId());
            if (uuid != null) {
                if (event.getReactionEmote().getEmoji().equals(Main.config.getString("start_session_emoji"))) {
                    Main.stopSession(uuid);
                    Main.sessions_array.put(uuid, System.currentTimeMillis());
                    private_channel.flatMap(channel -> channel.sendMessage("Your session has been started. You have " + Main.config.getString("session_time") + " seconds to join the server.")).queue();

                } else if (event.getReactionEmote().getEmoji().equals(Main.config.getString("stop_session_emoji"))){
                        Main.stopSession(uuid);

                            Bukkit.getScheduler().runTask(main, () -> {
                                try {
                                Bukkit.getPlayer(uuid).kick(Component.text(Main.config.getString("session_stopped_kick_reason")).color(NamedTextColor.RED));
                                } catch (NullPointerException e) {
                                    main.getLogger().info("Player with UUID=" + uuid + " is not online thus cannot be kicked.");
                                }
                            });

                    private_channel.flatMap(channel -> channel.sendMessage(Main.config.getString("session_stopped_kick_reason"))).queue();

                } else {
                    private_channel.flatMap(channel -> channel.sendMessage("Invalid action.")).queue();
                }

                event.getReaction().removeReaction(event.getUser()).queue();

            } else {
                private_channel.flatMap(channel -> channel.sendMessage("Your Discord account is not linked to your game account. You need to do that in order no be able to manage sessions.")).queue();
            }
        }
    }
}
