package CarrotPlugin.listeners;

import CarrotPlugin.Counter;
import CarrotPlugin.TopHarvesters;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class CitizensEnable implements Listener {

    Counter counter;
    Plugin plugin;
    Logger log;

    public CitizensEnable(Counter counter, Plugin plugin, Logger log) {
        this.counter = counter;
        this.plugin = plugin;
        this.log = log;
    }

    @EventHandler
    public void onCitizensEnableEvent(CitizensEnableEvent event) {
        new TopHarvesters(counter, Bukkit.getServer().getWorld("world"), plugin);
    }
}
