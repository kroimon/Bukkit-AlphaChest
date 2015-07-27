package net.sradonia.bukkit.alphachest.commands;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import net.sradonia.bukkit.alphachest.Teller;

public class DisposalCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
	if (command.getName().equalsIgnoreCase("disposal"))
	{
	    // Make sure the sender is a player
	    if (!(sender instanceof Player))
	    {
		Teller.tell(sender, Teller.Type.ERROR, "Only players are able to open virtual workbenches.");
		return true;
	    }

	    Player player = (Player) sender;

	    if (player.hasPermission("alphachest.disposal"))
	    {
		Inventory disposal = Bukkit.getServer().createInventory(player, 27, "Disposal");
		player.openInventory(disposal);
	    }
	    else
	    {
		Teller.tell(sender, Teller.Type.ERROR, "You are not allowed to use this command.");
	    }

	    return true;
	}

	return false;
    }
}
