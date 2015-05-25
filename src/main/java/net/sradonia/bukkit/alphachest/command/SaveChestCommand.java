package net.sradonia.bukkit.alphachest.command;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.sradonia.bukkit.alphachest.AlphaChest;
import net.sradonia.bukkit.alphachest.core.Teller;
import net.sradonia.bukkit.alphachest.core.VirtualChestManager;
import net.sradonia.bukkit.alphachest.util.BukkitUtil;

public class SaveChestCommand implements CommandExecutor {

    private final AlphaChest plugin;
    private final Teller teller;
    private final VirtualChestManager chestManager;

    public SaveChestCommand(AlphaChest plugin) {
        this.plugin = plugin;

        teller = plugin.getTeller();
        chestManager = plugin.getChestManager();
    }

    public AlphaChest getPlugin() {
        return plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("savechest")) {
            if (args.length == 1) {
                if (sender.hasPermission("alphachest.save")) {
                    OfflinePlayer target = BukkitUtil.getOfflinePlayer(args[0]);

                    if (target != null) {
                        chestManager.saveChest(target.getUniqueId());
                        teller.tell(sender, Teller.Type.SUCCESS, "Successfully saved " + args[0] + "\'s chest.");
                    } else {
                        teller.tell(sender, Teller.Type.ERROR, String.format("Chest for %s not found", args[0]));
                    }
                } else {
                    teller.tell(sender, Teller.Type.ERROR, "You are not allowed to use this command.");
                }

                return true;
            }

            return false;
        }

        return false;
    }
}
