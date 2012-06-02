package net.sradonia.bukkit.alphachest;

import net.minecraft.server.InventoryLargeChest;
import net.minecraft.server.ItemStack;
import net.minecraft.server.TileEntityChest;

public class VirtualChest extends InventoryLargeChest {

	private boolean changed;

	public VirtualChest() {
		super("VIP Batoh", new TileEntityChest(), new TileEntityChest());
		changed = false;
	}

	@Override
	public void setItem(int i, ItemStack itemstack) {
		super.setItem(i, itemstack);
		changed = true;
	}

	@Override
	public ItemStack splitStack(int i, int j) {
		changed = true;
		return super.splitStack(i, j);
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}
}
