package net.sradonia.bukkit.alphachest.listener;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.sradonia.bukkit.alphachest.AlphaChest;
import net.sradonia.bukkit.alphachest.core.VirtualChestManager;

public class DeathListener implements Listener {

    private final AlphaChest plugin;
    private final VirtualChestManager chestManager;

    private final boolean clearOnDeath;
    private final boolean dropOnDeath;

    public DeathListener(AlphaChest plugin) {
        this.plugin = plugin;

        chestManager = plugin.getChestManager();

        // Load the death settings
        clearOnDeath = plugin.getConfig().getBoolean("death.clear", false);
        dropOnDeath = plugin.getConfig().getBoolean("death.drop", false);
    }

    public AlphaChest getPlugin() {
        return plugin;
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
