package net.sradonia.bukkit.alphachest;

import net.minecraft.server.ContainerWorkbench;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;

public class VirtualWorkbench extends ContainerWorkbench {

	public VirtualWorkbench(EntityPlayer player, int windowId) {
		super(player.inventory, player.world, 0, 0, 0);
		super.windowId = windowId;
		super.addSlotListener(player);
	}

	/**
	 * This method is called to check that the player is still next to the workbench. He always is.
	 */
	@Override
	public boolean b(EntityHuman entityhuman) {
		return true;
	}
}
