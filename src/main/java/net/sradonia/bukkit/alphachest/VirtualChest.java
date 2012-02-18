package net.sradonia.bukkit.alphachest;

import net.minecraft.server.InventoryLargeChest;
import net.minecraft.server.TileEntityChest;

public class VirtualChest extends InventoryLargeChest {

	public VirtualChest() {
		super("Virtual Chest", new TileEntityChest(), new TileEntityChest());
	}

}
