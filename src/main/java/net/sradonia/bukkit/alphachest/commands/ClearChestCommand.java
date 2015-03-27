package net.sradonia.bukkit.alphachest.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sradonia.bukkit.alphachest.Teller;
import net.sradonia.bukkit.alphachest.Teller.Type;
import net.sradonia.bukkit.alphachest.VirtualChestManager;
import net.sradonia.bukkit.alphachest.utils.BukkitUtil;

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

                    if (player.hasPermission("alphachest.chest")) {
                        chestManager.removeChest(player.getUniqueId());
                        Teller.tell(player, Type.SUCCESS, "Successfully cleared your chest.");
                    } else {
                        Teller.tell(player, Type.ERROR, "You are not allowed to use this command.");
                    }

                    return true;
                case 1:
                    if (sender.hasPermission("alphachest.admin")) {
                        OfflinePlayer target = BukkitUtil.getOfflinePlayerByName(args[0]);

                        if (target != null) {
                            chestManager.removeChest(target.getUniqueId());
                            Teller.tell(sender, Type.SUCCESS, "Successfully cleared " + args[0] + "\'s chest.");
                        } else {
                            Teller.tell(sender, Type.ERROR, String.format("Chest for %s not found", args[0]));
                        }
                    } else {
                        Teller.tell(sender, Type.ERROR, "You are not allowed to clear other user's chests.");
                    }

                    return true;
                default:
                    return false;
            }
        }

        return false;
    }
}
