package com.is.mtc.root;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class Logs {
	public static boolean ENABLE_DEV_LOGS = false;

	public static void stdLog(String message) {
		System.out.println("[MTC_MSG]: " + message);
	}

	public static void errLog(String message) {
		System.out.println("[MTC_ERR]: " + message);
	}

	public static void devLog(String message) {
		if (ENABLE_DEV_LOGS)
			System.out.println("[MTC_DEV]: " + message);
	}

	public static void chatMessage(PlayerEntity player, String message) {
		player.sendMessage(new StringTextComponent(message), player.getUUID());
	}
}
