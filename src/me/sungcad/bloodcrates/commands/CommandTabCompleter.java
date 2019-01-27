package me.sungcad.bloodcrates.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

public class CommandTabCompleter implements TabCompleter {
    private static List<String> arg1 = Arrays.asList("config", "cooldowns", "help", "items", "locations", "reload");

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        final List<String> list = new ArrayList<String>();
        if (args.length == 0 || (args.length == 1 && !CommandTabCompleter.arg1.contains(args[0]))) {
            StringUtil.copyPartialMatches(args[0], arg1, list);
        }
        return list;
    }
}