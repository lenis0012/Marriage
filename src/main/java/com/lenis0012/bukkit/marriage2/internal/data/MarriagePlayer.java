package com.lenis0012.bukkit.marriage2.internal.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.lenis0012.bukkit.marriage2.Gender;
import com.lenis0012.bukkit.marriage2.MData;
import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.config.Settings;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import com.lenis0012.bukkit.marriage2.internal.MarriagePlugin;
import com.lenis0012.bukkit.marriage2.misc.Cooldown;

public class MarriagePlayer implements MPlayer {
	private final Cooldown<UUID> requests;
	private final UUID uuid;
	private String lastName;
	private MData marriage;
	private Gender gender = Gender.UNKNOWN;
	private boolean inChat;
	private boolean priest;
	private long lastLogin;
	private long lastLogout;

	public MarriagePlayer(UUID uuid, ResultSet data) throws SQLException {
		this.uuid = uuid;
		if(data.next()) {
			this.lastName = data.getString("last_name");
			this.gender = Gender.valueOf(data.getString("gender"));
			this.priest = data.getBoolean("priest");
			this.lastLogout = data.getLong("lastlogin");
		}
		this.lastLogin = System.currentTimeMillis();
		this.requests = new Cooldown<>(Settings.REQUEST_EXPRY.value(), TimeUnit.SECONDS);
	}
	
	public void addMarriage(MarriageData data) {
		this.marriage = data;
	}
	
	void save(PreparedStatement ps) throws SQLException {
		ps.setString(1, uuid.toString());
		ps.setString(2, lastName);
		ps.setString(3, gender.toString());
		ps.setBoolean(4, priest);
		ps.setLong(5, System.currentTimeMillis());
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String name) {
		this.lastName = name;
	}

	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public void requestMarriage(UUID from) {
		requests.set(from);
	}

	@Override
	public boolean isMarriageRequested(UUID from) {
		return requests.isCached(from);
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

	@Override
	public MPlayer getPartner() {
		Marriage core = MarriagePlugin.getCore();
		if(marriage != null) {
			UUID id = uuid.equals(marriage.getPlayer1Id()) ? marriage.getPllayer2Id() : marriage.getPlayer1Id();
			return core.getMPlayer(id);
		}
		
		return null;
	}

	@Override
	public void divorce() {
		if(marriage == null) {
			return;
		}

		((MarriageCore) MarriagePlugin.getCore()).removeMarriage(marriage);
		MarriagePlayer partner = (MarriagePlayer) getPartner();
		partner.marriage = null;
		this.marriage = null;
	}

	@Override
	public boolean isPriest() {
		return priest;
	}

	@Override
	public void setPriest(boolean priest) {
		this.priest = priest;
	}

	public long getLastLogin() {
		return lastLogin;
	}

	public long getLastLogout() {
		return lastLogout;
	}
}