package net.sradonia.bukkit.alphachest;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
public class VirtualChestManager
{
    private static final String ymlEx = ".chest.yml";
    private static File dataFolder;
    public static Map<UUID, Inventory> chests = new ConcurrentHashMap<>();

    public static void initChestMan(File dataFolder)
    {
	VirtualChestManager.dataFolder = dataFolder;
    }

    public static void addChest(Player p, Inventory i)
    {
	chests.put(p.getUniqueId(), i);
    }

    public static void addChest(UUID p, Inventory i)
    {
	chests.put(p, i);
    }

    public static void saveChest(UUID playerUUID)
    {
	final String uuidString = playerUUID.toString();
	final Inventory chest = chests.get(playerUUID);
	if (chest != null)
	{
	    File chestFile = new File(dataFolder, uuidString + ymlEx);
	    try
	    {
		InventoryIO.saveToYaml(chest, chestFile);
	    }
	    catch (IOException e)
	    {
		System.out.println("Couldn't save chest file: " + chestFile.getName());
	    }
	}
    }

    // Never run this pl0x
    public static int saveAll()
    {
	for (Entry<UUID, Inventory> chest : chests.entrySet())
	{
	    try
	    {
		File chestFile = new File(dataFolder, chest.getKey() + ymlEx);
		InventoryIO.saveToYaml(chest.getValue(), chestFile);
	    }
	    catch (Exception e)
	    {
		System.out.println("Couldn't save chest file: " + chest.getKey());
		continue;
	    }
	}
	return chests.size();
    }

    public static void removeEntry(Player p)
    {
	chests.remove(p.getUniqueId());
    }

    public static void removeChest(Player p)
    {
	chests.put(p.getUniqueId(), Bukkit.createInventory(null, 36));
    }

    public static void removeChest(UUID uuid)
    {

    }

    public static void setChest(Player p)
    {
	Inventory chest = chests.get(p.getUniqueId());
	if (chest == null)
	{
	    try
	    {
		File chestFile = new File(dataFolder, p.getUniqueId() + ymlEx);
		if (chestFile.exists())
		{
		    chests.put(p.getUniqueId(), InventoryIO.loadFromYaml(chestFile));
		}
		else
		{
		    Inventory newInv = Bukkit.createInventory(null, 36);
		    chests.put(p.getUniqueId(), newInv);
		    InventoryIO.saveToYaml(newInv, chestFile);
		}
	    }
	    catch (Throwable t)
	    {
		t.printStackTrace();
	    }
	}
    }

    public static Inventory getChest(UUID uuid)
    {
	Inventory chest = chests.get(uuid);
	if (chest == null)
	{
	    try
	    {
		File chestFile = new File(dataFolder, uuid + ymlEx);
		if (chestFile.exists())
		{
		    chests.put(uuid, InventoryIO.loadFromYaml(chestFile));
		}
		else
		{
		    chestFile.createNewFile();
		}
	    }
	    catch (Throwable t)
	    {
		t.printStackTrace();
	    }
	}
	return chest;
    }

}
