package org.arkadst.discordauth;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DiscordAuthCommand implements CommandExecutor {

    Main main;

    public DiscordAuthCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            switch (args[0]) {
                case "reload":
                    main.reloadConfig();
                    Main.config = main.getConfig();
                    Main.refreshEmojis();
                    sender.sendMessage(ChatColor.GREEN + "Configuration was reloaded successfully");
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "No such command.");
                    break;
            }

        } else {
            sender.sendMessage(ChatColor.RED + "No such command.");
        }
        return true;
    }
}
