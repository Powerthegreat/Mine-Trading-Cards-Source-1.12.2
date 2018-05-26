package com.is.mtc.root;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.item.ItemStack;

import com.is.mtc.card.CardItem;

public class Tools {

	public static final int SIDE_BOTTOM = 0;
	public static final int SIDE_TOP = 1;

	public static final int SIDE_NORTH = 2;
	public static final int SIDE_SOUTH = 3;

	public static final int SIDE_WEST = 4;
	public static final int SIDE_EAST = 5;

	// -
	public static String clean(String str) {
		if (str == null)
			return "";

		str = str.replaceAll(" +", " ");
		str = str.replace('\t', ' ');
		str = str.trim();

		return str;
	}

	private static Pattern pattern = Pattern.compile("^[a-z0-9_]*$");
	public static boolean isValidID(String str) {
		Matcher matcher;

		if (str == null || str.isEmpty())
			return false;
		matcher = pattern.matcher(str.toLowerCase());

		return matcher.find();
	}

	public static int randInt(int min, int max) {
		Random r = new Random();

		return r.nextInt(max - min) + min;
	}

	public static String generateString(int length) {
		Random r = new Random();
		char[] text = new char[length];
		String characters = "abcdefghijklmnopqrstuvwxyz0123456789";

		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(r.nextInt(characters.length()));
		}

		return new String(text);
	}

	public static float clamp(float min, float value, float max) {
		return value < min ? min : value > max ? max : value;
	}

	public static boolean isValidCard(ItemStack stack) {
		return stack != null && stack.getItem() instanceof CardItem && hasCDWD(stack);
	}

	public static boolean hasCDWD(ItemStack stack) {
		if (stack == null || !stack.hasTagCompound() || !stack.stackTagCompound.hasKey("cdwd"))
			return false;

		return true;
	}
}
