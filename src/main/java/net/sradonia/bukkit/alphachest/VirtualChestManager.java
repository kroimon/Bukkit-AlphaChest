package net.sradonia.bukkit.alphachest;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;

public class VirtualChestManager {
	
	private static final String YAML_CHEST_EXTENSION = ".chest.yml";
	private static final int YAML_EXTENSION_LENGTH = YAML_CHEST_EXTENSION.length();

	private final File dataFolder;
	private final Logger logger;
	private final Map<UUID, Inventory> chests;

	public VirtualChestManager(File dataFolder, Logger logger) {
		this.logger = logger;
		this.dataFolder = dataFolder;

		this.chests = new HashMap<UUID, Inventory>();

		load();
	}

	/**
	 * Loads all existing chests from the data folder.
	 */
	private void load() {
		dataFolder.mkdirs();

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(YAML_CHEST_EXTENSION);
			}
		};
		for (File chestFile : dataFolder.listFiles(filter)) {
			String chestFileName = chestFile.getName();
			try {
				try {
					UUID playerUUID = UUID.fromString(chestFileName.substring(0, chestFileName.length() - YAML_EXTENSION_LENGTH));
					chests.put(playerUUID, InventoryIO.loadFromYaml(chestFile));
				} catch (IllegalArgumentException e){
					// Assume that the filename isn't a UUID, and is therefore an old player-name chest
					String playerName = chestFileName.substring(0, chestFileName.length() - YAML_EXTENSION_LENGTH);
					Boolean flagPlayerNotFound = true;
					for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
						// Search all the known players, load inventory, flag old file for deletion
						if (player.getName().toLowerCase().equals(playerName)) {
							flagPlayerNotFound = false;
							chests.put(player.getUniqueId(), InventoryIO.loadFromYaml(chestFile));
							chestFile.deleteOnExit();
						}
					}
					if (flagPlayerNotFound) {
						logger.log(Level.WARNING, "Couldn't load chest file: " + chestFileName);
					}
				}
			} catch (Exception e) {
				logger.log(Level.WARNING, "Couldn't load chest file: " + chestFileName);
			}
		}

		logger.info("Loaded " + chests.size() + " chests");
	}

	/**
	 * Saves all existing chests to the data folder.
	 * 
	 * @return the number of successfully written chests
	 */
	public int save() {
		int savedChests = 0;

		dataFolder.mkdirs();

		Iterator<Entry<UUID, Inventory>> chestIterator = chests.entrySet().iterator();
		while (chestIterator.hasNext()) {
			final Entry<UUID, Inventory> entry = chestIterator.next();
			final UUID playerUUID = entry.getKey();
			final Inventory chest = entry.getValue();

			final File chestFile = new File(dataFolder, playerUUID.toString() + YAML_CHEST_EXTENSION);
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
					logger.log(Level.WARNING, "Couldn't save chest file: " + chestFile.getName(), e);
				}
			}
		}

		return savedChests;
	}

	/**
	 * Gets a player's virtual chest.
	 * 
	 * @param playerUUID the UUID of the player
	 * @return the player's virtual chest.
	 */
	public Inventory getChest(UUID playerUUID) {
		Inventory chest = chests.get(playerUUID);

		if (chest == null) {
			chest = Bukkit.getServer().createInventory(null, 6 * 9);
			chests.put(playerUUID, chest);
		}

		return chest;
	}

	/**
	 * Clears a player's virtual chest.
	 * 
	 * @param playerUUID the UUID of the player
	 */
	public void removeChest(UUID playerUUID) {
		// Put a null to the map so we remember to delete the file when saving!
		chests.put(playerUUID, null);
	}

	/**
	 * Gets the number of virtual chests.
	 * 
	 * @return the number of virtual chests
	 */
	public int getChestCount() {
		return chests.size();
	}
}
