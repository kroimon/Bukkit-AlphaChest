package net.sradonia.bukkit.alphachest;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;

public class VirtualChestManager {
	private final AlphaChestPlugin plugin;
	private final HashMap<String, VirtualChest> chests;
	private final File dataFolder;

	public VirtualChestManager(AlphaChestPlugin plugin, File dataFolder) {
		this.plugin = plugin;
		this.dataFolder = dataFolder;
		this.chests = new HashMap<String, VirtualChest>();
	}

	public VirtualChest getChest(String playerName) {
		VirtualChest chest = chests.get(playerName.toLowerCase());

		if (chest == null) {
			chest = new VirtualChest();
			chests.put(playerName.toLowerCase(), chest);
		}

		return chest;
	}

	public void removeChest(String playerName) {
		// Put a null to the map so we remember to delete the file when saving!
		chests.put(playerName.toLowerCase(), null);
	}

	public int getChestCount() {
		return chests.size();
	}

	public int load() {
		chests.clear();

		dataFolder.mkdirs();
		for (File chestFile : dataFolder.listFiles()) {
			String chestFileName = chestFile.getName();
			try {
				if (chestFileName.endsWith(".chest.nbt")) {
					// New NBT file format
					String playerName = chestFileName.substring(0, chestFile.getName().length() - 10);
					chests.put(playerName.toLowerCase(), loadChestFromNBT(chestFile));
				} else if (chestFileName.endsWith(".chest")) {
					// Old plaintext file format
					String playerName = chestFileName.substring(0, chestFile.getName().length() - 6);
					chests.put(playerName.toLowerCase(), loadChestFromTextfile(chestFile));
				}
			} catch (IOException e) {
				plugin.getLogger().log(Level.WARNING, "Couldn't load chest file: " + chestFileName, e);
			}
		}

		return chests.size();
	}

	private VirtualChest loadChestFromTextfile(File chestFile) throws IOException {
		final VirtualChest chest = new VirtualChest();

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

		chest.setChanged(false);
		return chest;
	}

	private VirtualChest loadChestFromNBT(File chestFile) throws IOException {
		final VirtualChest chest = new VirtualChest();

		final DataInputStream in = new DataInputStream(new GZIPInputStream(new FileInputStream(chestFile)));
		final NBTTagCompound nbt = (NBTTagCompound) NBTBase.b(in);
		in.close();

		final NBTTagList items = nbt.getList("Items");

		int chestSize = chest.getSize();
		for (int i = 0; i < items.size(); i++) {
			NBTTagCompound item = (NBTTagCompound) items.get(i);
			byte slot = item.getByte("Slot");
			if (slot >= 0 && slot < chestSize) {
				ItemStack itemStack = ItemStack.a(item);
				chest.setItem(slot, itemStack);
			}
		}

		chest.setChanged(false);
		return chest;
	}

	public int save(boolean saveAll) {
		int savedChests = 0;

		dataFolder.mkdirs();

		Iterator<Entry<String, VirtualChest>> chestIterator = chests.entrySet().iterator();
		while (chestIterator.hasNext()) {
			final Entry<String, VirtualChest> entry = chestIterator.next();
			final String playerName = entry.getKey();
			final VirtualChest chest = entry.getValue();

			if (chest == null) {
				// Chest got removed, so we have to delete the old file(s).
				new File(dataFolder, playerName + ".chest").delete();
				new File(dataFolder, playerName + ".chest.nbt").delete();
				chestIterator.remove();

			} else if (saveAll || chest.isChanged()) {
				// Delete the old plaintext file if it exists
				new File(dataFolder, playerName + ".chest").delete();

				try {
					// Write the new chest file in NBT format
					final File nbtFile = new File(dataFolder, playerName + ".chest.nbt");
					saveChestToNBT(chest, nbtFile);
					chest.setChanged(false);

					savedChests++;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return savedChests;
	}

	private void saveChestToNBT(VirtualChest chest, File chestFile) throws IOException {
		final DataOutputStream out = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(chestFile)));

		NBTTagList items = new NBTTagList();

		int chestSize = chest.getSize();
		for (int slot = 0; slot < chestSize; slot++) {
			ItemStack stack = chest.getItem(slot);
			if (stack != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte) slot);
				stack.save(item);
				items.add(item);
			}
		}

		final NBTTagCompound nbt = new NBTTagCompound();
		nbt.set("Items", items);

		NBTBase.a(nbt, out);
		out.close();
	}

}
