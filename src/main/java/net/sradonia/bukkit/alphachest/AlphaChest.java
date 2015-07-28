package net.sradonia.bukkit.alphachest;

import org.bukkit.plugin.java.JavaPlugin;

import net.sradonia.bukkit.alphachest.commands.ChestCommand;
import net.sradonia.bukkit.alphachest.commands.ClearChestCommand;
import net.sradonia.bukkit.alphachest.commands.DisposalCommand;
import net.sradonia.bukkit.alphachest.commands.SaveChestsCommand;
import net.sradonia.bukkit.alphachest.commands.WorkbenchCommand;
import net.sradonia.bukkit.alphachest.core.ConfigUpdater;
import net.sradonia.bukkit.alphachest.core.Teller;
import net.sradonia.bukkit.alphachest.core.VirtualChestManager;
import net.sradonia.bukkit.alphachest.listeners.PlayerListener;

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

        // Register the plugin's events
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

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
     * Returns the Teller.
     *
     * @return the Teller
     */
    public Teller getTeller() {
        return teller;
    }

    /**
     * Returns the VirtualChestManager.
     *
     * @return the VirtualChestManager
     */
    public VirtualChestManager getChestManager() {
        return chestManager;
    }

    /**
     * Returns the ConfigUpdater.
     *
     * @return the ConfigUpdater
     */
    public ConfigUpdater getConfigUpdater() {
        return configUpdater;
    }
}
