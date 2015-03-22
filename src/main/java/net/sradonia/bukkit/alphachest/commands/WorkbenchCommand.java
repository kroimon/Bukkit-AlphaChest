package net.sradonia.bukkit.alphachest.commands;

import net.sradonia.bukkit.alphachest.Teller;
import net.sradonia.bukkit.alphachest.Teller.Type;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorkbenchCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("alphachest.workbench")) {
				((Player) sender).openWorkbench(null, true);
			} else {
				Teller.tell(sender, Type.ERROR, "You're not allowed to use this command.");
			}
			return true;
		}
		return false;
	}

}
