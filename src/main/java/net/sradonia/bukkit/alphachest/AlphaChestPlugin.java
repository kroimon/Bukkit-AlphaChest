package net.sradonia.bukkit.alphachest;

import java.io.File;
import java.util.logging.Logger;

import net.sradonia.bukkit.alphachest.commands.ChestCommands;
import net.sradonia.bukkit.alphachest.commands.WorkbenchCommand;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class AlphaChestPlugin extends JavaPlugin {
	private static final Logger log = Logger.getLogger("Minecraft");

	private VirtualChestManager chestManager;

	public void onEnable() {
		final PluginDescriptionFile pdf = getDescription();

		// Save default config.yml
		saveDefaultConfig();

		// Initialize
		chestManager = new VirtualChestManager(new File(getDataFolder(), "chests"));
		int chestCount = chestManager.load();
		log.info("[" + pdf.getName() + "] loaded " + chestCount + " chests");

		// Set command executors
		final ChestCommands chestCommands = new ChestCommands(chestManager);
		getCommand("chest").setExecutor(chestCommands);
		getCommand("clearchest").setExecutor(chestCommands);
		getCommand("savechests").setExecutor(chestCommands);
		getCommand("workbench").setExecutor(new WorkbenchCommand());

		// Schedule auto-saving
		int autosaveInterval = getConfig().getInt("autosave") * 1200;
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				int savedChests = chestManager.save(false);
				if (savedChests > 0 && !getConfig().getBoolean("silentAutosave"))
					log.info("[" + pdf.getName() + "] auto-saved " + savedChests + " chests");
			}
		}, autosaveInterval, autosaveInterval);

		// Success
		log.info("[" + pdf.getName() + "] version [" + pdf.getVersion() + "] enabled");
	}

	public void onDisable() {
		PluginDescriptionFile pdf = getDescription();

		int savedChests = chestManager.save(false);

		log.info("[" + pdf.getName() + "] saved " + savedChests + " chests");
		log.info("[" + pdf.getName() + "] version [" + pdf.getVersion() + "] disabled");
	}

}
