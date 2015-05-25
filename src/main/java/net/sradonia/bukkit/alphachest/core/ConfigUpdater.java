package net.sradonia.bukkit.alphachest.core;

import java.io.File;

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
                plugin.getLogger().info("[ConfigUpdater] The config.yml was detected as being outdated! Attempting to update...");
                updateToVersion2();
                break;
            default:
                plugin.getLogger().info("[ConfigUpdater] Update not required. The config.yml is already up-to-date.");
                break;
        }
    }

    /**
     * Updates the config.yml to version 2, which is required for AlphaChest 2.0.0 to work properly.
     */
    private void updateToVersion2() {
        plugin.getLogger().info("[ConfigUpdater] Updating config.yml to latest version...");

        // Get the user's current configuration settings from the old config
        int autosaveInterval = plugin.getConfig().getInt("autosave", 10);
        boolean silentAutosave = plugin.getConfig().getBoolean("silentAutosave", false);

        boolean clearOnDeath = plugin.getConfig().getBoolean("clearOnDeath", false);
        boolean dropOnDeath = plugin.getConfig().getBoolean("dropOnDeath", false);

        // Rename the old config and keep it as a backup
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        File oldConfigFile = new File(plugin.getDataFolder(), "config_old.yml");

        configFile.renameTo(oldConfigFile);

        // Create a new, fresh config and apply the old settings
        plugin.saveDefaultConfig();

        if (autosaveInterval <= 0) {
            // Auto-save disabling used to be identified by an interval set to 0
            plugin.getConfig().set("auto-save.enabled", false);
        }

        plugin.getConfig().set("auto-save.interval", autosaveInterval);
        plugin.getConfig().set("auto-save.silent", silentAutosave);

        plugin.getConfig().set("death.clear", clearOnDeath);
        plugin.getConfig().set("death.drop", dropOnDeath);

        // Save the config
        plugin.saveConfig();

        plugin.getLogger().info("[ConfigUpdater] Update complete. The config.yml was successfully updated to the latest version.");
    }
}
