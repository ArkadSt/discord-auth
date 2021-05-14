package org.arkadst.discordauth;

import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.util.DiscordUtil;

public class DiscordSRVListener {

    Main main;

    public DiscordSRVListener (Main main){
        this.main = main;
    }

    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        DiscordUtil.getJda().addEventListener(new JDAListener(main));
        Main.refreshEmojis();
    }
}
