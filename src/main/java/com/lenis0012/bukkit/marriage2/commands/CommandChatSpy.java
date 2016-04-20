package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.config.Permissions;

public class CommandChatSpy extends Command {
    public CommandChatSpy(Marriage marriage) {
        super(marriage, "chatspy");
        setDescription("Enable admin chat spy.");
        setPermission(Permissions.CHAT_SPY);
        setHidden(true);
    }

    @Override
    public void execute() {
        MPlayer mPlayer = marriage.getMPlayer(player.getUniqueId());
        boolean mode = !mPlayer.isChatSpy();
        mPlayer.setChatSpy(mode);
        reply(mode ? Message.CHAT_SPY_ENABLED : Message.CHAT_SPY_DISABLED);
    }
}
