package com.teampingui.models;

import java.util.prefs.Preferences;

public class Settings {

    private static final Preferences prefs;

    private static final String USERNAME = "username";

    static {
        prefs = Preferences.userNodeForPackage(Settings.class);
    }

    public static String getUsername() {
        return prefs.get(USERNAME, "");
    }

    public static void setUsername(String username) {
        prefs.put(USERNAME, username);
    }
}
