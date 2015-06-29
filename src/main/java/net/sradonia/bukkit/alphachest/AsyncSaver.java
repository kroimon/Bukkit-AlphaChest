package net.sradonia.bukkit.alphachest;

import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by axel on 6/28/15.
 */
public class AsyncSaver implements Runnable {
    int savedChests = 0;
    File dataFolder;
    Iterator<Map.Entry<UUID, Inventory>> chestIterator;

    public AsyncSaver(File dataFolder, Iterator<Map.Entry<UUID, Inventory>> chestIterator){
        this.dataFolder = dataFolder;
        this.chestIterator = chestIterator;
    }
    @Override
    public void run() {
        while (chestIterator.hasNext()) {
            final Map.Entry<UUID, Inventory> entry = chestIterator.next();
            final UUID playerUUID = entry.getKey();
            final Inventory chest = entry.getValue();

            final File chestFile = new File(dataFolder, playerUUID.toString() + ".chest.yml");

            if (chest == null) {
                // Chest got removed, so we have to delete the file.
                chestFile.delete();
                chestIterator.remove();
            } else {
                try {
                    // Write the chest file in YAML format
                    InventoryIO.saveToYaml(chest, chestFile);

                    savedChests++;
                } catch (IOException e) {
                    System.out.println("Couldn't save chest file: " + chestFile.getName());
                }
            }
        }
    }

    public int getSavedChests(){
        return savedChests;
    }
}
