package CarrotPlugin.donor;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class guiCommand implements CommandExecutor {
    GUI gui;
    Map<String, UserState> donors;
    Permission perms;

    public guiCommand(GUI gui, Map<String, UserState> donors, Permission perms) {
        this.gui = gui;
        this.donors = donors;
        this.perms = perms;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!perms.has(player, "carrotcraft.donor")) return false;
            String name = player.getName();
            if (donors.containsKey(name)) {
                player.openInventory(gui.openDonorGUI(name));
                return true;
            }

        }
        return false;
    }
}
