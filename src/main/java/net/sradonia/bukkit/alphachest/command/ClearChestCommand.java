package net.sradonia.bukkit.alphachest.command;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sradonia.bukkit.alphachest.AlphaChest;
import net.sradonia.bukkit.alphachest.core.Teller.Type;
import net.sradonia.bukkit.alphachest.util.BukkitUtil;

public class ClearChestCommand implements CommandExecutor {

    private final AlphaChest plugin;

    public ClearChestCommand(AlphaChest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("clearchest")) {
            switch (args.length) {
                case 0:
                    if (!(sender instanceof Player)) {
                        plugin.getTeller().tell(sender, Type.ERROR, "Only players are able to clear their own chests.");
                        return true;
                    }

                    Player player = (Player) sender;

                    if (player.hasPermission("alphachest.chest")) {
                        plugin.getChestManager().removeChest(player.getUniqueId());
                        plugin.getTeller().tell(player, Type.SUCCESS, "Successfully cleared your chest.");
                    } else {
                        plugin.getTeller().tell(player, Type.ERROR, "You are not allowed to use this command.");
                    }

                    return true;
                case 1:
                    if (sender.hasPermission("alphachest.admin")) {
                        OfflinePlayer target = BukkitUtil.getOfflinePlayer(args[0]);

                        if (target == null) {
                            plugin.getTeller().tell(sender, Type.ERROR, String.format("Chest for %s not found", args[0]));
                        } else {
                            plugin.getChestManager().removeChest(target.getUniqueId());
                            plugin.getTeller().tell(sender, Type.SUCCESS, "Successfully cleared " + args[0] + "\'s chest.");
                        }
                    } else {
                        plugin.getTeller().tell(sender, Type.ERROR, "You are not allowed to clear other user's chests.");
                    }

                    return true;
                default:
                    return false;
            }
        }

        return false;
    }
}
