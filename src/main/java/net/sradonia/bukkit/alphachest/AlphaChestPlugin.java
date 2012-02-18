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
		// Initialize
		chestManager = new VirtualChestManager(new File(getDataFolder(), "chests"));
		chestManager.load();

		// Set command executors
		final ChestCommands chestCommands = new ChestCommands(chestManager);
		getCommand("chest").setExecutor(chestCommands);
		getCommand("clearchest").setExecutor(chestCommands);
		getCommand("savechests").setExecutor(chestCommands);
		getCommand("workbench").setExecutor(new WorkbenchCommand());

		// Schedule auto-saving
		int autosaveInterval = getConfig().getInt("autosave", 10) * 3000;
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				chestManager.save();
				log.fine("[AlphaChest] auto-saved chests");
			}
		}, autosaveInterval, autosaveInterval);

		// Success
		PluginDescriptionFile pdfFile = getDescription();
		log.info("[" + pdfFile.getName() + "] version [" + pdfFile.getVersion() + "] enabled");
	}

	public void onDisable() {
		chestManager.save();

		PluginDescriptionFile pdfFile = getDescription();
		log.info("[" + pdfFile.getName() + "] version [" + pdfFile.getVersion() + "] disabled");
	}

}
