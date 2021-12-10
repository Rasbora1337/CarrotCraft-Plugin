package CarrotPlugin.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DonateCommand implements CommandExecutor {
    Component carrotcraft = Component.text("[", TextColor.fromHexString("#FFFF55"), TextDecoration.BOLD).append
            (Component.text("CarrotCraft", TextColor.fromHexString("#FFAA00"))).append
            (Component.text("] ", TextColor.fromHexString("#FFFF55")));


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(TextComponent.ofChildren(carrotcraft,
                    Component.text("Click here to donate!", TextColor.fromHexString("#01ea7a"), TextDecoration.BOLD)
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL,
                                    "https://carrotcraft.club/shop")).hoverEvent(Component.text("Click here to donate!"))));
        }

        return true;
    }
}
