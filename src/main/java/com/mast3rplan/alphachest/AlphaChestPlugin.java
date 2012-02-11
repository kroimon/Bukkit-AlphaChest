package com.mast3rplan.alphachest;

import com.mast3rplan.alphachest.commands.ChestCommands;
import com.mast3rplan.alphachest.commands.WorkbenchCommand;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class AlphaChestPlugin extends JavaPlugin {
	private static final Logger log = Logger.getLogger("Minecraft");

	private AlphaChestManager chestManager;

	public void onEnable() {
		// Initialize
		chestManager = new AlphaChestManager(new File(getDataFolder(), "chests"));
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
