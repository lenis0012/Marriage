package com.lenis0012.bukkit.marriage2.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lenis0012.bukkit.marriage2.MData;
import com.lenis0012.bukkit.marriage2.commands.CommandPriest;
import com.lenis0012.bukkit.marriage2.internal.data.DataConverter;
import org.bukkit.event.Listener;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.commands.Command;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.config.Settings;
import com.lenis0012.bukkit.marriage2.internal.data.DataManager;
import com.lenis0012.bukkit.marriage2.internal.data.MarriageData;
import com.lenis0012.bukkit.marriage2.internal.data.MarriagePlayer;
import com.lenis0012.bukkit.marriage2.misc.ListQuery;

public class MarriageCore extends MarriageBase {
	private final Map<UUID, MarriagePlayer> players = Collections.synchronizedMap(new HashMap<UUID, MarriagePlayer>());
	private DataManager dataManager;
	
	public MarriageCore(MarriagePlugin plugin) {
		super(plugin);
	}
	
	@Register(name = "config", type = Register.Type.ENABLE, priority = 0)
	public void loadConfig() {
//		plugin.saveDefaultConfig();
		enable();
		Settings.reloadAll(this);
		Message.reloadAll(this);
	}
	
	@Register(name = "database", type = Register.Type.ENABLE)
	public void loadDatabase() {
		this.dataManager = new DataManager(this, plugin.getConfig());
	}
	
	@Register(name = "listeners", type = Register.Type.ENABLE)
	public void registerListeners() {
		for(Listener listener : findObjects("com.lenis0012.bukkit.marriage2.listeners", Listener.class, this)) {
			register(listener);
		}
	}
	
	@Register(name = "commands", type = Register.Type.ENABLE)
	public void registerCommands() {
		for(Class<? extends Command> command : findClasses("com.lenis0012.bukkit.marriage2.commands", Command.class)) {
			register(command);
		}
	}

	@Register(name = "database", type = Register.Type.DISABLE)
	public void saveDatabase() {
		unloadAll();
	}

	@Register(name = "converter", type = Register.Type.ENABLE, priority = 10)
	public void loadConverter() {
		DataConverter converter = new DataConverter(this);
		if(converter.isOutdated()) {
			converter.convert();
		}
	}

	@Override
	public MPlayer getMPlayer(UUID uuid) {
		MarriagePlayer player = players.get(uuid);
		if(player == null) {
			player = dataManager.loadPlayer(uuid);
			players.put(uuid, player);
		}
		
		return player;
	}

	@Override
	public MData marry(MPlayer player1, MPlayer player2) {
		MarriageData mdata = new MarriageData(player1.getUniqueId(), player2.getUniqueId());
		((MarriagePlayer) player1).addMarriage(mdata);
		((MarriagePlayer) player2).addMarriage(mdata);
        dataManager.savePlayer((MarriagePlayer) player1);
		return mdata;
	}

	@Override
	public ListQuery getMarriageList(int scale, int page) {
		return dataManager.listMarriages(scale, page);
	}

	public DataManager getDataManager() {
		return dataManager;
	}

    public void removeMarriage(final MData mdata) {
        new Thread() {
            @Override
            public void run() {
                dataManager.deleteMarriage(mdata.getPlayer1Id(), mdata.getPllayer2Id());
            }
        }.start();
    }
	
	/**
	 * Unload player from the memory
	 * 
	 * @param uuid of player
	 */
	public void unloadPlayer(UUID uuid) {
		final MarriagePlayer mPlayer = players.remove(uuid);
		if(mPlayer != null) {
			new Thread() {
				@Override
				public void run() {
					dataManager.savePlayer(mPlayer);
				}
			}.start();
		}
	}

	public void unloadAll() {
		for(MarriagePlayer mp : players.values()) {
			dataManager.savePlayer(mp);
		}
		players.clear();
	}
}
