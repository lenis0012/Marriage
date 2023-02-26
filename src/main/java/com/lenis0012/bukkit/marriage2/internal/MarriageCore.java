package com.lenis0012.bukkit.marriage2.internal;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Relationship;
import com.lenis0012.bukkit.marriage2.events.PlayerMarryEvent;
import com.lenis0012.bukkit.marriage2.internal.data.DataManager;
import com.lenis0012.bukkit.marriage2.internal.data.MarriageData;
import com.lenis0012.bukkit.marriage2.internal.data.MarriagePlayer;
import com.lenis0012.bukkit.marriage2.misc.ListQuery;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MarriageCore {
    private final Map<UUID, MarriagePlayer> players = new ConcurrentHashMap<>();
    private final MarriagePlugin plugin;

    public MarriageCore(MarriagePlugin plugin) {
        this.plugin = plugin;
    }

    public MPlayer getMPlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if(player != null && player.isOnline()) {
            return getMPlayer(player);
        }

        MarriagePlayer mp = players.get(uuid);
        if(mp == null) {
            // Load from database, but don't save.
            mp = plugin.getDataManager().loadPlayer(uuid);
        }

        return mp;
    }

    public MPlayer getMPlayer(Player player) {
        MarriagePlayer mp = players.get(player.getUniqueId());
        if(mp == null) {
            mp = plugin.getDataManager().loadPlayer(player.getUniqueId());
            players.put(player.getUniqueId(), mp);
        }

        return mp;
    }

    public Relationship marry(MPlayer player1, MPlayer player2) {
        return marry(player1, player2, null);
    }

    public Relationship marry(MPlayer player1, MPlayer player2, MPlayer priest) {
        PlayerMarryEvent event = new PlayerMarryEvent(player1, player2, priest);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            return null;
        }

        MarriageData mdata = new MarriageData(plugin.getDataManager(), player1.getUniqueId(), player2.getUniqueId());
        mdata.saveAsync();
        ((MarriagePlayer) player1).addMarriage(mdata);
        ((MarriagePlayer) player2).addMarriage(mdata);
        return mdata;
    }

    public ListQuery getMarriageList(int scale, int page) {
        return plugin.getDataManager().listMarriages(scale, page);
    }

    public void setMPlayer(UUID uuid, MarriagePlayer mp) {
        players.put(uuid, mp);
    }

    public boolean isMPlayerSet(UUID uuid) {
        return players.containsKey(uuid);
    }

    public DataManager getDataManager() {
        return plugin.getDataManager();
    }

    public void removeMarriage(final Relationship mdata) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getDataManager().deleteMarriage(mdata.getPlayer1Id(), mdata.getPllayer2Id());
        });
    }

    /**
     * Unload player from the memory
     *
     * @param uuid of player
     */
    public void unloadPlayer(UUID uuid) {
        final MarriagePlayer mPlayer = players.remove(uuid);
        if(mPlayer != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                plugin.getDataManager().savePlayer(mPlayer);
            });
        }
    }

    public void savePlayer(final MarriagePlayer mPlayer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getDataManager().savePlayer(mPlayer);
        });
    }

    public void unloadAll() {
        for(MarriagePlayer mp : players.values()) {
            plugin.getDataManager().savePlayer(mp);
        }
        players.clear();
    }
}
