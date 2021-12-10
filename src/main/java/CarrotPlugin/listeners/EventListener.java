package CarrotPlugin.listeners;

import CarrotPlugin.Counter;
import CarrotPlugin.discord.DiscordBot;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public class EventListener implements Listener {
    Counter counter;
    Plugin plugin;
    Permission perms;
    int[] carrotRanks = new int[]{10000, 50000, 100000, 250000, 500000};
    DiscordBot discordBot;
    Map<String, String> users;
    Logger log;
    Component carrotcraft = Component.text("[", TextColor.fromHexString("#FFFF55"), TextDecoration.BOLD).append
            (Component.text("CarrotCraft", TextColor.fromHexString("#FFAA00"))).append
            (Component.text("] ", TextColor.fromHexString("#FFFF55")));

    public EventListener(Counter initCounter, Plugin carrotPlugin, Permission perm,
                         DiscordBot discordBot, Map<String, String> users, Logger log) {
        counter = initCounter;
        plugin = carrotPlugin;
        perms = perm;
        this.discordBot = discordBot;
        this.users = users;
        this.log = log;
    }

    public static boolean contains(final int[] arr, final int key) {
        return Arrays.stream(arr).anyMatch(i -> i == key);
    }

    TextComponent rankUpMsg(String name, String amount, TextComponent rank) {
        return TextComponent.ofChildren(carrotcraft,
                Component.text(name, TextColor.fromHexString("#13df01"), TextDecoration.BOLD),
                Component.text(" has just harvested their ", TextColor.fromHexString("#55FFFF")),
                Component.text(amount, TextColor.fromHexString("#ffaa00"), TextDecoration.BOLD),
                Component.text(" carrot and was promoted to ", TextColor.fromHexString("#55FFFF")),
                rank);
    }

    TextComponent promotionMsg(TextComponent rank) {
        return TextComponent.ofChildren(carrotcraft,
                Component.text("You have been promoted to ", TextColor.fromHexString("#55FFFF")),
                rank);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (event.getBlock().getBlockData().getMaterial() == Material.CARROTS) {
            Ageable carrots = (Ageable) event.getBlock().getBlockData();
            if (carrots.getAge() == 7) {

                Player p = event.getPlayer();
                String username = p.getName();

                int carrotCount = counter.increment(username);

                if (carrotCount % 1000 == 0) {
                    String formatted = NumberFormat.getNumberInstance(Locale.US).format(carrotCount);
                    if (!contains(carrotRanks, carrotCount) && carrotCount % 5000 != 0) {
                        p.sendMessage(TextComponent.ofChildren(carrotcraft,
                                Component.text("Congratulations! ", TextColor.fromHexString("#13df01"), TextDecoration.BOLD),
                                Component.text("You have now harvested ", TextColor.fromHexString("#55FFFF")),
                                Component.text(formatted, TextColor.fromHexString("#ffaa00"), TextDecoration.BOLD),
                                Component.text(" carrots!", TextColor.fromHexString("#55FFFF"))));
                    }


                    if (carrotCount % 5000 == 0) {
                        if (!contains(carrotRanks, carrotCount)) {
                            Bukkit.broadcast(TextComponent.ofChildren(carrotcraft,
                                    Component.text(username, TextColor.fromHexString("#13df01"), TextDecoration.BOLD),
                                    Component.text(" has just harvested their ", TextColor.fromHexString("#55FFFF")),
                                    Component.text(formatted + "th", TextColor.fromHexString("#ffaa00"), TextDecoration.BOLD),
                                    Component.text(" carrot!", TextColor.fromHexString("#55FFFF"))));
                        }

                        if (carrotCount % 50000 == 0 && !contains(carrotRanks, carrotCount)) {
                            discordBot.makeCarrotAnnouncement(username, formatted, null);
                        }


                        if (carrotCount == 10000) {
                            perms.playerAdd(p, "carrotplugin.sellall");
                            String discordID = users.get(username);
                            if (discordID != null) discordBot.addRole(discordID, "Carrot");
                            discordBot.makeCarrotAnnouncement(username, formatted, "Carrot");
                            String primaryGroup = perms.getPrimaryGroup(p);
                            if (primaryGroup.equals("Member")) {
                                if (perms.playerAddGroup(p, "Carrot")) {
                                    p.sendMessage(promotionMsg(Component.text("Carrot", TextColor.fromHexString("#FFFF55"), TextDecoration.BOLD)));
                                } else {
                                    p.sendMessage("Error adding Carrot Rank! Contact staff if you see this message!");
                                }
                            }
                            Bukkit.broadcast(rankUpMsg(username, "10,000th",
                                    Component.text("Carrot", TextColor.fromHexString("#FFFF55"), TextDecoration.BOLD)));
                        } else if (carrotCount == 50000) {
                            perms.playerAdd(p, "carrotplugin.sellall");
                            perms.playerAdd(p, "essentials.sethome.multiple.carrotplus");
                            perms.playerAdd(p, "factions.fly");
                            String discordID = users.get(username);
                            if (discordID != null) discordBot.addRole(discordID, "CarrotPlus");
                            discordBot.makeCarrotAnnouncement(username, formatted, "Carrot+");
                            String primaryGroup = perms.getPrimaryGroup(p);
                            if (primaryGroup.equals("Carrot")) {
                                if (perms.playerRemoveGroup(p, "Carrot") && perms.playerAddGroup(p, "CarrotPlus")) {
                                    p.sendMessage(promotionMsg(Component.text("Carrot+", TextColor.fromHexString("#FFFF55"), TextDecoration.BOLD)));
                                } else {
                                    p.sendMessage("Error adding Carrot+ Rank! Contact staff if you see this message!");
                                }
                            }
                            Bukkit.broadcast(rankUpMsg(username, "50,000th",
                                    Component.text("Carrot+", TextColor.fromHexString("#FFFF55"), TextDecoration.BOLD)));
                        } else if (carrotCount == 100000) {
                            perms.playerAdd(p, "carrotplugin.sellall");
                            perms.playerAdd(p, "factions.fly");
                            perms.playerAdd(p, "essentials.sethome.multiple.default");
                            String discordID = users.get(username);
                            if (discordID != null) discordBot.addRole(discordID, "CarrotPlusPlus");
                            discordBot.makeCarrotAnnouncement(username, formatted, "Carrot++");
                            String primaryGroup = perms.getPrimaryGroup(p);
                            if (primaryGroup.equals("CarrotPlus")) {
                                if (perms.playerRemoveGroup(p, "CarrotPlus") && perms.playerAddGroup(p, "CarrotPlusPlus")) {
                                    p.sendMessage(promotionMsg(Component.text("Carrot++", TextColor.fromHexString("#FFFF55"), TextDecoration.BOLD)));
                                } else {
                                    p.sendMessage("Error adding Carrot++ Rank! Contact staff if you see this message!");
                                }
                            }

                            Bukkit.broadcast(rankUpMsg(username, "100,000th",
                                    Component.text("Carrot++", TextColor.fromHexString("#FFFF55"), TextDecoration.BOLD)));
                        } else if (carrotCount == 250000) {
                            perms.playerAdd(p, "carrotplugin.sellall");
                            perms.playerAdd(p, "factions.fly");
                            perms.playerAdd(p, "essentials.sethome.multiple.default");
                            perms.playerAdd(p, "essentials.workbench");
                            String discordID = users.get(username);
                            if (discordID != null) discordBot.addRole(discordID, "Gardener");
                            discordBot.makeCarrotAnnouncement(username, formatted, "Gardener");
                            String primaryGroup = perms.getPrimaryGroup(p);
                            if (primaryGroup.equals("CarrotPlusPlus")) {
                                if (perms.playerRemoveGroup(p, "CarrotPlusPlus") && perms.playerAddGroup(p, "Gardener")) {
                                    p.sendMessage(promotionMsg(Component.text("Gardener", TextColor.fromHexString("#55FF55"), TextDecoration.BOLD)));
                                } else {
                                    p.sendMessage("Error adding Gardener Rank! Contact staff if you see this message!");
                                }
                            }
                            Bukkit.broadcast(rankUpMsg(username, "250,000th",
                                    Component.text("Gardener", TextColor.fromHexString("#55FF55"), TextDecoration.BOLD)));
                        } else if (carrotCount == 500000) {
                            perms.playerAdd(p, "carrotplugin.sellall");
                            perms.playerAdd(p, "factions.fly");
                            perms.playerAdd(p, "essentials.sethome.multiple.default");
                            perms.playerAdd(p, "essentials.workbench");
                            perms.playerAdd(p, "essentials.enderchest");
                            perms.playerAdd(p, "essentials.keepxp");
                            String discordID = users.get(username);
                            if (discordID != null) discordBot.addRole(discordID, "Harvester");
                            discordBot.makeCarrotAnnouncement(username, formatted, "Harvester");
                            String primaryGroup = perms.getPrimaryGroup(p);
                            if (primaryGroup.equals("Gardener")) {
                                if (perms.playerRemoveGroup(p, "Gardener") && perms.playerAddGroup(p, "Harvester")) {
                                    p.sendMessage(promotionMsg(Component.text("Harvester", TextColor.fromHexString("#00AA00"), TextDecoration.BOLD)));
                                } else {
                                    p.sendMessage("Error adding Harvester Rank! Contact staff if you see this message!");
                                }
                            }
                            Bukkit.broadcast(rankUpMsg(username, "500,000th",
                                    Component.text("Harvester", TextColor.fromHexString("#00AA00"), TextDecoration.BOLD)));
                        }

                    }


                }
            }
        }
    }
}
