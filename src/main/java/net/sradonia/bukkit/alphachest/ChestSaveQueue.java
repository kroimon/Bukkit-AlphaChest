package net.sradonia.bukkit.alphachest;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map.Entry;

public class ChestSaveQueue extends BukkitRunnable
{
    public File dataFolder;
    private static final String ymlEx = ".chest.yml";

    public ChestSaveQueue(File dataFolder)
    {
	this.dataFolder = dataFolder;
    }

    private static BlockingQueue<Entry<UUID, Inventory>> queue = new ArrayBlockingQueue(500);
    int maximumIterations = 50;

    public void saveChests()
    {
	int tracker = 0;
	for (Iterator<Entry<UUID, Inventory>> iter = queue.iterator(); iter.hasNext();)
	{
	    if (tracker > 50)
	    {
		break;
	    }
	    
	    tracker++;
	}
    }

    public void saveChest(UUID playerUUID, Inventory chest)
    {
	final File chestFile = new File(dataFolder + "/chests/", playerUUID + ymlEx);
	try
	{
	    InventoryIO.saveToYaml(chest, chestFile);
	}
	catch (IOException e)
	{
	    System.out.println("Couldn't save chest file: " + chestFile.getName());
	}
    }

    @Override
    public void run()
    {

    }
}
