package net.sradonia.bukkit.alphachest.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.sradonia.bukkit.alphachest.AlphaChest;

public class PlayerListener implements Listener {

    private final AlphaChest plugin;

    private final boolean clearOnDeath;
    private final boolean dropOnDeath;

    public PlayerListener(AlphaChest plugin) {
        this.plugin = plugin;

        // Load the death settings
        clearOnDeath = plugin.getConfig().getBoolean("death.clear", false);
        dropOnDeath = plugin.getConfig().getBoolean("death.drop", false);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        boolean drop = dropOnDeath;
        boolean clear = dropOnDeath || clearOnDeath;

        if (player.hasPermission("alphachest.keepOnDeath")) {
            drop = false;
            clear = false;
        } else if (player.hasPermission("alphachest.dropOnDeath")) {
            drop = true;
            clear = true;
        } else if (player.hasPermission("alphachest.clearOnDeath")) {
            drop = false;
            clear = true;
        }

        if (drop) {
            List<ItemStack> drops = event.getDrops();
            Inventory chest = plugin.getChestManager().getChest(player.getUniqueId());

            for (int i = 0; i < chest.getSize(); i++) {
                drops.add(chest.getItem(i));
            }
        }

        if (clear) {
            plugin.getChestManager().removeChest(player.getUniqueId());
        }
    }
}
