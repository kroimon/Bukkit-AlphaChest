package net.sradonia.bukkit.alphachest.utils;

import net.sradonia.bukkit.alphachest.VirtualChestManager;

public class ShutdownThread extends Thread
{
    public void run()
    {
	System.out.println("[AlphaChests] Shutdown Hook Called!");
	VirtualChestManager.saveAll();
    }
}
