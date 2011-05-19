package com.mast3rplan.alphachest;

import net.minecraft.server.ContainerWorkbench;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ICrafting;

public class AlphaWorkbench extends ContainerWorkbench {

	public AlphaWorkbench(EntityPlayer player, int bI) {
		super(player.inventory, null, 0, 0, 0);
		super.f = bI;
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
