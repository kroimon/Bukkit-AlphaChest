package com.mast3rplan.alphachest;

import com.mast3rplan.alphachest.Teller.Type;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import net.minecraft.server.EntityPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
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

	private boolean hasPermission(Player player, String permission) {
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

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String name = command.getName();
		if (name.equals("chest"))
			return performChestCommand(sender, args);
		else if (name.equalsIgnoreCase("savechests"))
			return performSaveChestsCommand(sender, args);
		else if (name.equalsIgnoreCase("reloadchests"))
			return performReloadChestsCommand(sender, args);
		else if (name.equalsIgnoreCase("clearchest"))
			return performClearChestCommand(sender, args);
		else
			return false;
	}

	private boolean performClearChestCommand(CommandSender sender, String[] args) {
		if (args.length >= 1) {
			if ((sender instanceof Player) && !hasPermission((Player) sender, "ac.admin")) {
				Teller.tell(sender, Type.Warning, "You\'re not allowed to clear other user's chests.");
				return true;
			}
			chestManager.removeChest(args[0]);
			Teller.tell(sender, Type.Success, "Successfully cleared " + args[0] + "\'s chest.");
			return true;
		} else {
			if (sender instanceof Player) {
				final Player player = (Player) sender;
				if (!hasPermission(player, "ac.chest")) {
					Teller.tell(player, Type.Warning, "You\'re not allowed to use this command.");
				} else {
					chestManager.removeChest(player.getName());
					Teller.tell(player, Type.Success, "Successfully cleared your chest.");
				}
				return true;
			}
		}
		return false;
	}

	private boolean performReloadChestsCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (!hasPermission((Player) sender, "ac.reload")) {
				Teller.tell(sender, Type.Warning, "You\'re not allowed to use this command.");
				return true;
			}
		}

		getConfiguration().load();
		Teller.tell(sender, Type.Success, "Reloaded seperate settings.");
		return true;
	}

	private boolean performSaveChestsCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (!hasPermission((Player) sender, "ac.save")) {
				Teller.tell(sender, Type.Warning, "You\'re not allowed to use this command.");
				return true;
			}
		}

		chestManager.save();
		Teller.tell(sender, Type.Success, "Saved all chests.");
		return true;
	}

	private boolean performChestCommand(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			EntityPlayer eh;
			if (args.length == 1) {
				if (hasPermission(player, "ac.admin")) {
					eh = ((CraftPlayer) sender).getHandle();
					eh.a(chestManager.getChest(args[0]));
				} else {
					Teller.tell(player, Type.Warning, "You\'re not allowed to use this command.");
				}
				return true;

			} else if (args.length == 0) {
				if (hasPermission(player, "ac.chest")) {
					eh = ((CraftPlayer) sender).getHandle();
					eh.a(chestManager.getChest(player.getName()));
				} else {
					Teller.tell(player, Type.Warning, "You\'re not allowed to use this command.");
				}
				return true;
			}
		}
		return false;
	}
}
