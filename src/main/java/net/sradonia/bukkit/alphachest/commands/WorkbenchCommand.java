package net.sradonia.bukkit.alphachest.commands;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet100OpenWindow;
import net.sradonia.bukkit.alphachest.VirtualWorkbench;
import net.sradonia.bukkit.alphachest.Teller;
import net.sradonia.bukkit.alphachest.Teller.Type;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;

public class WorkbenchCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof CraftPlayer) {
			if (sender.hasPermission("alphachest.workbench")) {
				final EntityPlayer player = ((CraftPlayer) sender).getHandle();

				int containerCounter = player.nextContainerCounter();
				player.netServerHandler.sendPacket(new Packet100OpenWindow(containerCounter, 1, "Virtual Crafting", 9));
				player.activeContainer = new VirtualWorkbench(player, containerCounter);
			} else {
				Teller.tell(sender, Type.Error, "You're not allowed to use this command.");
			}
			return true;
		}
		return false;
	}

}
