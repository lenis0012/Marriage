package com.lenis0012.bukkit.marriage2.internal;

public @interface Register {
	String name();
	
	Type type();
	
	int priority() default 5;
	
	public static enum Type {
		LOAD("Completed plugin pre-load stage."),
		ENABLE("Completed plugin load stage."),
		DISABLE("Plugin has been completely disabled.");
		
		private final String completionMessage;
		
		private Type(String completionMessage) {
			this.completionMessage = completionMessage;
		}
		
		public String getCompletionMessage() {
			return completionMessage;
		}
	}
}