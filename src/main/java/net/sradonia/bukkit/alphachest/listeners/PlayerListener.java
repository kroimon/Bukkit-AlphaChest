package net.sradonia.bukkit.alphachest.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.sradonia.bukkit.alphachest.AlphaChestPlugin;
import net.sradonia.bukkit.alphachest.VirtualChestManager;

public class PlayerListener implements Listener {

    private final AlphaChestPlugin plugin;
    private final VirtualChestManager chestManager;

    private boolean clearOnDeath;
    private boolean dropOnDeath;

    public PlayerListener(AlphaChestPlugin plugin, VirtualChestManager chestManager) {
        this.plugin = plugin;
        this.chestManager = chestManager;

        // Load the death event settings
        clearOnDeath = plugin.getConfig().getBoolean("clearOnDeath");
        dropOnDeath = plugin.getConfig().getBoolean("dropOnDeath");
    }

    /**
     * Handles a player's death and clears the chest or drops its contents depending on configuration and permissions.
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();

        boolean drop = dropOnDeath;
        boolean clear = dropOnDeath || clearOnDeath;

        if (player.hasPermission("alphachest.death.keep")) {
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
            Inventory chest = chestManager.getChest(player.getUniqueId());

            for (int i = 0; i < chest.getSize(); i++) {
                drops.add(chest.getItem(i));
            }
        }

        if (clear) {
            chestManager.removeChest(player.getUniqueId());
        }
    }
}
