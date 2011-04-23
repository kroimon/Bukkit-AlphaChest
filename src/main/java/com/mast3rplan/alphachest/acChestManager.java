package com.mast3rplan.alphachest;

import com.mast3rplan.alphachest.acChest;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
		InventoryLargeChest chest = null;

		chest = (InventoryLargeChest) chests.get(playerName.toLowerCase());

		if (chest == null) {
			chest = this.addChest(playerName);
		}

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

		File chestsFolder = new File(this.dataFolder, "chests");
		if (!chestsFolder.exists()) {
			chestsFolder.mkdir();
		}

		File[] chestFiles;
		int chestCount = (chestFiles = chestsFolder.listFiles()).length;

		for (int i = 0; i < chestCount; ++i) {
			File chestFile = chestFiles[i];
			if (chestFile.getName().endsWith(".chest")) {
				try {
					InventoryLargeChest e = new InventoryLargeChest("Large chest", new acChest(), new acChest());
					String playerName = chestFile.getName().substring(0, chestFile.getName().length() - 6);
					FileInputStream fstream = new FileInputStream(chestFile);
					DataInputStream in = new DataInputStream(fstream);
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					int p = 0;

					String strLine;
					while ((strLine = br.readLine()) != null) {
						if (strLine != "") {
							String[] parts = strLine.split(":");
							int type = Integer.parseInt(parts[0]);
							int amount = Integer.parseInt(parts[1]);
							short damage = Short.parseShort(parts[2]);
							if (type != 0) {
								e.setItem(p, new ItemStack(type, amount, damage));
							}

							++p;
						}
					}

					in.close();
					chests.put(playerName.toLowerCase(), e);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				++loadedChests;
			}
		}

		log.info("[Alpha Chest] loaded " + loadedChests + " chests");
	}

	public void save() {
		int savedChests = 0;
		File chestsFolder = new File(this.dataFolder, "chests");
		if (!chestsFolder.exists()) {
			chestsFolder.mkdir();
		}

		for (String playerName : chests.keySet()) {
			InventoryLargeChest chest = (InventoryLargeChest) chests.get(playerName);

			try {
				File chestFile = new File(chestsFolder, playerName + ".chest");
				if (chestFile.exists())
					chestFile.delete();

				chestFile.createNewFile();
				DataOutputStream out = new DataOutputStream(new FileOutputStream(chestFile));
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
				ItemStack[] itemStacks = chest.getContents();

				for (int i = 0; i < itemStacks.length; ++i) {
					ItemStack stack = itemStacks[i];
					int type = 0;
					int amount = 0;
					int damage = -1;

					type = stack.id;
					amount = stack.count;
					damage = stack.damage;

					bw.write(type + ":" + amount + ":" + damage + "\r\n");
				}

				bw.flush();
				out.close();

				savedChests++;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		log.info("[Alpha Chest] saved " + savedChests + " chests");
	}
}
