package com.is.mtc.root;

import com.is.mtc.MineTradingCards;
import com.is.mtc.card.CardItem;
import com.is.mtc.init.MTCItems;

import net.minecraft.util.text.TextFormatting;

public class Rarity {
	public static final int UNSET = -1;
	public static final int COMMON = 0;
	public static final int UNCOMMON = 1;
	public static final int RARE = 2;
	public static final int ANCIENT = 3;
	public static final int LEGENDARY = 4;
	public static final int RCOUNT = 5;

	private static final String[] STRINGS = {"Common", "Uncommon", "Rare", "Ancient", "Legendary"};
	private static final String[] SHORTSTRINGS = {"com", "unc", "rar", "anc", "leg"};

	private static final String[] STRINGSUPPERCASE = {"COMMON", "UNCOMMON", "RARE", "ANCIENT", "LEGENDARY"};
	private static final String[] SHORTSTRINGSUPPERCASE = {"COM", "UNC", "RAR", "ANC", "LEG"};

	public static String toString(int rarity) {
		if (rarity <= UNSET || rarity >= RCOUNT)
			return "???";

		return STRINGS[rarity];
	}

	public static String toShortString(int rarity) {
		if (rarity <= UNSET || rarity >= RCOUNT)
			return "???";

		return SHORTSTRINGS[rarity];
	}

	public static int fromString(String string) {
		string = string.toUpperCase();

		for (int i = 0; i < RCOUNT; ++i) {
			if (STRINGSUPPERCASE[i].equals(string) || SHORTSTRINGSUPPERCASE[i].equals(string)) {
				return i;
			}
		}

		return UNSET;
	}

	public static CardItem getAssociatedCardItem(int rarity) {
		switch (rarity) {
			case COMMON:
				return MTCItems.cardCommon;
			case UNCOMMON:
				return MTCItems.cardUncommon;
			case RARE:
				return MTCItems.cardRare;
			case ANCIENT:
				return MTCItems.cardAncient;
			case LEGENDARY:
				return MTCItems.cardLegendary;

			default:
				return null;
		}
	}

	public static TextFormatting toColor(int rarity) {
		TextFormatting tf;
		switch (rarity) {
			case COMMON:
				tf = TextFormatting.getValueByName(MineTradingCards.CARD_TOOLTIP_COLOR_COMMON);
				return tf!=null ? tf : TextFormatting.GREEN;
			case UNCOMMON:
				tf = TextFormatting.getValueByName(MineTradingCards.CARD_TOOLTIP_COLOR_UNCOMMON);
				return tf!=null ? tf : TextFormatting.GOLD;
			case RARE:
				tf = TextFormatting.getValueByName(MineTradingCards.CARD_TOOLTIP_COLOR_RARE);
				return tf!=null ? tf : TextFormatting.RED;
			case ANCIENT:
				tf = TextFormatting.getValueByName(MineTradingCards.CARD_TOOLTIP_COLOR_ANCIENT);
				return tf!=null ? tf : TextFormatting.AQUA;
			case LEGENDARY:
				tf = TextFormatting.getValueByName(MineTradingCards.CARD_TOOLTIP_COLOR_LEGENDARY);
				return tf!=null ? tf : TextFormatting.DARK_PURPLE;
	
			default:
				return null;
		}
	}
}
