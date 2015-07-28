package net.sradonia.bukkit.alphachest.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.sradonia.bukkit.alphachest.AlphaChest;
import net.sradonia.bukkit.alphachest.core.Teller.Type;

public class SaveChestsCommand implements CommandExecutor {

    private final AlphaChest plugin;

    public SaveChestsCommand(AlphaChest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("savechests")) {
            if (sender.hasPermission("alphachest.save")) {
                int savedChests = plugin.getChestManager().save();
                plugin.getTeller().tell(sender, Type.SUCCESS, "Saved " + savedChests + " chests.");
            } else {
                plugin.getTeller().tell(sender, Type.ERROR, "You are not allowed to use this command.");
            }

            return true;
        }

        return false;
    }
}
