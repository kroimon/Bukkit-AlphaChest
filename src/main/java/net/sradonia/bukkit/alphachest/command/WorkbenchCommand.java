package net.sradonia.bukkit.alphachest.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sradonia.bukkit.alphachest.AlphaChest;
import net.sradonia.bukkit.alphachest.core.Teller;
import net.sradonia.bukkit.alphachest.core.Teller.Type;

public class WorkbenchCommand implements CommandExecutor {

    private final AlphaChest plugin;
    private final Teller teller;

    public WorkbenchCommand(AlphaChest plugin) {
        this.plugin = plugin;

        teller = plugin.getTeller();
    }

    public AlphaChest getPlugin() {
        return plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("workbench")) {
            // Make sure the sender is a player
            if (!(sender instanceof Player)) {
                teller.tell(sender, Type.ERROR, "Only players are able to open virtual workbenches.");
                return true;
            }

            Player player = (Player) sender;

            if (player.hasPermission("alphachest.workbench")) {
                player.openWorkbench(null, true);
            } else {
                teller.tell(sender, Type.ERROR, "You are not allowed to use this command.");
            }

            return true;
        }

        return false;
    }
}
