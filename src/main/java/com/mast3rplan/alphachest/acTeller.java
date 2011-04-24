package com.mast3rplan.alphachest;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class acTeller {

	public static void tell(Player player, acTellerType type, String message) {
		ChatColor color = ChatColor.WHITE;
		switch (type) {
		case Info:
			color = ChatColor.WHITE;
			break;
		case Success:
			color = ChatColor.DARK_GREEN;
			break;
		case Warning:
			color = ChatColor.GOLD;
			break;
		case Error:
			color = ChatColor.DARK_RED;
			break;
		case Misc:
			color = ChatColor.DARK_BLUE;
		}

		player.sendMessage(ChatColor.BLACK + "[" + ChatColor.GRAY + "Alpha Chest" + ChatColor.BLACK + "] " + color + message);
	}

}
