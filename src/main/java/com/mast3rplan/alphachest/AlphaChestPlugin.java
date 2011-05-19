package com.mast3rplan.alphachest;

import com.mast3rplan.alphachest.commands.ChestCommands;
import com.mast3rplan.alphachest.commands.WorkbenchCommand;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class AlphaChestPlugin extends JavaPlugin {
	private static final Logger log = Logger.getLogger("Minecraft");

	private PermissionHandler permissionHandler;
	private AlphaChestManager chestManager;

	public void onEnable() {
		// Load/create configuration
		final Configuration config = getConfiguration();
		if (!new File(getDataFolder(), "config.yml").exists()) {
			ArrayList<String> admincmds = new ArrayList<String>();
			admincmds.add("ac.admin");
			admincmds.add("ac.save");
			admincmds.add("ac.reload");

			config.setProperty("admincmds", admincmds);
			config.setProperty("admins", getOps());

			config.setProperty("autosave", 10);

			config.save();
		}

		// Load Permissions plugin, if available
		setupPermissions();

		// Initialize
		chestManager = new AlphaChestManager(new File(getDataFolder(), "chests"));
		chestManager.load();

		// Set command executors
		final ChestCommands chestCommands = new ChestCommands(this, chestManager);
		getCommand("chest").setExecutor(chestCommands);
		getCommand("clearchest").setExecutor(chestCommands);
		getCommand("savechests").setExecutor(chestCommands);
		getCommand("workbench").setExecutor(new WorkbenchCommand(this));

		// Schedule auto-saving
		int autosaveInterval = config.getInt("autosave", 10) * 3000;
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

	private void setupPermissions() {
		if (permissionHandler == null) {
			final Plugin permissions = getServer().getPluginManager().getPlugin("Permissions");
			if (permissions != null) {
				permissionHandler = ((Permissions) permissions).getHandler();
			} else {
				PluginDescriptionFile pdfFile = getDescription();
				log.info("[" + pdfFile.getName() + "] Permission system not enabled. Using seperate settings.");
			}
		}
	}

	private List<String> getOps() {
		ArrayList<String> ops = new ArrayList<String>();
		try {
			BufferedReader e = new BufferedReader(new FileReader("ops.txt"));
			String s = "";
			while ((s = e.readLine()) != null)
				if (!s.equals(""))
					ops.add(s);
			e.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ops;
	}

	public boolean hasPermission(Player player, String permission) {
		if (permissionHandler != null) {
			return permissionHandler.has(player, permission);
		} else {
			final Configuration config = getConfiguration();
			final List<String> admincmds = config.getStringList("admincmds", null);
			if (!admincmds.contains(permission)) {
				return true;
			} else {
				final List<String> admins = config.getStringList("admins", null);
				for (String admin : admins) {
					if (admin.equalsIgnoreCase(player.getName())) {
						return true;
					}
				}
				return false;
			}
		}
	}

}
