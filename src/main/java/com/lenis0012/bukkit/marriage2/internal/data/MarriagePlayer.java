package com.lenis0012.bukkit.marriage2.internal.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import net.minecraft.util.com.google.common.collect.Lists;

import com.lenis0012.bukkit.marriage2.Gender;
import com.lenis0012.bukkit.marriage2.MData;
import com.lenis0012.bukkit.marriage2.MPlayer;

public class MarriagePlayer implements MPlayer {
	private final List<UUID> requests = Lists.newArrayList();
	private final UUID uuid;
	private MData marriage;
	private Gender gender;
	private boolean inChat;
	
	public MarriagePlayer(UUID uuid, ResultSet data) throws SQLException {
		this.uuid = uuid;
		if(data.next()) {
			this.gender = Gender.valueOf(data.getString("gender"));
		}
	}
	
	void addMarriage(MarriageData data) {
		this.marriage = data;
	}
	
	void save(PreparedStatement ps) throws SQLException {
		ps.setString(1, uuid.toString());
		ps.setString(2, gender.toString());
		ps.setLong(3, System.currentTimeMillis());
	}

	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public void requestMarriage(UUID from) {
		requests.add(from);
	}

	@Override
	public boolean isMarriageRequested(UUID from) {
		return requests.contains(from);
	}

	@Override
	public Gender getGender() {
		return gender;
	}

	@Override
	public void setGender(Gender gender) {
		this.gender = gender;
	}

	@Override
	public MData getMarriage() {
		return marriage;
	}

	@Override
	public boolean isMarried() {
		return marriage != null;
	}

	@Override
	public boolean isInChat() {
		return inChat;
	}

	@Override
	public void setInChat(boolean inChat) {
		this.inChat = inChat;
	}
}