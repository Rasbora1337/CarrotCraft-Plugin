package CarrotPlugin.commands;

import CarrotPlugin.DB.DB;
import CarrotPlugin.DB.DB_Callback;
import CarrotPlugin.discord.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class VerifyDiscordCommand implements CommandExecutor {

    Component carrotcraft = Component.text("[", TextColor.fromHexString("#FFFF55"), TextDecoration.BOLD).append
            (Component.text("CarrotCraft", TextColor.fromHexString("#FFAA00"))).append
            (Component.text("] ", TextColor.fromHexString("#FFFF55")));

    HashMap<String, String> discordLinks;
    Logger log;
    DB DB;
    Map<String, String> users;
    HashMap<String, CompletableFuture<Message>> tasks;
    DiscordBot discordBot;
    Permission perms;

    public VerifyDiscordCommand(Logger log, HashMap<String,
            String> discordLinks, DB DB,
                                Map<String, String> users,
                                HashMap<String, CompletableFuture<Message>> tasks,
                                DiscordBot discordBot,
                                Permission perms) {
        this.discordLinks = discordLinks;
        this.log = log;
        this.DB = DB;
        this.users = users;
        this.tasks = tasks;
        this.discordBot = discordBot;
        this.perms = perms;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String name = player.getName();
            if (users.containsKey(name)) return false;
            if (args.length == 0) {
                player.sendMessage(net.kyori.adventure.text.TextComponent.ofChildren(carrotcraft,
                        Component.text("Usage: /verifydiscord [token]", TextColor.fromHexString("#ff1a00"))));
            } else if (args.length == 1) {
                String token = args[0];
                if (token == null) return false;

                if (discordLinks.containsKey(token)) {
                    String discordID = discordLinks.get(token);
                    cancelTask(discordID);
                    discordLinks.remove(token);
                    log.info("Verified minecraft user " + name + " with discord ID " + discordID);
                    DB.insertUser(name, discordID, new DB_Callback() {
                        @Override
                        public void onQueryDone(boolean result) {
                            if (!result) {
                                log.severe("Failure inserting user on discord link! " + name + " - " + discordID);
                                player.sendMessage(net.kyori.adventure.text.TextComponent.ofChildren(carrotcraft,
                                        Component.text("Failure verifying! Report this message to staff!", TextColor.fromHexString("#ff1a00"))));
                            } else {
                                users.put(name, discordID);
                                player.sendMessage(net.kyori.adventure.text.TextComponent.ofChildren(carrotcraft,
                                        Component.text("Successfully linked discord account!", TextColor.fromHexString("#13df01"))));
                            }

                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setTitle("Verification Success", "https://carrotcraft.club/");
                            eb.setColor(new Color(0x13df01));
                            eb.setDescription("You have successfully linked your minecraft account **" + name + "** with this discord account!");
                            eb.setAuthor("CarrotBot", "https://carrotcraft.club/", "https://i.imgur.com/7G8iB7d.png");
                            discordBot.sendMessage(discordID, eb.build());

                            String primaryGroup = perms.getPrimaryGroup(player);
                            if (primaryGroup != null) discordBot.addRole(discordID, primaryGroup);
                        }
                    });

                    return true;
                } else {
                    player.sendMessage(net.kyori.adventure.text.TextComponent.ofChildren(carrotcraft,
                            Component.text("Invalid Token!", TextColor.fromHexString("#ff1a00"))));
                    return false;
                }


            }
        }

        return true;
    }

    public void cancelTask(String discordID) {
        if (discordID == null) return;

        CompletableFuture<Message> task = tasks.get(discordID);
        if (task == null) return;

        task.cancel(true);
        tasks.remove(discordID);
    }

}
