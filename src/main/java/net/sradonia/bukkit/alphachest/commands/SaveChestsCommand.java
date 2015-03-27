package net.sradonia.bukkit.alphachest.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.sradonia.bukkit.alphachest.Teller;
import net.sradonia.bukkit.alphachest.Teller.Type;
import net.sradonia.bukkit.alphachest.VirtualChestManager;

public class SaveChestsCommand implements CommandExecutor {

    private final VirtualChestManager chestManager;

    public SaveChestsCommand(VirtualChestManager chestManager) {
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
