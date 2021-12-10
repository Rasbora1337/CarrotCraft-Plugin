package CarrotPlugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class VerifyDiscordCommandTab implements TabCompleter {


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String CommandLabel, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            list.add("[token]");
        }
        return list;
    }
}
