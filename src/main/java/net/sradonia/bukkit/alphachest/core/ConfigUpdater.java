package net.sradonia.bukkit.alphachest.core;

import java.io.File;

import net.sradonia.bukkit.alphachest.AlphaChest;

public class ConfigUpdater {

    private final AlphaChest plugin;

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
     * Checks the config.yml and updates it if it's outdated.
     */
    public void updateConfig() {
        switch (getConfigVersion()) {
            case 1:
                plugin.getLogger().info("[Config] An update to the config was found!");
                updateToVersion2();
                break;
            default:
                plugin.getLogger().info("[Config] Update not required. Config is already up-to-date.");
                break;
        }
    }

    /**
     * Updates the config.yml to version 2, which is required for AlphaChest 2.0.0 to work properly.
     */
    private void updateToVersion2() {
        plugin.getLogger().info("[Config] Performing update...");

        // Rename the old config and keep it as a backup
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        File oldConfigFile = new File(plugin.getDataFolder(), "config_old.yml");

        configFile.renameTo(oldConfigFile);

        if (configFile.exists()) {
            configFile.delete();
        }

        // Get the old config settings
        int autosave = plugin.getConfig().getInt("autosave", 10);
        boolean silentAutosave = plugin.getConfig().getBoolean("silentAutosave", false);
        boolean clearOnDeath = plugin.getConfig().getBoolean("clearOnDeath", false);
        boolean dropOnDeath = plugin.getConfig().getBoolean("dropOnDeath", false);

        // Create a new, fresh config and apply the old settings
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        if (autosave == 0) {
            plugin.getConfig().set("auto-save.enabled", false);
        }

        plugin.getConfig().set("auto-save.interval", autosave);
        plugin.getConfig().set("auto-save.silent", silentAutosave);

        plugin.getConfig().set("death.clear", clearOnDeath);
        plugin.getConfig().set("death.drop", dropOnDeath);

        plugin.saveConfig();

        plugin.getLogger().info("[Config] Update complete.");
    }
}
