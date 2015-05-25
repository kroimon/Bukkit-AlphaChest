package net.sradonia.bukkit.alphachest.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.sradonia.bukkit.alphachest.AlphaChest;
import net.sradonia.bukkit.alphachest.core.Teller;
import net.sradonia.bukkit.alphachest.core.Teller.Type;
import net.sradonia.bukkit.alphachest.core.VirtualChestManager;

public class SaveChestsCommand implements CommandExecutor {

    private final AlphaChest plugin;
    private final Teller teller;
    private final VirtualChestManager chestManager;

    public SaveChestsCommand(AlphaChest plugin) {
        this.plugin = plugin;

        teller = plugin.getTeller();
        chestManager = plugin.getChestManager();
    }

    public AlphaChest getPlugin() {
        return plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("savechests")) {
            if (sender.hasPermission("alphachest.save")) {
                int savedChests = chestManager.save();
                teller.tell(sender, Type.SUCCESS, "Saved " + savedChests + " chests.");
            } else {
                teller.tell(sender, Type.ERROR, "You are not allowed to use this command.");
            }

            return true;
        }

        return false;
    }
}
