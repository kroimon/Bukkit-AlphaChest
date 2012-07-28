package net.sradonia.bukkit.alphachest;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import net.sradonia.bukkit.alphachest.commands.ChestCommands;
import net.sradonia.bukkit.alphachest.commands.WorkbenchCommand;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class AlphaChestPlugin extends JavaPlugin implements Listener {
	private Logger log;

	private VirtualChestManager chestManager;
	private boolean clearOnDeath;
	private boolean dropOnDeath;

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
		dropOnDeath  = getConfig().getBoolean("dropOnDeath");

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

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDeath(final PlayerDeathEvent event) {
		final Player player = event.getEntity();

		boolean drop  = dropOnDeath;
		boolean clear = dropOnDeath || clearOnDeath;

		if (player.hasPermission("alphachest.keepOnDeath")) {
			drop  = false;
			clear = false;
		} else if (player.hasPermission("alphachest.dropOnDeath")) {
			drop  = true;
			clear = true;
		} else if (player.hasPermission("alphachest.clearOnDeath")) {
			drop  = false;
			clear = true;
		}

		if (drop) {
			List<ItemStack> drops = event.getDrops();
			Inventory chest = chestManager.getChest(player.getName());
			for (int i = 0; i < chest.getSize(); i++) {
				drops.add(chest.getItem(i));
			}
		}
		if (clear) {
			chestManager.removeChest(player.getName());
		}
	}
}
