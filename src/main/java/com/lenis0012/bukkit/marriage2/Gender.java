package com.lenis0012.bukkit.marriage2;

import com.lenis0012.bukkit.marriage2.config.Settings;

/**
 * @deprecated Use {@link PlayerGender} instead
 */
@Deprecated
public enum Gender {
    /**
     * Opposite of female
     */
    MALE,
    /**
     * Opposite of male
     */
    FEMALE,
    /**
     * Not set
     */
    UNKNOWN;

    /**
     * Get chat prefix for gender.
     *
     * @return Chat prefix
     */
    public String getChatPrefix() {
        switch(this) {
            case MALE:
                for(PlayerGender gender : Genders.getOptions()) {
                    if (gender.isMale()) {
                        return gender.getChatPrefix();
                    }
                }
                return Settings.PREFIX_GENDERLESS.value();
            case FEMALE:
                for(PlayerGender gender : Genders.getOptions()) {
                    if (gender.isFemale()) {
                        return gender.getChatPrefix();
                    }
                }
                return Settings.PREFIX_GENDERLESS.value();
            default:
                return Settings.PREFIX_GENDERLESS.value();
        }
    }
}