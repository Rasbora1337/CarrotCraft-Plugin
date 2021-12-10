package CarrotPlugin.commands;

import CarrotPlugin.Counter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class CarrotCommand implements CommandExecutor {
    Counter counter;
    Economy econ;
    String CarrotCraft = ChatColor.YELLOW + "" + ChatColor.BOLD + "[" + ChatColor.GOLD + "" + ChatColor.BOLD + "CarrotCraft" + ChatColor.YELLOW + "" + ChatColor.BOLD + "]" + ChatColor.RESET + " ";
    Integer usersPerPage = 10;
    Component carrotcraft = Component.text("[", TextColor.fromHexString("#FFFF55"), TextDecoration.BOLD).append
            (Component.text("CarrotCraft", TextColor.fromHexString("#FFAA00"))).append
            (Component.text("] ", TextColor.fromHexString("#FFFF55")));
    Permission perms;


    public CarrotCommand(Counter initCounter, Economy economy, Permission perm) {
        counter = initCounter;
        econ = economy;
        perms = perm;
    }

    private LinkedHashMap<String, Integer> sortByValue() {
        LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();

        counter.carrotCounts.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
        return reverseSortedMap;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            String name = player.getName();
            if (args.length == 0) {
                if (counter.carrotCounts.containsKey(name)) {
                    player.sendMessage(CarrotCraft + ChatColor.AQUA + "You have harvested " + ChatColor.GOLD + "" + ChatColor.BOLD + counter.getCount(player.getName()) + ChatColor.RESET + "" + ChatColor.AQUA + " carrots!");
                } else {
                    player.sendMessage(CarrotCraft + ChatColor.RED + "You have not harvested any carrots yet!");
                }
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("scoreboard")) {
                    String scoreboard = ChatColor.YELLOW + "" + ChatColor.BOLD + "[" + ChatColor.GOLD + "CARROT SCOREBOARD" + ChatColor.YELLOW + "" + ChatColor.BOLD + "]" + ChatColor.RESET + "\n";
                    String users = "";
                    Integer score = 1;
                    Integer total = 0;
                    Map<String, Integer> sorted = sortByValue();
                    Integer pageCount = (int) Math.ceil((float) sorted.size() / (usersPerPage - 1));
                    for (Map.Entry<String, Integer> en :
                            sorted.entrySet()) {
                        total += en.getValue();
                        if (score < usersPerPage)
                            users = users.concat(ChatColor.YELLOW + "" + score++ + ". " + ChatColor.GOLD + en.getKey() + ": " + ChatColor.YELLOW + NumberFormat.getNumberInstance(Locale.US).format(en.getValue()) + "\n");
                    }
                    scoreboard = scoreboard.concat(ChatColor.GOLD + "Server Total: " + ChatColor.YELLOW + NumberFormat.getNumberInstance(Locale.US).format(total) + "\n");
                    scoreboard = scoreboard.concat(users);
                    player.sendMessage(scoreboard);
                    if (pageCount > 1) {
                        TextComponent page = new TextComponent(ChatColor.YELLOW + "Page " + ChatColor.GOLD + "1 / " + pageCount + " ");
                        TextComponent nextPage = new TextComponent(ChatColor.YELLOW + "" + ChatColor.BOLD + "[" + ChatColor.GOLD + "" + ChatColor.BOLD + ">>" + ChatColor.YELLOW + "" + ChatColor.BOLD + "]");
                        nextPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/carrots scoreboard 2"));
                        nextPage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Page 2").create()));
                        page.addExtra(nextPage);
                        player.sendMessage(page);
                    }

                } else if (args[0].equalsIgnoreCase("sellall")) {
                    if (!perms.has(player, "carrotplugin.sellall")) {
                        player.sendMessage(net.kyori.adventure.text.TextComponent.ofChildren(carrotcraft,
                                Component.text("You must be at least Carrot rank to use this command!", TextColor.fromHexString("#ff1a00"))));
                        return false;
                    }
                    if (player.getInventory().contains(Material.CARROT)) {
                        int carrotsToSell = 0;
                        for (ItemStack a : player.getInventory().getStorageContents()) {
                            if (a == null) continue;
                            if (a.getType() == Material.CARROT) carrotsToSell += a.getAmount();
                        }
                        if (carrotsToSell == 0) {
                            player.sendMessage(net.kyori.adventure.text.TextComponent.ofChildren(carrotcraft,
                                    Component.text("No carrots to sell!", TextColor.fromHexString("#ff1a00"))));
                            System.out.println(name + " just did something really weird!");
                            return false;
                        }
                        EconomyResponse sellCarrots = econ.depositPlayer(player, carrotsToSell);
                        if (sellCarrots.type == EconomyResponse.ResponseType.SUCCESS) {
                            player.getInventory().remove(Material.CARROT);
                            player.sendMessage(net.kyori.adventure.text.TextComponent.ofChildren(carrotcraft,
                                    Component.text("Sold ", TextColor.fromHexString("#55FFFF")),
                                    Component.text(NumberFormat.getNumberInstance(Locale.US).format(carrotsToSell), TextColor.fromHexString("#ffaa00"), TextDecoration.BOLD),
                                    Component.text(" carrot" + (carrotsToSell > 1 ? "s" : "") + "! New balance: ", TextColor.fromHexString("#55FFFF")),
                                    Component.text("$" + NumberFormat.getNumberInstance(Locale.US).format((int) sellCarrots.balance), TextColor.fromHexString("#13df01"), TextDecoration.BOLD)));
                        } else {
                            System.out.println("ERROR selling carrots! " + name + " - " + sellCarrots.type + " " + sellCarrots.errorMessage);
                            player.sendMessage(net.kyori.adventure.text.TextComponent.ofChildren(carrotcraft,
                                    Component.text("Error selling carrots! Please contact staff if you see this message!", TextColor.fromHexString("#ff1a00"))));
                        }

                    } else {
                        player.sendMessage(net.kyori.adventure.text.TextComponent.ofChildren(carrotcraft,
                                Component.text("No carrots to sell!", TextColor.fromHexString("#ff1a00"))));
                    }
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("scoreboard")) {
                    Integer pageNum;
                    try {
                        pageNum = Integer.parseInt(args[1]);
                    } catch (Exception e) {
                        player.sendMessage(ChatColor.RED + "Invalid page number!");
                        return false;
                    }

                    Map<String, Integer> sorted = sortByValue();
                    Integer pageCount = (int) Math.ceil((float) sorted.size() / (usersPerPage - 1));

                    if (pageNum > pageCount || pageNum < 1) {
                        player.sendMessage(ChatColor.RED + "Invalid page number!");
                        return false;
                    }

                    String users = "";
                    Integer score = 1;
                    Integer start = (pageNum * usersPerPage) - usersPerPage;
                    Integer end = start + usersPerPage;

                    for (Map.Entry<String, Integer> en :
                            sorted.entrySet()) {
                        if (score >= start && score < end) {
                            users = users.concat(ChatColor.YELLOW + "" + score + ". " + ChatColor.GOLD + en.getKey() + ": " + ChatColor.YELLOW + NumberFormat.getNumberInstance(Locale.US).format(en.getValue()) + "\n");
                        }
                        score++;
                        if (score >= end) break;

                    }
                    player.sendMessage(users);
                    if (pageCount > 1) {
                        TextComponent page = new TextComponent(ChatColor.YELLOW + "Page " + ChatColor.GOLD + pageNum + " / " + pageCount + " ");

                        if (pageNum > 1) {
                            TextComponent prevPage = new TextComponent(ChatColor.YELLOW + "" + ChatColor.BOLD + "[" + ChatColor.GOLD + "" + ChatColor.BOLD + "<<" + ChatColor.YELLOW + "" + ChatColor.BOLD + "] ");
                            prevPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/carrots scoreboard " + (pageNum - 1)));
                            prevPage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Page " + (pageNum - 1)).create()));
                            page.addExtra(prevPage);
                        }

                        if (pageNum < pageCount) {
                            TextComponent nextPage = new TextComponent(ChatColor.YELLOW + "" + ChatColor.BOLD + "[" + ChatColor.GOLD + "" + ChatColor.BOLD + ">>" + ChatColor.YELLOW + "" + ChatColor.BOLD + "]");
                            nextPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/carrots scoreboard " + (pageNum + 1)));
                            nextPage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Page " + (pageNum + 1)).create()));
                            page.addExtra(nextPage);
                        }
                        player.sendMessage(page);
                    }

                } else if (args[0].equalsIgnoreCase("inspect")) {
                    String playerName = args[1];
                    if (playerName == null) {
                        player.sendMessage(net.kyori.adventure.text.TextComponent.ofChildren(carrotcraft,
                                Component.text("Player not found!", TextColor.fromHexString("#ff1a00"))));
                        return false;
                    }

                    String result = counter.getCount(playerName);
                    if (result != null) {
                        player.sendMessage(net.kyori.adventure.text.TextComponent.ofChildren(carrotcraft,
                                Component.text(playerName, TextColor.fromHexString("#FFAA00"), TextDecoration.BOLD),
                                Component.text(" has harvested ", TextColor.fromHexString("#FFFF55")),
                                Component.text(result, TextColor.fromHexString("#FFAA00"), TextDecoration.BOLD),
                                Component.text(" carrots!", TextColor.fromHexString("#FFFF55"))));
                        return true;
                    } else {
                        player.sendMessage(net.kyori.adventure.text.TextComponent.ofChildren(carrotcraft,
                                Component.text("Player not found!", TextColor.fromHexString("#ff1a00"))));
                        return false;
                    }

                }
            }
        }

        return true;
    }

}
