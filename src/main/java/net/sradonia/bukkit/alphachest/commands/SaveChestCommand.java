package net.sradonia.bukkit.alphachest.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.sradonia.bukkit.alphachest.Teller;
import net.sradonia.bukkit.alphachest.VirtualChestManager;
import net.sradonia.bukkit.alphachest.utils.BukkitUtil;

public class SaveChestCommand implements CommandExecutor {

    private final VirtualChestManager chestManager;

    public SaveChestCommand(VirtualChestManager chestManager) {
        this.chestManager = chestManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("savechest")) {
            if (args.length == 1) {
                if (sender.hasPermission("alphachest.save")) {
                    OfflinePlayer target = BukkitUtil.getOfflinePlayerByName(args[0]);

                    if (target != null) {
                        chestManager.saveChest(target.getUniqueId());
                        Teller.tell(sender, Teller.Type.SUCCESS, "Successfully saved " + args[0] + "\'s chest.");
                    } else {
                        Teller.tell(sender, Teller.Type.ERROR, String.format("Chest for %s not found", args[0]));
                    }
                } else {
                    Teller.tell(sender, Teller.Type.ERROR, "You are not allowed to use this command.");
                }

                return true;
            }

            return false;
        }

        return false;
    }
}
