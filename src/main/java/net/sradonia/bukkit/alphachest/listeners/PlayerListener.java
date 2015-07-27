package net.sradonia.bukkit.alphachest.listeners;

import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.sradonia.bukkit.alphachest.VirtualChestManager;

public class PlayerListener implements Listener
{
    private Plugin pl;

    public PlayerListener(Plugin pl)
    {
	this.pl = pl;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e)
    {
	CompletableFuture.runAsync(() -> VirtualChestManager.setChest(e.getPlayer()));
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e)
    {
	CompletableFuture.runAsync(() -> VirtualChestManager.saveChest(e.getPlayer().getUniqueId()));
	new BukkitRunnable()
	{
	    final Player p = e.getPlayer();
	    public void run()
	    {
		if (!p.isOnline())
		{
		    CompletableFuture.runAsync(() -> VirtualChestManager.removeEntry(e.getPlayer()));
		}
	    }
	}.runTaskLater(pl, 2400);
    }

}
