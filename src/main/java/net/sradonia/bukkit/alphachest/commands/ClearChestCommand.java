package net.sradonia.bukkit.alphachest.commands;

import net.sradonia.bukkit.alphachest.utils.BukkitUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sradonia.bukkit.alphachest.Teller;
import net.sradonia.bukkit.alphachest.VirtualChestManager;
import net.sradonia.bukkit.alphachest.Teller.Type;
import org.bukkit.inventory.Inventory;

public class ClearChestCommand implements CommandExecutor {

    private final VirtualChestManager chestManager;

    public ClearChestCommand(VirtualChestManager chestManager) {
        this.chestManager = chestManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("clearchest")) {
            switch (args.length) {
                case 0:
                    // Make sure the sender is a player
                    if (!(sender instanceof Player)) {
                        Teller.tell(sender, Type.ERROR, "Only players are able to clear their own chests.");
                        return true;
                    }

                    Player player = (Player) sender;

                    // Make sure the player has permission to use this command
                    if (!player.hasPermission("alphachest.admin.clearchest")) {
                        Teller.tell(player, Type.ERROR, "You are not allowed to use this command.");
                        return true;
                    }

                    chestManager.removeChest(player.getUniqueId());
                    Teller.tell(player, Type.SUCCESS, "Successfully cleared your chest.");

                    return true;
                case 1:
                    // Make sure the sender has permission to use this command
                    if (!sender.hasPermission("alphachest.admin.clearchest.others")) {
                        Teller.tell(sender, Type.ERROR, "You are not allowed to use this command.");
                        return true;
                    }

                    OfflinePlayer target = BukkitUtil.getOfflinePlayerByName(args[0]);

                    if (target != null) {
                        chestManager.removeChest(target.getUniqueId());
                        Teller.tell(sender, Type.SUCCESS, "Successfully cleared " + args[0] + "\'s chest.");
                    } else {
                        Teller.tell(sender, Type.ERROR, String.format("Chest for %s not found", args[0]));
                    }

                    return true;
                default:
                    return false;
            }
        }

        return false;
    }
}
