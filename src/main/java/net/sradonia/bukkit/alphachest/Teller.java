package net.sradonia.bukkit.alphachest;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Teller {

	public enum Type {
		Info, Success, Warning, Error, Misc;
	}

	public static void tell(CommandSender sender, Type type, String message) {
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

		sender.sendMessage(ChatColor.BLACK + "[" + ChatColor.GRAY + "AlphaChest" + ChatColor.BLACK + "] " + color + message);
	}

}
