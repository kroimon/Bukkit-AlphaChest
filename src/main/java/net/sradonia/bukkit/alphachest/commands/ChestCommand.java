package net.sradonia.bukkit.alphachest.commands;

import net.sradonia.bukkit.alphachest.Teller;
import net.sradonia.bukkit.alphachest.VirtualChestManager;
import net.sradonia.bukkit.alphachest.Teller.Type;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ChestCommand implements CommandExecutor {
	
	private final VirtualChestManager chestManager;

	public ChestCommand(VirtualChestManager chestManager) {
		this.chestManager = chestManager;
	}
	
	/**
	 * Searches for an offline player by their name.
	 * 
	 * @param name the name to search for
	 * @return the found player or null
	 */
	private OfflinePlayer findOfflinePlayerByName(String name) {
		OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
		for (OfflinePlayer player : offlinePlayers) {
			if (player.getName().equalsIgnoreCase(name)) {
				return player;
			}
		}
		return null;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("chest")) {
			// Make sure the sender is a player
			if (!(sender instanceof Player)) {
				Teller.tell(sender, Type.ERROR, "Only players are able to open chests.");
				return true;
			}
				
			Player player = (Player) sender;
			
			// Prevent opening of the chest in Creative Mode
			if (player.getGameMode().equals(GameMode.CREATIVE) && !player.hasPermission("alphachest.chest.creativeMode")) {
				Teller.tell(sender, Type.ERROR, "You are not allowed to open your chest in Creative Mode!");
				return true;
			}

			switch (args.length) {
				case 0:
					// Open the player's own chest
					if (player.hasPermission("alphachest.chest")) {
						Inventory chest = chestManager.getChest(player.getUniqueId());
						player.openInventory(chest);
					} else {
						Teller.tell(sender, Type.ERROR, "You are not allowed to use this command.");
					}
					return true;	
				case 1:
					// Open someone else's chest
					if (player.hasPermission("alphachest.admin")) {
						OfflinePlayer target = findOfflinePlayerByName(args[0]);
						if (target != null) {
							Inventory chest = chestManager.getChest(target.getUniqueId());
							player.openInventory(chest);
						} else {
							Teller.tell(player, Type.ERROR, String.format("Chest for %s not found", args[0]));
						}
					} else {
						Teller.tell(player, Type.ERROR, "You are not allowed to open other user's chests.");
					}
					return true;
			}

			return false;
		}
		return false;
	}
}
