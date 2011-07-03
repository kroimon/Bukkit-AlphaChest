package com.mast3rplan.alphachest;

import net.minecraft.server.ContainerWorkbench;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ICrafting;

public class AlphaWorkbench extends ContainerWorkbench {

	public AlphaWorkbench(EntityPlayer player, int windowId) {
		super(player.inventory, player.world, 0, 0, 0);
        super.windowId = windowId;
		super.a((ICrafting) player);
	}

	/**
	 * This method is called to check that the player is still next to the workbench. He always is.
	 */
	@Override
	public boolean b(EntityHuman entityhuman) {
		return true;
	}
}
