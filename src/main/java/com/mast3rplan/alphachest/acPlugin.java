package com.mast3rplan.alphachest;

import com.mast3rplan.alphachest.acChestManager;
import com.mast3rplan.alphachest.acTeller;
import com.mast3rplan.alphachest.acTellerType;
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
	public PermissionHandler Permissions;
	public boolean usingPermissions;
	public Configuration configuration;
	private File pluginsFolder = new File(this.getDataFolder(), "plugins");
	private File dataFolder;
	public acChestManager chestManager;

	public acPlugin() {
		this.dataFolder = new File(this.pluginsFolder, "AlphaChest");
		this.chestManager = new acChestManager(this.dataFolder);
	}

	public void onDisable() {
		this.chestManager.save();
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[" + pdfFile.getName() + "] version [" + pdfFile.getVersion() + "] disabled");
	}

	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		if (!this.dataFolder.exists()) {
			this.dataFolder.mkdir();
		}

		log.info("[" + pdfFile.getName() + "] version [" + pdfFile.getVersion() + "] enabled");
		this.setupPermissions();
		this.chestManager.load();
	}

	public void setupPermissions() {
		Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
		if (this.Permissions == null) {
			if (test != null) {
				this.Permissions = ((Permissions) test).getHandler();
				this.usingPermissions = true;
			} else {
				this.usingPermissions = false;
				PluginDescriptionFile pdfFile = this.getDescription();
				log.info("[" + pdfFile.getName() + "] Permission system not enabled. Using seperate settings.");
				this.setupNoPermissions();
			}
		}

	}

	private void setupNoPermissions() {
		File configurationFile = new File(this.dataFolder, "config.yml");
		boolean init = false;
		if (!configurationFile.exists()) {
			init = true;
		}

		this.configuration = new Configuration(configurationFile);
		this.configuration.load();
		if (init) {
			ArrayList<String> admincmds = new ArrayList<String>();
			List<String> admins = this.getOps();
			admincmds.add("ac.admin");
			admincmds.add("ac.save");
			admincmds.add("ac.reload");
			this.configuration.setProperty("admincmds", admincmds);
			this.configuration.setProperty("admins", admins);
			this.configuration.save();
		}

	}

	private List<String> getOps() {
		ArrayList<String> ops = new ArrayList<String>();

		try {
			BufferedReader e = new BufferedReader(new FileReader(new File("ops.txt")));
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

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String name = command.getName().toLowerCase();
		return name.equalsIgnoreCase("chest") ? this.performAcChest(sender, args) : (name.equalsIgnoreCase("savechests") ? this.performAcSaveChests(
				sender, args) : (name.equalsIgnoreCase("reloadchests") ? this.performAcReloadChests(sender, args) : (name
				.equalsIgnoreCase("clearchest") ? this.performAcClearChest(sender, args) : false)));
	}

	private boolean performAcClearChest(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length == 1) {
				if (this.hasPermission(player, "ac.admin")) {
					this.chestManager.removeChest(args[0]);
					acTeller.tell(player, acTellerType.Success, "Successfully cleared " + args[0] + "\'s chest.");
					return true;
				}

				acTeller.tell(player, acTellerType.Warning, "You\'re not allowed to use this command.");
				return true;
			}

			if (this.hasPermission(player, "ac.chest")) {
				this.chestManager.removeChest(player.getName());
				acTeller.tell(player, acTellerType.Success, "Successfully cleared your chest.");
				return true;
			}
		}

		return false;
	}

	private boolean performAcReloadChests(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (this.hasPermission(player, "ac.reload")) {
				if (!this.usingPermissions) {
					this.configuration.load();
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
			if (this.hasPermission(player, "ac.save")) {
				this.chestManager.save();
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
				if (this.hasPermission(player, "ac.admin")) {
					eh = ((CraftPlayer) sender).getHandle();
					eh.a(this.chestManager.getChest(args[0]));
					return true;
				}

				acTeller.tell(player, acTellerType.Warning, "You\'re not allowed to use this command.");
				return true;
			}

			if (args.length == 0) {
				if (this.hasPermission(player, "ac.chest")) {
					eh = ((CraftPlayer) sender).getHandle();
					eh.a(this.chestManager.getChest(player.getName()));
					return true;
				}

				acTeller.tell(player, acTellerType.Warning, "You\'re not allowed to use this command.");
				return true;
			}
		}

		return false;
	}

	private boolean hasPermission(Player player, String permission) {
		if (this.usingPermissions) {
			return this.Permissions.has(player, permission);
		} else {
			List<String> admincmds = this.configuration.getStringList("admincmds", null);
			List<String> admins = this.configuration.getStringList("admins", null);
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
}
