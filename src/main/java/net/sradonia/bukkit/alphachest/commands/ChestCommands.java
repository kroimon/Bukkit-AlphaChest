package net.sradonia.bukkit.alphachest.commands;

import net.minecraft.server.EntityPlayer;
import net.sradonia.bukkit.alphachest.VirtualChest;
import net.sradonia.bukkit.alphachest.VirtualChestManager;
import net.sradonia.bukkit.alphachest.Teller;
import net.sradonia.bukkit.alphachest.Teller.Type;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ChestCommands implements CommandExecutor {

	private final VirtualChestManager chestManager;

	public ChestCommands(VirtualChestManager chestManager) {
		this.chestManager = chestManager;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		final String name = command.getName();
		if (name.equals("chest"))
			return performChestCommand(sender, args);
		else if (name.equalsIgnoreCase("clearchest"))
			return performClearChestCommand(sender, args);
		else if (name.equalsIgnoreCase("savechests"))
			return performSaveChestsCommand(sender, args);
		else
			return false;
	}

	private boolean performChestCommand(CommandSender sender, String[] args) {
		if (sender instanceof CraftPlayer) {
			if (args.length == 0) {
				if (sender.hasPermission("alphachest.chest")) {
					VirtualChest chest = chestManager.getChest(sender.getName());
					EntityPlayer player = ((CraftPlayer) sender).getHandle();
					player.openContainer(chest);
				} else {
					Teller.tell(sender, Type.Error, "You're not allowed to use this command.");
				}
				return true;
			} else if (args.length == 1) {
				if (sender.hasPermission("alphachest.admin")) {
					VirtualChest chest = chestManager.getChest(args[0]);
					EntityPlayer player = ((CraftPlayer) sender).getHandle();
					player.openContainer(chest);
				} else {
					Teller.tell(sender, Type.Error, "You're not allowed to open other user's chests.");
				}
				return true;

			} else {
				return false;
			}
		} else {
			Teller.tell(sender, Type.Error, "Only players are able to open chests.");
			return true;
		}
	}

	private boolean performClearChestCommand(CommandSender sender, String[] args) {
		if (args.length == 0 && sender instanceof Player) {
			if (sender.hasPermission("alphachest.chest")) {
				chestManager.removeChest(sender.getName());
				Teller.tell(sender, Type.Success, "Successfully cleared your chest.");
			} else {
				Teller.tell(sender, Type.Error, "You're not allowed to use this command.");
			}
			return true;
		} else if (args.length == 1) {
			if (sender.hasPermission("alphachest.admin")) {
				chestManager.removeChest(args[0]);
				Teller.tell(sender, Type.Success, "Successfully cleared " + args[0] + "\'s chest.");
			} else {
				Teller.tell(sender, Type.Error, "You're not allowed to clear other user's chests.");
			}
			return true;
		} else {
			return false;
		}
	}

	private boolean performSaveChestsCommand(CommandSender sender, String[] args) {
		if (sender.hasPermission("alphachest.save")) {
			int savedChests = chestManager.save(true);
			Teller.tell(sender, Type.Success, "Saved " + savedChests + " chests.");
		} else {
			Teller.tell(sender, Type.Error, "You're not allowed to use this command.");
		}
		return true;
	}

}
