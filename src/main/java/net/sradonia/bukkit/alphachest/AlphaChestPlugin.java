package net.sradonia.bukkit.alphachest;

import java.io.File;
import java.util.logging.Logger;

import net.sradonia.bukkit.alphachest.commands.ChestCommands;
import net.sradonia.bukkit.alphachest.commands.WorkbenchCommand;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AlphaChestPlugin extends JavaPlugin implements Listener {
	private Logger log;

	private VirtualChestManager chestManager;
	private boolean clearOnDeath;

	@Override
	public void onEnable() {
		log = getLogger();

		// Save default config.yml
		if (!new File(getDataFolder(), "config.yml").exists())
			saveDefaultConfig();

		// Initialize
		chestManager = new VirtualChestManager(this, new File(getDataFolder(), "chests"));
		int chestCount = chestManager.load();
		log.info("loaded " + chestCount + " chests");

		clearOnDeath = getConfig().getBoolean("clearOnDeath");

		// Set command executors
		final ChestCommands chestCommands = new ChestCommands(chestManager);
		getCommand("chest").setExecutor(chestCommands);
		getCommand("clearchest").setExecutor(chestCommands);
		getCommand("savechests").setExecutor(chestCommands);
		getCommand("workbench").setExecutor(new WorkbenchCommand());

		// Register events
		getServer().getPluginManager().registerEvents(this, this);

		// Schedule auto-saving
		int autosaveInterval = getConfig().getInt("autosave") * 1200;
		if (autosaveInterval > 0) {
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
				public void run() {
					int savedChests = chestManager.save(false);
					if (savedChests > 0 && !getConfig().getBoolean("silentAutosave"))
						log.info("auto-saved " + savedChests + " chests");
				}
			}, autosaveInterval, autosaveInterval);
		}
	}

	@Override
	public void onDisable() {
		int savedChests = chestManager.save(false);
		log.info("saved " + savedChests + " chests");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDeath(final PlayerDeathEvent event) {
		final Player player = event.getEntity();
		if (clearOnDeath && !player.hasPermission("alphachest.keepOnDeath")) {
			chestManager.removeChest(player.getName());
		}
	}
}
