package me.lenis0012.mr.lang;

public class Messages {
	private static final LangConfig lang = LangConfig.get();
	
	/** Messages */
	public static final String NO_PERMISSION = lang.getMessageWithDefault("no permission", "No permission!");
	public static final String NO_CONSOLE = lang.getMessageWithDefault("no console", "You must be a player!");
	public static final String NO_REQUEST = lang.getMessageWithDefault("no request", "You don't got a request!");
	public static final String NOT_ONLINE = lang.getMessageWithDefault("not online", "That player is not online!");
	public static final String MARRIED = lang.getMessageWithDefault("married", "{USER1} has married with {USER2}!");
	public static final String DIVORCED = lang.getMessageWithDefault("divorced", "{USER1} has divorced with {USER2}!");
	public static final String NO_PARTNER = lang.getMessageWithDefault("no partner", "You are not married!");
	public static final String LEFT_CHAT = lang.getMessageWithDefault("left chat", "You have left the partner chat!");
	public static final String JOINED_CHAT = lang.getMessageWithDefault("joined chat", "You have joined the partner chat!");
	public static final String GIFT_SENT = lang.getMessageWithDefault("gift sent", "Gift sent!");
	public static final String GIFT_RECEIVED = lang.getMessageWithDefault("gift received", "You got a {ITEM} from your partner!");
	public static final String INVALID_ITEM = lang.getMessageWithDefault("invalid item", "Invalid item in your hand!");
	public static final String NO_HOME = lang.getMessageWithDefault("no home", "You don't have a home set!");
	public static final String HOME_TP = lang.getMessageWithDefault("home tp", "Teleporting to your home!");
	public static final String NO_PARTNERS = lang.getMessageWithDefault("no partners", "There are no married players on this server!");
	public static final String REQUEST_SENT = lang.getMessageWithDefault("request sent", "Request sent!");
	public static final String NOT_YOURSELF = lang.getMessageWithDefault("not yourself", "You can't marry yourself!");
	public static final String ALREADY_MARRIED = lang.getMessageWithDefault("already married", "You already married!");
	public static final String HAS_PARTNER = lang.getMessageWithDefault("has partner", "{USER} already has a partner!");
	public static final String REQUEST_RECEIVED = lang.getMessageWithDefault("request received", "{USER} wants to marry with you.\ntype {COMMAND} to accept");
	public static final String INVALID_PLAYER = lang.getMessageWithDefault("invalid player", "Invalid player!");
	public static final String HOME_SET = lang.getMessageWithDefault("home set", "Home set!");
	public static final String PARTNER_SETHOME = lang.getMessageWithDefault("partner sethome", "Your partner has set your home!");
	public static final String PARTNER_TELEPORTING = lang.getMessageWithDefault("partner teleporting", "Your partner is teleporting to you!");
	public static final String GIFT_CREATIVE = lang.getMessageWithDefault("gift creative", "You may not gift items in creative!");
	public static final String OFFLINE_SINCE = lang.getMessageWithDefault("offline since", "Your partner is offline since {TIME}");
	public static final String ONLINE_SINCE = lang.getMessageWithDefault("online since", "Your partner is online since {TIME}");
	
	/** Words */
	public static final String PAGE = lang.getWordWithDefault("page", "Page");
	public static final String PARTNERS = lang.getWordWithDefault("partners", "Partners");
	public static final String TELEPORTING = lang.getWordWithDefault("teleporting", "Teleporting");
	public static final String SECONDS = lang.getWordWithDefault("seconds", "sec");
	public static final String MINUTES = lang.getWordWithDefault("seconds", "min");
	public static final String HOURS = lang.getWordWithDefault("seconds", "hours");
	public static final String DAYS = lang.getWordWithDefault("days", "days");
	public static final String WEEKS = lang.getWordWithDefault("weeks", "weeks");
	public static final String MONTHS = lang.getWordWithDefault("months", "months");
	public static final String YEARS = lang.getWordWithDefault("years", "years");
}