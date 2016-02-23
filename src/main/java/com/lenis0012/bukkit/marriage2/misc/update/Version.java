package com.lenis0012.bukkit.marriage2.misc.update;

public class Version {
    private final String name;
    private final ReleaseType type;
    private final String serverVersion;
    private final String downloadURL;

    public Version(String name, ReleaseType type, String serverVersion, String downloadURL) {
        this.name = name;
        this.type = type;
        this.serverVersion = serverVersion;
        this.downloadURL = downloadURL;
    }

    public String getName() {
        return name;
    }

    public ReleaseType getType() {
        return type;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    protected String getDownloadURL() {
        return downloadURL;
    }
}
