package CarrotPlugin.DB;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class LoadConfig {

    FileConfiguration customConfig;
    File customConfigFile;

    public LoadConfig(File pluginDir, Logger log) {
        customConfigFile = new File(pluginDir, "config.yml");
        if (!customConfigFile.exists()) {
            log.severe("No config file found!");
            return;
        }

        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            log.severe("Error loading config file!");
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return this.customConfig;
    }

}
