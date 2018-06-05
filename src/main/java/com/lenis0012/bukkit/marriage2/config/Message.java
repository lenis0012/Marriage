package com.lenis0012.bukkit.marriage2.config;

import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.misc.BConfig;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public enum Message {
    PLAYER_NOT_FOUND("&cNo player named %s was found!"),
    TARGET_ALREADY_MARRIED("&cPlayers %s is already married to someone!"),
    ALREADY_MARRIED("&cYou are already married to someone!"),
    MARRIED("&a&lPlayer %s and %s have just married!"),
    MARRIAGE_REQUESTED("&aPlayer %s has requested you to marry with them, use &e/marry %s &ato accept it."),
    REQUEST_SENT("&aYou have proposed to %s!"),
    NOT_MARRIED("&cYou are currently not married with someone!"),
    DIVORCED("&aPlayer %s and %s have divorced!"),
    HOME_TELEPORT("&aYou have been teleported to your marriage home!"),
    HOME_NOT_SET("&cYou currently do not have a home set for your marriage!"),
    NO_ITEM("&cYou aren't holding an item to gift!"),
    ITEM_GIFTED("&aYou have given %s of %s to your partner!"),
    GIFT_RECEIVED("&aYou have received %s of %s as a gift from your partner!"),
    PARTNER_NOT_ONLINE("&cYour partner is currently not online!"),
    FETCHING_LIST("&eFetching player marriage list..."),
    HOME_SET("&aYou have set a home for your marriage!"),
    INVALID_FORMAT("&cThe argument could not be parsed to an integer!"),
    INVALID_GENDER("&cThat is not a valid gender! Pick \"male\" or \"female\""),
    GENDER_SET("&aYour gender has been set to %s!"),
    MARRY_SELF("&cYou cannot marry yourself!"),
    NEGATIVE_NUMBER("&cYou must enter a positive number!"),
    PRIEST_ADDED("&aSet player as a priest, he may now marry other players!"),
    PRIEST_REMOVED("&aUnset player as a priest, he may now no longer marry othewr players!"),
    TELEPORTED("&aYou have been teleported to your partners location!"),
    TELEPORTED_2("&aYour partner has just teleported to you!"),
    ONLINE_SINCE("&aYour partner has been &2ONLINE &afor %s!"),
    OFFLINE_SINCE("&aYour partner has been &cOFFLINE &afor %ss!"),
    NOT_A_PRIEST("&cYou are not permitted to marry 2 players!"),
    COOLDOWN("&cYou may not perform this action too frequently!"),
    UPDATE_AVAILABLE("&f&l[Marriage] &eThere is a new update available! %s for %s\nType &6/marry update &eto update now."),
    PAID_FEE("&a%s has been removed from your balance."),
    INSUFFICIENT_MONEY("&cYou have insufficient funds, costs %s"),
    PARTNER_FEE("&cYour partner couldn't pay the marriage fee!"),
    MARRIED_TO("&fmarried to %s"),
    CHAT_ENABLED("&aYou are now in marriage chat mode!"),
    CHAT_DISABLED("&aYou are no longer in marriage chat mode!"),
    CHAT_SPY_ENABLED("&aNow spying on marriage private chat!"),
    CHAT_SPY_DISABLED("&aNo longer spying on marriage chat!"),
    NO_HEALTH("&cYou don't have enough health to share!"),
    FULL_HEALTH("&cYour partner already has full health!"),
    HEALTH_GIVEN("&aYou healed your partner by %s hearts!"),
    HEALTH_TAKEN("&aYou were healed by your partner with %s hearts!"),
    PVP_ENABLED("&aYou have enabled pvp with your partner!"),
    PVP_DISABLED("&aYou have disabled pvp with your partner!"),
    PARTNER_PVP("&aYour partner has changed pvp rules."),
    BONUS_EXP("&aYou gained %s extra EXP for leveling with your partner!"),
    CONFIG_RELOAD("&aConfiguration settings were reloaded, please note that some settings may not apply until reboot."),
    GENDER_ALREADY_CHANGED("&cYou have already specified your gender, you can only do this once."),
    TELEPORT_UNSAFE("&cThe location you are trying to teleport to is unsafe or obstructed"),

    // COMMANDS
    COMMAND_MARRY("Request a marriage with another player"),
    COMMAND_MARRY_PRIEST("Marry 2 players with eachother"),
    COMMAND_CHAT("Enable partner-only chat mode"),
    COMMAND_DIVORCE("Divorce your current partner"),
    COMMAND_GENDER("Specify your gender"),
    COMMAND_GIFT("Gift the item(s) you currently hold"),
    COMMAND_HOME("Teleport to your marriage home"),
    COMMAND_LIST("View a list of all married players"),
    COMMAND_SEEN("Check when your partner last logged in"),
    COMMAND_SETHOME("Set a home for you a your partner"),
    COMMAND_TELEPORT("Teleport to your partner"),
    COMMAND_HEAL("Send your health to your partner"),
    COMMAND_PVP("Enable/disable pvping with your partner"),

    // WORDS
    STATUS("&aStatus: %s"),
    SINGLE("&fsingle"),
    ON_OFF("on/off");

    private final String defaultMessage;
    private String message;

    Message(String def) {
        this.defaultMessage = def;
        this.message = def; // Use default if not loaded yet
    }

    private void reload(BConfig config) {
        this.message = config.getOrSet(name().toLowerCase(), defaultMessage);
    }

    @Override
    public String toString() {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void send(Player player, Object... params) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(message, params)));
    }

    public static void reloadAll(Marriage marriage) {
        BConfig config = marriage.getBukkitConfig("messages.yml");
        for(Message message : values()) {
            message.reload(config);
        }

        config.save();
    }
}