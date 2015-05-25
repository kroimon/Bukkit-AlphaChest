package net.sradonia.bukkit.alphachest;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.sradonia.bukkit.alphachest.command.ChestCommand;
import net.sradonia.bukkit.alphachest.command.ClearChestCommand;
import net.sradonia.bukkit.alphachest.command.DisposalCommand;
import net.sradonia.bukkit.alphachest.command.SaveChestsCommand;
import net.sradonia.bukkit.alphachest.command.WorkbenchCommand;
import net.sradonia.bukkit.alphachest.core.ConfigUpdater;
import net.sradonia.bukkit.alphachest.core.Teller;
import net.sradonia.bukkit.alphachest.core.VirtualChestManager;
import net.sradonia.bukkit.alphachest.listener.DeathListener;

public class AlphaChest extends JavaPlugin {

    private VirtualChestManager chestManager;
    private Teller teller;
    private ConfigUpdater configUpdater;

    @Override
    public void onEnable() {
        // Save a copy of the default config.yml if one doesn't already exist
        saveDefaultConfig();

        // Initialize
        teller = new Teller(this);

        chestManager = new VirtualChestManager(this);

        configUpdater = new ConfigUpdater(this);
        configUpdater.updateConfig();

        // Register the plugin's events
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new DeathListener(this), this);

        // Register the plugin's commands
        getCommand("chest").setExecutor(new ChestCommand(this));
        getCommand("clearchest").setExecutor(new ClearChestCommand(this));
        getCommand("disposal").setExecutor(new DisposalCommand(this));
        getCommand("savechests").setExecutor(new SaveChestsCommand(this));
        getCommand("workbench").setExecutor(new WorkbenchCommand(this));

        // Schedule an auto-save task
        if (getConfig().getBoolean("auto-save.enabled")) {
            int autosaveInterval = getConfig().getInt("auto-save.interval") * 1200;

            if (autosaveInterval > 0) {
                getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                    @Override
                    public void run() {
                        int savedChests = chestManager.save();

                        if (savedChests > 0 && !getConfig().getBoolean("auto-save.silent")) {
                            getLogger().info("Auto-saved " + savedChests + " chests.");
                        }
                    }
                }, autosaveInterval, autosaveInterval);
            }
        }
    }

    @Override
    public void onDisable() {
        // Save all of the virtual chests
        int savedChests = chestManager.save();
        getLogger().info("Saved " + savedChests + " chests.");

        // Cancel the auto-save task
        getServer().getScheduler().cancelTasks(this);
    }

    /**
     * Returns an instance of the Teller.
     *
     * @return an instance of the Teller
     */
    public Teller getTeller() {
        return teller;
    }

    /**
     * Returns an instance of the VirtualChestManager.
     *
     * @return an instance of the VirtualChestManager
     */
    public VirtualChestManager getChestManager() {
        return chestManager;
    }

    /**
     * Returns an instance of the ConfigUpdater.
     *
     * @return an instance of the ConfigUpdater
     */
    public ConfigUpdater getConfigUpdater() {
        return configUpdater;
    }
}
