package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;

public class CommandChat extends Command {

    public CommandChat(MarriageCore marriage) {
        super(marriage, "chat");
        setDescription(Message.COMMAND_CHAT.toString());
    }

    @Override
    public void execute() {
        MPlayer mPlayer = marriage.getMPlayer(player);
        if(!mPlayer.isMarried()) {
            reply(Message.NOT_MARRIED);
            return;
        }

        boolean value = !mPlayer.isInChat();
        mPlayer.setInChat(value);
        reply(value ? Message.CHAT_ENABLED : Message.CHAT_DISABLED);
    }
}
