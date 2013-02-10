package net.sradonia.bukkit.alphachest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.minecraft.server.v1_4_R1.NBTBase;
import net.minecraft.server.v1_4_R1.NBTTagCompound;
import net.minecraft.server.v1_4_R1.NBTTagList;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_4_R1.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * This class contains fragile functions for loading and saving inventories.
 * 
 * WARNING: As this class uses internal CraftBukkit/Minecraft classes that might not be present in the current environment, a
 * {@link NoClassDefFoundError} might be thrown when calling one of the below methods!
 */
public class FragileInventoryIO {

	/**
	 * Loads an inventory from a NBT file.
	 * 
	 * @param file
	 *        the NBT file to load
	 * @return the loaded inventory
	 * @throws IOException
	 *         if the file could not be read
	 */
	public static Inventory loadFromNBT(File file) throws IOException {
		final Inventory inventory = Bukkit.getServer().createInventory(null, 6 * 9);

		final DataInputStream in = new DataInputStream(new GZIPInputStream(new FileInputStream(file)));
		final NBTTagCompound nbt = (NBTTagCompound) NBTBase.b(in);
		in.close();

		final net.minecraft.server.v1_4_R1.NBTTagList items = nbt.getList("Items");

		int inventorySize = inventory.getSize();
		for (int i = 0; i < items.size(); i++) {
			NBTTagCompound item = (NBTTagCompound) items.get(i);
			byte slot = item.getByte("Slot");
			if (slot >= 0 && slot < inventorySize) {
				net.minecraft.server.v1_4_R1.ItemStack nmsStack = net.minecraft.server.v1_4_R1.ItemStack.createStack(item);
				ItemStack itemStack = CraftItemStack.asBukkitCopy(nmsStack);
				inventory.setItem(slot, itemStack);
			}
		}
		return inventory;
	}

	/**
	 * Saves an inventory to NBT format
	 * 
	 * @param inventory
	 *        the inventory to save
	 * @param file
	 *        the NBT file to write
	 * @throws IOException
	 *         if the file could not be written
	 */
	@Deprecated
	public static void saveToNBT(Inventory inventory, File file) throws IOException {
		final DataOutputStream out = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(file)));

		NBTTagList items = new NBTTagList();

		int inventorySize = inventory.getSize();
		for (int slot = 0; slot < inventorySize; slot++) {
			ItemStack stack = inventory.getItem(slot);
			if (stack != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte) slot);
				CraftItemStack.asNMSCopy(stack).save(item);
				items.add(item);
			}
		}

		final NBTTagCompound nbt = new NBTTagCompound();
		nbt.set("Items", items);

		NBTBase.a(nbt, out);
		out.close();
	}

}
