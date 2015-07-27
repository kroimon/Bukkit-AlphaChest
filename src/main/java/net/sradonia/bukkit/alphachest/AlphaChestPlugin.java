package net.sradonia.bukkit.alphachest;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.bukkit.plugin.java.JavaPlugin;

import net.sradonia.bukkit.alphachest.commands.*;
import net.sradonia.bukkit.alphachest.listeners.PlayerListener;

public class AlphaChestPlugin extends JavaPlugin
{
    private Teller teller;
    @Override
    public void onEnable()
    {
	saveDefaultConfig();
	File chestFolder = new File(getDataFolder(), "chests");
	VirtualChestManager.initChestMan(chestFolder);
	setTeller(new Teller(this));
	getCommand("chest").setExecutor(new ChestCommand());
	getCommand("clearchest").setExecutor(new ClearChestCommand());
	getCommand("savechests").setExecutor(new SaveChestsCommand());
	getCommand("workbench").setExecutor(new WorkbenchCommand());
	getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
	int autosaveInterval = getConfig().getInt("autosave") * 1200;
	if (autosaveInterval > 0)
	{
	    getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
	    {
		public void run()
		{
		    try
		    {
			int savedChests = CompletableFuture.supplyAsync(() -> VirtualChestManager.saveAll()).get();
			if (savedChests > 0 && !getConfig().getBoolean("silentAutosave"))
			{
			    System.out.println("Auto-saved " + savedChests + " chests");
			}
		    }
		    catch (InterruptedException | ExecutionException e)
		    {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
	    }, autosaveInterval, autosaveInterval);
	}
    }

    @Override
    public void onDisable()
    {
	System.out.println("[AlphaChest] Saving inventories!");
	while (true)
	{
	    VirtualChestManager.saveAll();
	    break;
	}
    }

    public Teller getTeller()
    {
	return teller;
    }

    public void setTeller(Teller teller)
    {
	this.teller = teller;
    }
}
