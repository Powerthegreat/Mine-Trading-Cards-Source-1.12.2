package com.is.mtc.version;

import com.is.mtc.util.Reference;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class DevVersionWarning {
	public static DevVersionWarning instance = new DevVersionWarning();

	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onPlayerTickEvent(PlayerTickEvent event) {
		if (event.player.world.isRemote) {
			event.player.sendMessage(
					new TextComponentString(
							"You're using a "
									+ TextFormatting.RED + "development version" + TextFormatting.RESET + " of " + Reference.NAME + "."
					));

			event.player.sendMessage(
					new TextComponentString(
							TextFormatting.RED + "This version is not meant for public use."
					));

			MinecraftForge.EVENT_BUS.unregister(instance);
		}
	}
}