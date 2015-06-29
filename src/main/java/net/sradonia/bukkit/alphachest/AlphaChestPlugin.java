package net.sradonia.bukkit.alphachest;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import net.sradonia.bukkit.alphachest.commands.*;
import net.sradonia.bukkit.alphachest.listeners.PlayerListener;

public class AlphaChestPlugin extends JavaPlugin {

    private Logger logger;
    public static boolean useAsync;

    private VirtualChestManager chestManager;

    private Teller teller;

    @Override
    public void onEnable() {
        // Save a copy of the default config.yml if one doesn't already exist
        saveDefaultConfig();

        // Initialize some classes and objects
        logger = getLogger();

        File chestFolder = new File(getDataFolder(), "chests");
        chestManager = new VirtualChestManager(chestFolder, logger);
        useAsync = getConfig().getBoolean("useAsync");
        teller = new Teller(this);

        // Set the plugin's command executors
        getCommand("chest").setExecutor(new ChestCommand(chestManager));
        getCommand("clearchest").setExecutor(new ClearChestCommand(chestManager));
        getCommand("disposal").setExecutor(new DisposalCommand(chestManager));
        getCommand("savechests").setExecutor(new SaveChestsCommand(chestManager));
        getCommand("workbench").setExecutor(new WorkbenchCommand());

        // Register the plugin's events
        getServer().getPluginManager().registerEvents(new PlayerListener(this, chestManager), this);

        // Schedule an auto-save task
        int autosaveInterval = getConfig().getInt("autosave") * 1200;

        if (autosaveInterval > 0) {
            getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                public void run() {
                    int savedChests = chestManager.save();

                    if (savedChests > 0 && !getConfig().getBoolean("silentAutosave")) {
                        logger.info("Auto-saved " + savedChests + " chests");
                    }
                }
            }, autosaveInterval, autosaveInterval);
        }
    }

    @Override
    public void onDisable() {
        int savedChests = chestManager.save();
        logger.info("Saved " + savedChests + " chests");
    }
}
