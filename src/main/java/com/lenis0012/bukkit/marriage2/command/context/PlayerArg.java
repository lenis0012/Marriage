package com.lenis0012.bukkit.marriage2.command.context;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.internal.MarriagePlugin;
import org.bukkit.entity.Player;

public class PlayerArg {
    private final Player player;
    private final MPlayer mPlayer;

    protected PlayerArg(Player player) {
        this.player = player;
        this.mPlayer = MarriagePlugin.getCore().getMPlayer(player);
    }

    public Player player() {
        return player;
    }

    public MPlayer data() {
        return mPlayer;
    }
}
