package com.mast3rplan.alphachest.commands;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet100OpenWindow;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.mast3rplan.alphachest.AlphaChestPlugin;
import com.mast3rplan.alphachest.AlphaWorkbench;
import com.mast3rplan.alphachest.Teller;
import com.mast3rplan.alphachest.Teller.Type;

public class WorkbenchCommand implements CommandExecutor {

	private final AlphaChestPlugin plugin;

	public WorkbenchCommand(AlphaChestPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			final Player player = (Player) sender;
			if (plugin.hasPermission(player, "ac.workbench")) {
				final EntityPlayer eh = ((CraftPlayer) sender).getHandle();
				
				final int bI = 1; // seems like the window ID - should be safe to use a static one
		        eh.netServerHandler.sendPacket(new Packet100OpenWindow(bI, 1, "Virtual Crafting", 9));
		        eh.activeContainer = new AlphaWorkbench(eh, bI);
			} else {
				Teller.tell(player, Type.Warning, "You\'re not allowed to use this command.");
			}
			return true;
		}
		return false;
	}

}
