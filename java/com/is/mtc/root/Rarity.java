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

	private static final String[] STR = { "Common", "Uncommon", "Rare", "Ancient", "Legendary" };
	private static final String[] SSTR = { "com", "unc", "rar", "anc", "leg" };

	private static final String[] STRU = { "COMMON", "UNCOMMON", "RARE", "ANCIENT", "LEGENDARY" };
	private static final String[] SSTRU = { "COM", "UNC", "RAR", "ANC", "LEG" };

	public static String toString(int r)
	{
		if (r <= UNSET || r >= RCOUNT)
			return "???";

		return STR[r];
	}

	public static String toShortString(int r)
	{
		if (r <= UNSET || r >= RCOUNT)
			return "???";

		return SSTR[r];
	}

	public static int fromString(String s)
	{
		s = s.toUpperCase();

		for (int i = 0; i < RCOUNT; ++i)
		{
			if (STRU[i].equals(s) || SSTRU[i].equals(s))
				return i;
		}

		return UNSET;
	}

	public static CardItem getAssociatedCardItem(int r)
	{
		switch (r) {
		case COMMON:
			return MineTradingCards.cc;
		case UNCOMMON:
			return MineTradingCards.cu;
		case RARE:
			return MineTradingCards.cr;
		case ANCIENT:
			return MineTradingCards.ca;
		case LEGENDARY:
			return MineTradingCards.cl;

		default:
			return null;
		}
	}

	public static EnumChatFormatting toColor(int r) {
		switch (r) {
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
