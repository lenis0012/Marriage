package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.config.Message;

public class CommandChat extends Command {

    public CommandChat(Marriage marriage) {
        super(marriage, "chat");
        setDescription("Enable partner-only chat mode");
    }

    @Override
    public void execute() {
        MPlayer mPlayer = marriage.getMPlayer(player.getUniqueId());
        if(!mPlayer.isMarried()) {
            reply(Message.NOT_MARRIED);
            return;
        }

        boolean value = !mPlayer.isInChat();
        mPlayer.setInChat(value);
        reply(value ? Message.CHAT_ENABLED : Message.CHAT_DISABLED);
    }
}
