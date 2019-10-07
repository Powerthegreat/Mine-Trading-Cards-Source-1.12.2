package com.is.mtc.root;

import com.is.mtc.card.CardItem;
import net.minecraft.item.ItemStack;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {

	/*public static final int SIDE_BOTTOM = 0;
	public static final int SIDE_TOP = 1;

	public static final int SIDE_NORTH = 2;
	public static final int SIDE_SOUTH = 3;

	public static final int SIDE_WEST = 4;
	public static final int SIDE_EAST = 5;*/

	// -
	public static String clean(String string) {
		if (string == null)
			return "";

		string = string.replaceAll(" +", " ");
		string = string.replace('\t', ' ');
		string = string.trim();

		return string;
	}

	private static Pattern pattern = Pattern.compile("^[a-z0-9_]*$");

	public static boolean isValidID(String string) {
		Matcher matcher;

		if (string == null || string.isEmpty())
			return false;
		matcher = pattern.matcher(string.toLowerCase());

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
		return value < min ? min : Math.min(value, max);
	}

	public static boolean isValidCard(ItemStack stack) {
		return stack != ItemStack.EMPTY && stack.getItem() instanceof CardItem && hasCDWD(stack);
	}

	public static boolean hasCDWD(ItemStack stack) {
		return stack != ItemStack.EMPTY && stack.hasTagCompound() && stack.getTagCompound().hasKey("cdwd");
	}
}
