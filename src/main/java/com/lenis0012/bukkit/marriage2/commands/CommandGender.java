package com.lenis0012.bukkit.marriage2.commands;

import com.lenis0012.bukkit.marriage2.*;
import com.lenis0012.bukkit.marriage2.config.Message;
import com.lenis0012.bukkit.marriage2.config.Settings;
import com.lenis0012.bukkit.marriage2.internal.MarriageCore;
import org.bukkit.ChatColor;

import java.util.Locale;
import java.util.stream.Collectors;

public class CommandGender extends Command {

    public CommandGender(MarriageCore marriage) {
        super(marriage, "gender");
        setDescription(Message.COMMAND_GENDER.toString());
        setUsage("[gender]");
        setMinArgs(0);
    }

    @Override
    public void execute() {
        MPlayer mPlayer = marriage.getMPlayer(player);
        if (getArgLength() == 0) {
            String gender = mPlayer.getChosenGender().map(PlayerGender::getIdentifier).orElse("unknown");
            reply(Message.CURRENT_GENDER, gender, Genders.getOptions().stream().map(PlayerGender::getIdentifier).collect(Collectors.joining(", ")));
            return;
        }

        PlayerGender gender = Genders.getGender(getArg(0).toLowerCase(Locale.ROOT));
        if (gender == null) {
            reply(Message.UNKNOWN_GENDER, Genders.getOptions().stream().map(PlayerGender::getIdentifier).collect(Collectors.joining(", ")));
            return;
        }

        if(mPlayer.getChosenGender().isPresent() && !Settings.ALLOW_GENDER_CHANGE.value()) {
            reply(Message.GENDER_ALREADY_CHANGED);
        }

        mPlayer.setChosenGender(gender);
        reply(Message.GENDER_SET, formatIcons(gender.getDisplayName()));
    }

    private String formatIcons(String text) {
        text = ChatColor.translateAlternateColorCodes('&', text);
        return text.replace("{heart}", "\u2764")
                .replace("{icon:heart}", "\u2764")
                .replace("{icon:male}", "\u2642")
                .replace("{icon:female}", "\u2640")
                .replace("{icon:genderless}", "\u26B2");
    }
}
