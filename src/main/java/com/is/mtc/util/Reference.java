package com.is.mtc.util;

import net.minecraft.util.text.TextFormatting;

public class Reference {
	public static final String MODID = "is_mtc";
	public static final String MOD_VERSION = "3.1.2";
	public static final String CONFIG_VERSION = "1.1";
	public static final String NAME = "Mine Trading Cards";
	public static final String NAME_COLORIZED = TextFormatting.BLUE + "Mine Trading Cards" + TextFormatting.RESET;

	// Mod compats
	public static final String VILLAGE_NAMES_MODID = "villagenames";

	// Configs
	public static final String FML_GAME_RESTART_TITLE_GUI_ADDRESS = "fml.configgui.gameRestartTitle";
	public static final String FML_GAME_RESTART_REQUIRED_GUI_ADDRESS = "fml.configgui.gameRestartRequired";
	public static final String FML_GAME_CONFIRM_RESTART_GUI_ADDRESS = "fml.configgui.confirmRestartMessage";
	public static final String GUI_FACTORY = "com.is.mtc.gui.MTCGuiFactory";

	// Colors
	// Minecraft chat formatting
	public static final int COLOR_DARK_RED = 0xAA0000; // 4
	public static final int COLOR_RED = 0xff5555; // c
	public static final int COLOR_GOLD = 0xffaa00; // 6
	public static final int COLOR_YELLOW = 0xffff55; // e
	public static final int COLOR_DARK_GREEN = 0x00aa00; // 2
	public static final int COLOR_GREEN = 0x55ff55; // a
	public static final int COLOR_AQUA = 0x55ffff; // b
	public static final int COLOR_DARK_AQUA = 0x00aaaa; // 3
	public static final int COLOR_DEEP_BLUE = 0x0000aa; // 1
	public static final int COLOR_BLUE = 0x5555ff; // 9
	public static final int COLOR_LIGHT_PURPLE = 0xff55ff; // d
	public static final int COLOR_DARK_PURPLE = 0xaa00aa; // 5
	public static final int COLOR_WHITE = 0xffffff; // f
	public static final int COLOR_GRAY = 0xaaaaaa; // 7
	public static final int COLOR_DARK_GRAY = 0x555555; // 8
	public static final int COLOR_BLACK = 0x000000; // 0e
	// Custom
	public static final int COLOR_BROWN = 0xad7030;

	// Drops
	public static final String KEY_CARD_COM = "common_card";
	public static final String KEY_CARD_UNC = "uncommon_card";
	public static final String KEY_CARD_RAR = "rare_card";
	public static final String KEY_CARD_ANC = "ancient_card";
	public static final String KEY_CARD_LEG = "legendary_card";
	public static final String KEY_PACK_COM = "common_pack";
	public static final String KEY_PACK_UNC = "uncommon_pack";
	public static final String KEY_PACK_RAR = "rare_pack";
	public static final String KEY_PACK_ANC = "ancient_pack";
	public static final String KEY_PACK_LEG = "legendary_pack";
	public static final String KEY_PACK_STD = "standard_pack";
	public static final String KEY_PACK_EDT = "edition_pack";
	public static final String KEY_PACK_CUS = "custom_pack";

	// Update checker
	public static final String URL = "https://modrinth.com/mod/mine-trading-cards";
	public static final String VERSION_CHECKER_URL = "https://raw.githubusercontent.com/Powerthegreat/Mine-Trading-Cards-Source-1.12.2/master/CURRENT_VERSION";
}
