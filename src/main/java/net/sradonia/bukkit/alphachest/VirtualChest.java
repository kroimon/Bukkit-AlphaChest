package net.sradonia.bukkit.alphachest;

import net.minecraft.server.InventoryLargeChest;
import net.minecraft.server.ItemStack;
import net.minecraft.server.TileEntityChest;

public class VirtualChest extends InventoryLargeChest {

	private boolean changed;

	public VirtualChest() {
		super("Virtual Chest", new TileEntityChest(), new TileEntityChest());
		changed = false;
	}

	@Override
	public void setItem(int i, ItemStack itemstack) {
		super.setItem(i, itemstack);
		changed = true;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}
}
