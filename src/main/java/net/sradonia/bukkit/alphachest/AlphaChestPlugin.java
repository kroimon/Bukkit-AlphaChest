package net.sradonia.bukkit.alphachest;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import net.sradonia.bukkit.alphachest.commands.CommandChest;
import net.sradonia.bukkit.alphachest.commands.CommandClearChest;
import net.sradonia.bukkit.alphachest.commands.CommandSaveChests;
import net.sradonia.bukkit.alphachest.commands.CommandWorkbench;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class AlphaChestPlugin extends JavaPlugin implements Listener {
	
	private Logger logger;

	private VirtualChestManager chestManager;
	
	private Teller teller;
	
	private boolean clearOnDeath;
	private boolean dropOnDeath;

	@Override
	public void onEnable() {
		logger = getLogger();

		// Save a copy of the default config.yml if one is not there
		saveDefaultConfig();

		// Initialize
		File chestFolder = new File(getDataFolder(), "chests");
		chestManager = new VirtualChestManager(chestFolder, getLogger());
		
		teller = new Teller(this);
		teller.init();

		// Load settings
		clearOnDeath = getConfig().getBoolean("clearOnDeath");
		dropOnDeath = getConfig().getBoolean("dropOnDeath");

		// Set command executors
		getCommand("chest").setExecutor(new CommandChest(chestManager));
		getCommand("clearchest").setExecutor(new CommandClearChest(chestManager));
		getCommand("savechests").setExecutor(new CommandSaveChests(chestManager));
		getCommand("workbench").setExecutor(new CommandWorkbench());

		// Register events
		getServer().getPluginManager().registerEvents(this, this);

		// Schedule auto-saving
		int autosaveInterval = getConfig().getInt("autosave") * 1200;
		if (autosaveInterval > 0) {
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
				public void run() {
					int savedChests = chestManager.save();
					if (savedChests > 0 && !getConfig().getBoolean("silentAutosave"))
						logger.info("Auto-saved " + savedChests + " chests");
				}
			}, autosaveInterval, autosaveInterval);
		}
	}

	@Override
	public void onDisable() {
		int savedChests = chestManager.save();
		logger.info("Saved " + savedChests + " chests");
	}

	/**
	 * Handles a player's death and clears the chest or drops its contents depending on configuration and permissions.
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPlayerDeath(final PlayerDeathEvent event) {
		final Player player = event.getEntity();

		boolean drop = dropOnDeath;
		boolean clear = dropOnDeath || clearOnDeath;

		if (player.hasPermission("alphachest.keepOnDeath")) {
			drop = false;
			clear = false;
		} else if (player.hasPermission("alphachest.dropOnDeath")) {
			drop = true;
			clear = true;
		} else if (player.hasPermission("alphachest.clearOnDeath")) {
			drop = false;
			clear = true;
		}

		if (drop) {
			List<ItemStack> drops = event.getDrops();
			Inventory chest = chestManager.getChest(player.getUniqueId());
			for (int i = 0; i < chest.getSize(); i++) {
				drops.add(chest.getItem(i));
			}
		}
		
		if (clear) {
			chestManager.removeChest(player.getUniqueId());
		}
	}
}
