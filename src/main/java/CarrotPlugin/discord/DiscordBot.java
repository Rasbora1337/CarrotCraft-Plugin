package CarrotPlugin.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class DiscordBot {
    JDA discordBot;
    Logger log;
    String guildID;
    Plugin plugin;
    String carrotAnnouncements;

    String[] carrotRanks = new String[]{"Member", "Carrot", "CarrotPlus", "CarrotPlusPlus", "Gardener", "Harvester", "Donor5", "Donor10", "Donor25", "Donor50", "Donor75", "Donor100"};
    HashMap<String, String> mcToDiscord = new HashMap<>();

    public DiscordBot(Logger log, HashMap<String, String> discordLinks,
                      Map<String, String> users, FileConfiguration config,
                      HashMap<String, CompletableFuture<Message>> tasks,
                      Plugin plugin) {

        String botKey = config.getString("discord-bot-key");
        if (botKey == null) {
            log.severe("Invalid key provided to discord bot!");
            return;
        }

        guildID = config.getString("discordserver-id");
        if (guildID == null) {
            log.severe("Invalid guildID provided to discord bot!");
            return;
        }

        carrotAnnouncements = config.getString("carrotannouncements-channel");
        if (carrotAnnouncements == null) {
            log.severe("Invalid carrotAnnouncements ID provided to discord bot!");
        }

        for (String rank : carrotRanks) {
            String discordRoleID = config.getString("roleid-" + rank.toLowerCase());
            if (discordRoleID != null) {
                mcToDiscord.put(rank, discordRoleID);
            } else {
                log.severe("Could not find discord role ID for minecraft rank " + rank);
            }
        }

        carrotRanks = null; //free like 50 bytes of RAM

        this.plugin = plugin;

        try {
            discordBot = JDABuilder.createDefault(botKey)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .addEventListeners(new DiscordBotListener(log, discordLinks, users, tasks))
                    .setActivity(Activity.listening("0 / 420 Carrots Playing"))
                    .build();
            discordBot.awaitReady();
        } catch (LoginException | InterruptedException e) {
            log.severe("DISCORD LOGIN EXCEPTION!");
            e.printStackTrace();
        }
        this.log = log;
    }

    public void shutdown() {
        discordBot.shutdownNow();
    }

    public Presence getPresence() {
        return discordBot.getPresence();
    }

    public void sendMessage(String ID, MessageEmbed embed) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Guild guild = discordBot.getGuildById(guildID);
                if (guild == null) {
                    log.severe("Could not retrieve guild in sendMessage!");
                    return;
                }
                try {
                    guild.retrieveMemberById(ID)
                            .queue((memberRest) -> memberRest.getUser().openPrivateChannel()
                                    .queue((privateChannel -> privateChannel.sendMessage(embed)
                                            .queue())));
                } catch (Exception e) {
                    log.severe("ERROR sending message to discord user! " + e);
                    e.printStackTrace();
                    return;
                }
            }
        });
    }

    public void makeCarrotAnnouncement(String username, String carrots, String rank) {
        if (carrotAnnouncements == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Guild guild = discordBot.getGuildById(guildID);
                if (guild == null) {
                    log.severe("Could not retrieve guild in makeCarrotAnnouncement!");
                    return;
                }
                try {
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Carrot Alerts", "https://carrotcraft.club/");
                    eb.setColor(new Color(0xFFAA00));
                    eb.setDescription("**" + username + "** has just harvested their **" + carrots + "th** carrot!");
                    eb.setAuthor("CarrotBot", "https://carrotcraft.club/", "https://i.imgur.com/7G8iB7d.png");
                    eb.setThumbnail("https://minotar.net/armor/body/" + username);
                    if (rank == null) {
                        eb.setDescription("**" + username + "** has just harvested their **" + carrots + "th** carrot!");
                    } else {
                        eb.setDescription("**" + username + "** has just harvested their **" + carrots + "th** carrot and was promoted to **" + rank + "**!");
                    }
                    guild.getTextChannelById(carrotAnnouncements).sendMessage(eb.build()).queue();

                } catch (Exception e) {
                    log.severe("ERROR sending message to carrot channel! " + e);
                    e.printStackTrace();
                    return;
                }
            }
        });
    }

    public void addRole(String discordID, String carrotRank) {

        String discordRoleID = mcToDiscord.get(carrotRank);
        if (discordRoleID == null) {
            log.severe("Could not find discord role ID for rank: " + carrotRank);
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Guild guild = discordBot.getGuildById(guildID);
                if (guild == null) {
                    log.severe("Could not retrieve guild in addRole!");
                    return;
                }

                Role role = guild.getRoleById(discordRoleID);
                if (role == null) {
                    log.severe("Could not retrieve role in addRole! " + discordID + " - " + discordRoleID);
                    return;
                }

                try {
                    guild.addRoleToMember(discordID, role).queue();
                } catch (Exception e) {
                    log.severe("ERROR processing role update " + e);
                    e.printStackTrace();
                    return;
                }
            }
        });
    }

}
