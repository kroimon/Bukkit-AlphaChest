package net.sradonia.bukkit.alphachest;

import java.io.*;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.minecraft.server.InventoryLargeChest;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.TileEntityChest;

public class AlphaChestManager {
	private static Logger log = Logger.getLogger("Minecraft");

	private final HashMap<String, InventoryLargeChest> chests;
	private final File dataFolder;

	public AlphaChestManager(File dataFolder) {
		this.dataFolder = dataFolder;
		this.chests = new HashMap<String, InventoryLargeChest>();
	}

	public InventoryLargeChest getChest(String playerName) {
		InventoryLargeChest chest = chests.get(playerName.toLowerCase());

		if (chest == null)
			chest = addChest(playerName);

		return chest;
	}

	private InventoryLargeChest addChest(String playerName) {
		InventoryLargeChest chest = new InventoryLargeChest("Virtual Chest", new TileEntityChest(), new TileEntityChest());
		chests.put(playerName.toLowerCase(), chest);
		return chest;
	}

	public void removeChest(String playerName) {
		chests.remove(playerName.toLowerCase());
	}

	public void load() {
		chests.clear();

		dataFolder.mkdirs();
		for (File chestFile : dataFolder.listFiles()) {
			try {
				String chestFileName = chestFile.getName();
				if (chestFileName.endsWith(".chest.nbt")) {
					// Old plaintext file format
					String playerName = chestFileName.substring(0, chestFile.getName().length() - 10);
					chests.put(playerName.toLowerCase(), loadChestFromNBT(chestFile));
				} else if (chestFileName.endsWith(".chest")) {
					// New NBT file format
					String playerName = chestFileName.substring(0, chestFile.getName().length() - 6);
					chests.put(playerName.toLowerCase(), loadChestFromTextfile(chestFile));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		log.info("[AlphaChest] loaded " + chests.size() + " chests");
	}

	private InventoryLargeChest loadChestFromTextfile(File chestFile) throws IOException {
		final InventoryLargeChest chest = new InventoryLargeChest("Virtual Chest", new TileEntityChest(), new TileEntityChest());

		final BufferedReader in = new BufferedReader(new FileReader(chestFile));

		String line;
		int slot = 0;
		while ((line = in.readLine()) != null) {
			if (!line.equals("")) {
				final String[] parts = line.split(":");
				try {
					int type = Integer.parseInt(parts[0]);
					int amount = Integer.parseInt(parts[1]);
					short damage = Short.parseShort(parts[2]);
					if (type != 0) {
						chest.setItem(slot, new ItemStack(type, amount, damage));
					}
				} catch (NumberFormatException e) {
					// ignore
				}
				++slot;
			}
		}

		in.close();

		return chest;
	}

	private InventoryLargeChest loadChestFromNBT(File chestFile) throws IOException {
		final InventoryLargeChest chest = new InventoryLargeChest("Virtual Chest", new TileEntityChest(), new TileEntityChest());

		final DataInputStream in = new DataInputStream(new GZIPInputStream(new FileInputStream(chestFile)));
		final NBTTagCompound nbt = (NBTTagCompound) NBTBase.b(in);
		in.close();

		final NBTTagList items = nbt.getList("Items");

		int chestSize = chest.getSize();
		for (int i = 0; i < items.size(); i++) {
			NBTTagCompound item = (NBTTagCompound) items.get(i);
			byte slot = item.getByte("Slot");
			if (slot > 0 && slot < chestSize) {
				ItemStack itemStack = ItemStack.a(item);
				chest.setItem(slot, itemStack);
			}
		}

		return chest;
	}

	public void save() {
		int savedChests = 0;

		dataFolder.mkdirs();

		for (Entry<String, InventoryLargeChest> entry : chests.entrySet()) {
			final String playerName = entry.getKey();
			final InventoryLargeChest chest = entry.getValue();

			try {
				// Delete the old plaintext file if it exists
				final File textFile = new File(dataFolder, playerName + ".chest");
				textFile.delete();

				// Write the new chest file in NBT format
				final File nbtFile = new File(dataFolder, playerName + ".chest.nbt");
				saveChestToNBT(chest, nbtFile);

				savedChests++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		log.info("[AlphaChest] saved " + savedChests + " chests");
	}

	private void saveChestToNBT(InventoryLargeChest chest, File chestFile) throws IOException {
		final DataOutputStream out = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(chestFile)));

		NBTTagList items = new NBTTagList();

		int chestSize = chest.getSize();
		for (int slot = 0; slot < chestSize; slot++) {
			ItemStack stack = chest.getItem(slot);
			if (stack != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte) slot);
				stack.b(item);
				items.add(item);
			}
		}

		final NBTTagCompound nbt = new NBTTagCompound();
		nbt.set("Items", items);

		NBTBase.a(nbt, out);
		out.close();
	}

}
