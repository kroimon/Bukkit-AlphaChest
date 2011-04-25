package com.mast3rplan.alphachest;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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

public class acPlugin extends JavaPlugin {
	private static Logger log = Logger.getLogger("Minecraft");

	public PermissionHandler permissionHandler;

	private acChestManager chestManager = new acChestManager(getDataFolder());

	public void onEnable() {
		setupPermissions();
		chestManager.load();

		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				chestManager.save();
				log.fine("[AlphaChest] auto-saved chests");
			}
		}, 15000, 30000); // delay=5min, period=10min

		PluginDescriptionFile pdfFile = getDescription();
		log.info("[" + pdfFile.getName() + "] version [" + pdfFile.getVersion() + "] enabled");
	}

	public void onDisable() {
		chestManager.save();
		PluginDescriptionFile pdfFile = getDescription();
		log.info("[" + pdfFile.getName() + "] version [" + pdfFile.getVersion() + "] disabled");
	}

	public void setupPermissions() {
		if (permissionHandler == null) {
			Plugin permissions = getServer().getPluginManager().getPlugin("Permissions");
			if (permissions != null) {
				permissionHandler = ((Permissions) permissions).getHandler();
			} else {
				PluginDescriptionFile pdfFile = this.getDescription();
				log.info("[" + pdfFile.getName() + "] Permission system not enabled. Using seperate settings.");
				setupNoPermissions();
			}
		}
	}

	private void setupNoPermissions() {
		if (!new File(getDataFolder(), "config.yml").exists()) {
			Configuration config = getConfiguration();

			ArrayList<String> admincmds = new ArrayList<String>();
			admincmds.add("ac.admin");
			admincmds.add("ac.save");
			admincmds.add("ac.reload");

			config.setProperty("admincmds", admincmds);
			config.setProperty("admins", getOps());
			config.save();
		}
	}

	private List<String> getOps() {
		ArrayList<String> ops = new ArrayList<String>();

		try {
			BufferedReader e = new BufferedReader(new FileReader("ops.txt"));
			String s = "";
			while ((s = e.readLine()) != null) {
				if (!s.equals("")) {
					ops.add(s);
				}
			}

			e.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ops;
	}

	private boolean hasPermission(Player player, String permission) {
		if (permissionHandler != null) {
			return permissionHandler.has(player, permission);
		} else {
			Configuration config = getConfiguration();
			List<String> admincmds = config.getStringList("admincmds", null);
			List<String> admins = config.getStringList("admins", null);
			if (!admincmds.contains(permission)) {
				return true;
			} else {
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
			return performAcChest(sender, args);
		else if (name.equalsIgnoreCase("savechests"))
			return performAcSaveChests(sender, args);
		else if (name.equalsIgnoreCase("reloadchests"))
			return performAcReloadChests(sender, args);
		else if (name.equalsIgnoreCase("clearchest"))
			return performAcClearChest(sender, args);
		else
			return false;
	}

	private boolean performAcClearChest(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length == 1) {
				if (hasPermission(player, "ac.admin")) {
					chestManager.removeChest(args[0]);
					acTeller.tell(player, acTellerType.Success, "Successfully cleared " + args[0] + "\'s chest.");
					return true;
				}

				acTeller.tell(player, acTellerType.Warning, "You\'re not allowed to use this command.");
				return true;
			}

			if (hasPermission(player, "ac.chest")) {
				chestManager.removeChest(player.getName());
				acTeller.tell(player, acTellerType.Success, "Successfully cleared your chest.");
				return true;
			}
		}

		return false;
	}

	private boolean performAcReloadChests(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (hasPermission(player, "ac.reload")) {
				if (permissionHandler != null) {
					getConfiguration().load();
					acTeller.tell(player, acTellerType.Success, "Reloaded seperate settings.");
				} else {
					acTeller.tell(player, acTellerType.Warning, "Please use /pr instead.");
				}
				return true;
			} else {
				acTeller.tell(player, acTellerType.Warning, "You\'re not allowed to use this command.");
				return true;
			}
		} else {
			return false;
		}
	}

	private boolean performAcSaveChests(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (hasPermission(player, "ac.save")) {
				chestManager.save();
				acTeller.tell(player, acTellerType.Success, "Saved all chests.");
				return true;
			} else {
				acTeller.tell(player, acTellerType.Warning, "You\'re not allowed to use this command.");
				return true;
			}
		} else {
			return false;
		}
	}

	private boolean performAcChest(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			EntityPlayer eh;
			if (args.length == 1) {
				if (hasPermission(player, "ac.admin")) {
					eh = ((CraftPlayer) sender).getHandle();
					eh.a(chestManager.getChest(args[0]));
				} else {
					acTeller.tell(player, acTellerType.Warning, "You\'re not allowed to use this command.");
				}
				return true;

			} else if (args.length == 0) {
				if (hasPermission(player, "ac.chest")) {
					eh = ((CraftPlayer) sender).getHandle();
					eh.a(chestManager.getChest(player.getName()));
				} else {
					acTeller.tell(player, acTellerType.Warning, "You\'re not allowed to use this command.");
				}
				return true;
			}
		}
		return false;
	}
}
