package CarrotPlugin;

import CarrotPlugin.API.API;
import CarrotPlugin.DB.DB;
import CarrotPlugin.DB.LoadConfig;
import CarrotPlugin.commands.*;
import CarrotPlugin.discord.DiscordBot;
import CarrotPlugin.donor.GUI;
import CarrotPlugin.donor.Listener;
import CarrotPlugin.donor.UserState;
import CarrotPlugin.donor.guiCommand;
import CarrotPlugin.listeners.CitizensEnable;
import CarrotPlugin.listeners.EventListener;
import com.google.gson.Gson;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class CarrotPlugin extends JavaPlugin {
    Counter counter;
    DB DB;
    private static Economy econ = null;
    private static Permission perms = null;
    DiscordBot discordBot;
    int playerCountCache = 0;
    private static final Logger log = Logger.getLogger("Minecraft");
    HashMap<String, CompletableFuture<Message>> tasks = new HashMap<>();
    HashMap<String, String> discordLinks = new HashMap<>();
    Map<String, String> users = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    FileConfiguration config;
    Scoreboard scoreboard;
    GUI donorGUI;
    Map<String, UserState> donors = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    Set<String> donorNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    API API;

    private void makeDir() {
        try {
            File dirCheck = new File(this.getDataFolder().toString());
            if (!dirCheck.exists()) {
                dirCheck.mkdir();
                System.out.println("Folder created: " + this.getDataFolder());
            }
        } catch (Exception e) {
            System.out.println("An error occurred making plugin folder!");
            e.printStackTrace();
        }
    }

    private void saveStats() {
        Gson gson = new Gson();
        String json = gson.toJson(counter.carrotCounts);
        makeDir();
        try (PrintWriter out = new PrintWriter(this.getDataFolder() + "/counter.json")) {
            out.println(json);
        } catch (Exception e) {
            System.out.println("ERROR Saving carrot counter!");
            System.out.println(e);
        }
        DB.updateStats(counter.carrotCounts);
    }


    private void saveCounter() {
        this.getServer().getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
                    public void run() {
                        saveStats();
                        saveCounter();
                    }
                }
                , 6000);
    }

    private void updatePlayerCount() {
        this.getServer().getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
                    public void run() {
                        int currentCount = playerCount();
                        if (currentCount != playerCountCache) {
                            try {
                                discordBot.getPresence().setActivity(Activity.watching(currentCount + " / 420 Carrots Playing"));
                            } catch (Exception e) {
                                log.severe("Exception updating activity!");
                                e.printStackTrace();
                            }
                            playerCountCache = currentCount;
                        }
                        updatePlayerCount();
                    }
                }
                , 60);
    }

    private int playerCount() {
        return this.getServer().getOnlinePlayers().size();
    }

    public void initializeDiscordCommands() {
        getCommand("verifydiscord").setExecutor(new VerifyDiscordCommand(log, discordLinks, DB, users, tasks, discordBot, perms));
        getCommand("verifydiscord").setTabCompleter(new VerifyDiscordCommandTab());
    }

    @Override
    public void onEnable() {
        scoreboard = getServer().getScoreboardManager().getMainScoreboard();
        DB = new DB(this.getDataFolder(), this, log, donors, scoreboard, donorNames);
        makeDir();

        if (!setupPermissions()) {
            log.severe("ERROR: Could not setup vault permissions!!!!");
        }

        if (!setupEconomy()) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        counter = new Counter(this.getDataFolder(), DB);
        getServer().getPluginManager().registerEvents(new CitizensEnable(counter, this, log), this);
        users = DB.loadUsers();
        donorGUI = new GUI(scoreboard, donors, perms);
        getServer().getPluginManager().registerEvents(new Listener(scoreboard, this, donorGUI, donors, perms, donorNames, DB), this);
        getCommand("gui").setExecutor(new guiCommand(donorGUI, donors, perms));
        getCommand("carrots").setExecutor(new CarrotCommand(counter, econ, perms));
        getCommand("carrots").setTabCompleter(new CarrotCommandTab(perms));
        getCommand("discord").setExecutor(new DiscordCommand());

        config = new LoadConfig(this.getDataFolder(), log).getConfig();
        if (config == null) {
            log.severe("Error loading YAML config!");
        } else {
            Plugin plugin = this;
            this.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                public void run() {
                    discordBot = new DiscordBot(log, discordLinks, users, config, tasks, plugin);
                    getServer().getPluginManager().registerEvents(new EventListener(counter, plugin, perms, discordBot, users, log), plugin);
                    initializeDiscordCommands();
                    updatePlayerCount();

                    try {
                        API = new API(donorNames, DB, plugin, perms, discordBot, users);
                    } catch (IOException e) {
                        log.severe("ERROR Initializing HTTP API!");
                        e.printStackTrace();
                    }
                }
            });
        }

        getCommand("donate").setExecutor(new DonateCommand());

        saveCounter();
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }


    @Override
    public void onDisable() {
        saveStats();
        donorGUI.deRegisterTeams(scoreboard);

        NamespacedKey persistentKey = new NamespacedKey(this, "spawnedItem");
        for (World world : Bukkit.getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getPersistentDataContainer().has(persistentKey, PersistentDataType.BYTE)) {
                    entity.remove();
                }
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            UserState state = donors.get(p.getName());
            if (state != null) {
                DB.saveDonorState(p.getName(), state);
            }
        }
        DB.onClose();
        API.stop();
        discordBot.shutdown();
    }
}
