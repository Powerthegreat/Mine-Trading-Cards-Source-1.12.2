package com.is.mtc.root;

import net.minecraft.util.EnumChatFormatting;

import com.is.mtc.card.CardItem;

public class Rarity {
	public static final int UNSET = -1;
	public static final int COMMON = 0;
	public static final int UNCOMMON = 1;
	public static final int RARE = 2;
	public static final int ANCIENT = 3;
	public static final int LEGENDARY = 4;
	public static final int RCOUNT = 5;

	private static final String[] STRINGS = { "Common", "Uncommon", "Rare", "Ancient", "Legendary" };
	private static final String[] SHORTSTRINGS = { "com", "unc", "rar", "anc", "leg" };

	private static final String[] STRINGSUPPERCASE = { "COMMON", "UNCOMMON", "RARE", "ANCIENT", "LEGENDARY" };
	private static final String[] SHORTSTRINGSUPPERCASE = { "COM", "UNC", "RAR", "ANC", "LEG" };

	public static String toString(int rarity)
	{
		if (rarity <= UNSET || rarity >= RCOUNT)
			return "???";

		return STRINGS[rarity];
	}

	public static String toShortString(int rarity)
	{
		if (rarity <= UNSET || rarity >= RCOUNT)
			return "???";

		return SHORTSTRINGS[rarity];
	}

	public static int fromString(String string)
	{
		string = string.toUpperCase();

		for (int i = 0; i < RCOUNT; ++i)
		{
			if (STRINGSUPPERCASE[i].equals(string) || SHORTSTRINGSUPPERCASE[i].equals(string))
				return i;
		}

		return UNSET;
	}

	public static CardItem getAssociatedCardItem(int rarity)
	{
		switch (rarity) {
			case COMMON:
				return MineTradingCards.cardCommon;
			case UNCOMMON:
				return MineTradingCards.cardUncommon;
			case RARE:
				return MineTradingCards.cardRare;
			case ANCIENT:
				return MineTradingCards.cardAncient;
			case LEGENDARY:
				return MineTradingCards.cardLegendary;

			default:
				return null;
		}
	}

	public static EnumChatFormatting toColor(int rarity) {
		switch (rarity) {
			case COMMON:
				return EnumChatFormatting.GREEN;
			case UNCOMMON:
				return EnumChatFormatting.GOLD;
			case RARE:
				return EnumChatFormatting.RED;
			case ANCIENT:
				return EnumChatFormatting.AQUA;
			case LEGENDARY:
				return EnumChatFormatting.DARK_PURPLE;

			default:
				return null;
		}
	}
}
