package CarrotPlugin.commands;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CarrotCommandTab implements TabCompleter {

    Permission perms;

    public CarrotCommandTab(Permission perm) {
        perms = perm;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String CommandLabel, String[] args) {

        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.add("scoreboard");
            list.add("inspect");
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (perms.has(player, "carrotplugin.sellall")) {
                    list.add("sellall");
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("scoreboard")) {
                list.add("1");
            } else if (args[0].equalsIgnoreCase("inspect")) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        list.add(player.getName());
                    }
                });
            }
        }
        return list;
    }
}
