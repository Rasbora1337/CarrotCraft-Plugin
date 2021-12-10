package CarrotPlugin.API;

import CarrotPlugin.DB.DB;
import CarrotPlugin.discord.DiscordBot;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;

public class API {
    HttpServer server;
    Set<String> donorNames;
    DB DB;
    Plugin plugin;
    Permission perms;
    DiscordBot discordBot;
    Map<String, String> users;

    Component carrotcraft = Component.text("[", TextColor.fromHexString("#FFFF55"), TextDecoration.BOLD).append
            (Component.text("CarrotCraft", TextColor.fromHexString("#FFAA00"))).append
            (Component.text("] ", TextColor.fromHexString("#FFFF55")));

    String[] ranksArr = new String[]{"Donor5", "Donor10", "Donor25", "Donor50", "Donor75", "Donor100"};

    public API(Set<String> donorNames, DB DB, Plugin plugin, Permission perms, DiscordBot discordBot,
               Map<String, String> users) throws IOException {
        this.donorNames = donorNames;
        this.DB = DB;
        this.plugin = plugin;
        this.perms = perms;
        this.discordBot = discordBot;
        this.users = users;
        server = HttpServer.create(new InetSocketAddress(18512), 0);
        server.createContext("/carrot/newDonor", new MyHttpHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    private class MyHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {

            Headers requestHeaders = he.getRequestHeaders();
            boolean auth = false;
            for (Map.Entry<String, List<String>> entry : requestHeaders.entrySet()) {
                if (entry.getKey().equalsIgnoreCase("carrot-auth")) {
                    if (entry.getValue().get(0).contains("b0o945932wk0v4m5i54io4er30er0o")) {
                        auth = true;
                        break;
                    }
                }
            }
            OutputStream outputStream = he.getResponseBody();

            if (!auth) {
                he.sendResponseHeaders(500, 0);
                outputStream.close();
                return;
            }

            if (he.getRequestMethod().equals("POST")) {
                InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String query = br.readLine();
                JSONParser parser = new JSONParser();
                JSONObject json;
                try {
                    json = (JSONObject) parser.parse(query);
                } catch (ParseException e) {
                    System.out.println("Failed JSON parsing!");
                    e.printStackTrace();
                    he.sendResponseHeaders(500, 0);
                    outputStream.close();
                    return;
                }

                String donorName = (String) json.get("name");
                String rank = (String) json.get("rank");
                if (donorName == null || rank == null) {
                    System.out.println("Failed parsing donor / rank name!");
                    he.sendResponseHeaders(500, 0);
                    outputStream.close();
                    return;
                }

                List<String> ranksList = new ArrayList<>(Arrays.asList(ranksArr));
                if (!ranksList.contains(rank)) {
                    System.out.println("Invalid Rank!");
                    he.sendResponseHeaders(500, 0);
                    outputStream.close();
                    return;
                }

                donorNames.add(donorName);

                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Player p = Bukkit.getPlayerExact(donorName);
                        if (p != null) {
                            perms.playerAdd(p, "carrotcraft.donor");

                            String primaryGroup = perms.getPrimaryGroup(p);
                            if (primaryGroup.equalsIgnoreCase("Carrot")) {
                                perms.playerAdd(p, "carrotplugin.sellall");
                            } else if (primaryGroup.equalsIgnoreCase("CarrotPlus")) {
                                perms.playerAdd(p, "carrotplugin.sellall");
                                perms.playerAdd(p, "factions.fly");
                                perms.playerAdd(p, "essentials.sethome.multiple.carrotplus");
                            } else if (primaryGroup.equalsIgnoreCase("CarrotPlusPlus")) {
                                perms.playerAdd(p, "carrotplugin.sellall");
                                perms.playerAdd(p, "factions.fly");
                                perms.playerAdd(p, "essentials.sethome.multiple.default");
                            } else if (primaryGroup.equalsIgnoreCase("Gardener")) {
                                perms.playerAdd(p, "carrotplugin.sellall");
                                perms.playerAdd(p, "factions.fly");
                                perms.playerAdd(p, "essentials.sethome.multiple.default");
                                perms.playerAdd(p, "essentials.workbench");
                            } else if (primaryGroup.equalsIgnoreCase("Harvester")) {
                                perms.playerAdd(p, "carrotplugin.sellall");
                                perms.playerAdd(p, "factions.fly");
                                perms.playerAdd(p, "essentials.sethome.multiple.default");
                                perms.playerAdd(p, "essentials.workbench");
                                perms.playerAdd(p, "essentials.enderchest");
                                perms.playerAdd(p, "essentials.keepxp");
                            }

                            perms.playerRemoveGroup(p, primaryGroup);
                            perms.playerAddGroup(p, rank);

                            DB.loadDonor(p, donorName);
                            p.sendMessage(TextComponent.ofChildren(carrotcraft,
                                    Component.text("Thanks for donating, ", TextColor.fromHexString("#00b2f6")),
                                    Component.text(donorName + "!", TextColor.fromHexString("#00f636"), TextDecoration.BOLD)));
                        } else {
                            OfflinePlayer offlineP = Bukkit.getOfflinePlayer(donorName);
                            perms.playerAdd("world", offlineP, "carrotcraft.donor");

                            String primaryGroup = perms.getPrimaryGroup("world", offlineP);
                            if (primaryGroup.equalsIgnoreCase("Carrot")) {
                                perms.playerAdd("world", offlineP, "carrotplugin.sellall");
                            } else if (primaryGroup.equalsIgnoreCase("CarrotPlus")) {
                                perms.playerAdd("world", offlineP, "carrotplugin.sellall");
                                perms.playerAdd("world", offlineP, "factions.fly");
                                perms.playerAdd("world", offlineP, "essentials.sethome.multiple.carrotplus");
                            } else if (primaryGroup.equalsIgnoreCase("CarrotPlusPlus")) {
                                perms.playerAdd("world", offlineP, "carrotplugin.sellall");
                                perms.playerAdd("world", offlineP, "factions.fly");
                                perms.playerAdd("world", offlineP, "essentials.sethome.multiple.default");
                            } else if (primaryGroup.equalsIgnoreCase("Gardener")) {
                                perms.playerAdd("world", offlineP, "carrotplugin.sellall");
                                perms.playerAdd("world", offlineP, "factions.fly");
                                perms.playerAdd("world", offlineP, "essentials.sethome.multiple.default");
                                perms.playerAdd("world", offlineP, "essentials.workbench");
                            } else if (primaryGroup.equalsIgnoreCase("Harvester")) {
                                perms.playerAdd("world", offlineP, "carrotplugin.sellall");
                                perms.playerAdd("world", offlineP, "factions.fly");
                                perms.playerAdd("world", offlineP, "essentials.sethome.multiple.default");
                                perms.playerAdd("world", offlineP, "essentials.workbench");
                                perms.playerAdd("world", offlineP, "essentials.enderchest");
                                perms.playerAdd("world", offlineP, "essentials.keepxp");
                            }

                            perms.playerRemoveGroup("world", offlineP, primaryGroup);
                            perms.playerAddGroup("world", offlineP, rank);
                        }
                        Bukkit.broadcast(TextComponent.ofChildren(carrotcraft,
                                Component.text(donorName, TextColor.fromHexString("#00f636"), TextDecoration.BOLD),
                                Component.text(" just donated!", TextColor.fromHexString("#00b2f6"))));
                        System.out.println("PROCESSED PLAYER DONATION: " + donorName + " - " + rank);
                    }
                });

                String discordID = users.get(donorName);
                if (discordID != null) discordBot.addRole(discordID, rank);

            }
            String response = "success";
            he.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            outputStream.close();
        }
    }
}
