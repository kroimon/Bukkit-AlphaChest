package net.sradonia.bukkit.alphachest.core;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;

import net.sradonia.bukkit.alphachest.AlphaChest;

public class VirtualChestManager {

    private final AlphaChest plugin;

    private final String YAML_CHEST_EXTENSION = ".chest.yml";
    private final int YAML_EXTENSION_LENGTH = YAML_CHEST_EXTENSION.length();

    private final File chestsFolder;

    private final Map<UUID, Inventory> chests = new HashMap<>();

    public VirtualChestManager(AlphaChest plugin) {
        this.plugin = plugin;

        chestsFolder = new File(plugin.getDataFolder(), "chests");

        load();
    }

    /**
     * Loads all existing chests from the data folder.
     */
    private void load() {
        chestsFolder.mkdirs();

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(YAML_CHEST_EXTENSION);
            }
        };

        for (File chestFile : chestsFolder.listFiles(filter)) {
            String fileName = chestFile.getName();

            try {
                try {
                    UUID uuid = UUID.fromString(fileName.substring(0, fileName.length() - YAML_EXTENSION_LENGTH));
                    chests.put(uuid, InventoryIO.loadFromYaml(chestFile));
                } catch (IllegalArgumentException e) {
                    // Assume that the filename isn't a UUID, and is therefore an old player-name chest
                    String playerName = fileName.substring(0, fileName.length() - YAML_EXTENSION_LENGTH);
                    boolean flagPlayerNotFound = true;

                    for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                        // Search all the known players, load inventory, flag old file for deletion
                        if (player.getName().equalsIgnoreCase(playerName)) {
                            flagPlayerNotFound = false;
                            chests.put(player.getUniqueId(), InventoryIO.loadFromYaml(chestFile));
                            chestFile.deleteOnExit();
                        }
                    }

                    if (flagPlayerNotFound) {
                        plugin.getLogger().log(Level.WARNING, "Couldn't load chest file: " + fileName);
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Couldn't load chest file: " + fileName);
            }
        }

        plugin.getLogger().info("Loaded " + chests.size() + " chests.");
    }

    /**
     * Saves all existing chests to the data folder.
     *
     * @return the number of successfully written chests
     */
    public int save() {
        int savedChests = 0;

        chestsFolder.mkdirs();

        Iterator<Entry<UUID, Inventory>> chestIterator = chests.entrySet().iterator();

        while (chestIterator.hasNext()) {
            Entry<UUID, Inventory> entry = chestIterator.next();
            UUID uuid = entry.getKey();
            Inventory chest = entry.getValue();

            File chestFile = new File(chestsFolder, uuid.toString() + YAML_CHEST_EXTENSION);

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
                    plugin.getLogger().log(Level.WARNING, "Couldn't save chest file: " + chestFile.getName(), e);
                }
            }
        }

        return savedChests;
    }

    /**
     * Saves a specified player's chest to the data folder.
     *
     * @param uuid the UUID of the player to save the chest of
     */
    public void saveChest(UUID uuid) {
        chestsFolder.mkdirs();

        Inventory chest = chests.get(uuid);
        File chestFile = new File(chestsFolder, uuid.toString() + YAML_CHEST_EXTENSION);

        if (chest == null) {
            // Chest got removed, so we have to delete the file.
            chestFile.delete();
        } else {
            try {
                // Write the chest file in YAML format
                InventoryIO.saveToYaml(chest, chestFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Couldn't save chest file: " + chestFile.getName(), e);
            }
        }
    }

    /**
     * Returns a player's virtual chest.
     *
     * @param uuid the UUID of the player to get the chest of
     * @return the player's virtual chest
     */
    public Inventory getChest(UUID uuid) {
        Inventory chest = chests.get(uuid);

        if (chest == null) {
            chest = Bukkit.getServer().createInventory(null, 6 * 9);
            chests.put(uuid, chest);
        }

        return chest;
    }

    /**
     * Clears a player's virtual chest.
     *
     * @param uuid the UUID of the player to clear the chest of
     */
    public void removeChest(UUID uuid) {
        // Put a null to the map so we remember to delete the file when saving!
        chests.put(uuid, null);
    }

    /**
     * Returns the number of virtual chests.
     *
     * @return the number of virtual chests
     */
    public int getChestCount() {
        return chests.size();
    }
}
