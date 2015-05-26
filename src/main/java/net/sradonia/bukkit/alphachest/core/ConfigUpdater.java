package net.sradonia.bukkit.alphachest.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.sradonia.bukkit.alphachest.AlphaChest;

public class ConfigUpdater {

    private final AlphaChest plugin;

    /**
     * The current latest config version.
     */
    private final int CONFIG_VERSION = 2;

    public ConfigUpdater(AlphaChest plugin) {
        this.plugin = plugin;
    }

    /**
     * Returns the version of the current user's config version.
     *
     * @return the current version of the user's config
     */
    public int getConfigVersion() {
        return plugin.getConfig().getInt("config-version", 1);
    }

    /**
     * Returns the current, most up-to-date config version.
     *
     * @return the current latest config version
     */
    public int getLatestConfigVersion() {
        return CONFIG_VERSION;
    }

    /**
     * Checks the config.yml and updates it if it's outdated.
     */
    public void updateConfig() {
        switch (getConfigVersion()) {
            case 1:
                plugin.getLogger().info("[Config] An update to the config.yml was found.");
                updateToVersion2();
                break;
            default:
                plugin.getLogger().info("[Config] Update not required. The config.yml is already up-to-date.");
                break;
        }
    }

    /**
     * Updates the config.yml to version 2, which is required for AlphaChest 2.0.0 to work properly.
     */
    private void updateToVersion2() {
        plugin.getLogger().info("[Config] Updating the config.yml...");

        // Rename the old config and keep it as a backup
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        File oldConfigFile = new File(plugin.getDataFolder(), "config_old.yml");

        configFile.renameTo(oldConfigFile);

        // Create a new, fresh config and apply the old settings
        plugin.saveDefaultConfig();

        plugin.getLogger().info("[Config] Update of the config.yml complete.");
    }
}
