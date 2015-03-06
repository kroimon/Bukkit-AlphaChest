package net.sradonia.bukkit.alphachest.commands;

import net.sradonia.bukkit.alphachest.Teller;
import net.sradonia.bukkit.alphachest.VirtualChestManager;
import net.sradonia.bukkit.alphachest.Teller.Type;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandSaveChests implements CommandExecutor {
	
	private final VirtualChestManager chestManager;

	public CommandSaveChests(VirtualChestManager chestManager) {
		this.chestManager = chestManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("savechests")) {
			if (sender.hasPermission("alphachest.save")) {
				int savedChests = chestManager.save();
				Teller.tell(sender, Type.SUCCESS, "Saved " + savedChests + " chests.");
			} else {
				Teller.tell(sender, Type.ERROR, "You are not allowed to use this command.");
			}
			return true;
		}
		return false;
	}
}
