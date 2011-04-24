package com.mast3rplan.alphachest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;
import net.minecraft.server.InventoryLargeChest;
import net.minecraft.server.ItemStack;

public class acChestManager {
	private static Logger log = Logger.getLogger("Minecraft");

	private HashMap<String, InventoryLargeChest> chests = new HashMap<String, InventoryLargeChest>();
	private File dataFolder;

	public acChestManager(File dataFolder) {
		this.dataFolder = dataFolder;
	}

	public InventoryLargeChest getChest(String playerName) {
		InventoryLargeChest chest = chests.get(playerName.toLowerCase());

		if (chest == null)
			chest = addChest(playerName);

		return chest;
	}

	public void removeChest(String playerName) {
		chests.remove(playerName.toLowerCase());
	}

	public InventoryLargeChest addChest(String playerName) {
		InventoryLargeChest chest = new InventoryLargeChest(playerName + "\'s Virtual Chest", new acChest(), new acChest());
		chests.put(playerName.toLowerCase(), chest);
		return chest;
	}

	public void load() {
		chests = new HashMap<String, InventoryLargeChest>();

		int loadedChests = 0;

		File chestsFolder = new File(dataFolder, "chests");
		if (!chestsFolder.exists())
			chestsFolder.mkdir();

		for (File chestFile : chestsFolder.listFiles()) {
			if (chestFile.getName().endsWith(".chest")) {
				try {
					InventoryLargeChest chest = new InventoryLargeChest("Large chest", new acChest(), new acChest());
					String playerName = chestFile.getName().substring(0, chestFile.getName().length() - 6);

					BufferedReader in = new BufferedReader(new FileReader(chestFile));
					int p = 0;

					String strLine;
					while ((strLine = in.readLine()) != null) {
						if (strLine != "") {
							String[] parts = strLine.split(":");
							try {
								int type = Integer.parseInt(parts[0]);
								int amount = Integer.parseInt(parts[1]);
								short damage = Short.parseShort(parts[2]);
								if (type != 0) {
									chest.setItem(p, new ItemStack(type, amount, damage));
								}
	
								++p;
							} catch (NumberFormatException e) {
								// ignore
							}
						}
					}

					in.close();
					chests.put(playerName.toLowerCase(), chest);

					++loadedChests;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		log.info("[Alpha Chest] loaded " + loadedChests + " chests");
	}

	public void save() {
		int savedChests = 0;

		File chestsFolder = new File(dataFolder, "chests");
		chestsFolder.mkdirs();

		for (String playerName : chests.keySet()) {
			InventoryLargeChest chest = chests.get(playerName);

			try {
				File chestFile = new File(chestsFolder, playerName + ".chest");
				if (chestFile.exists())
					chestFile.delete();
				chestFile.createNewFile();

				BufferedWriter out = new BufferedWriter(new FileWriter(chestFile));

				for (ItemStack stack : chest.getContents()) {
					int type = 0;
					int amount = 0;
					int damage = -1;

					type = stack.id;
					amount = stack.count;
					damage = stack.damage;

					out.write(type + ":" + amount + ":" + damage + "\r\n");
				}

				out.close();

				savedChests++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		log.info("[Alpha Chest] saved " + savedChests + " chests");
	}
}
