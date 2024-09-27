package com.is.mtc.root;

import com.is.mtc.card.CardItem;
import net.minecraft.item.ItemStack;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {
	private static Pattern pattern = Pattern.compile("^[a-z0-9_]*$");

	// -
	public static String clean(String string) {
		if (string == null)
			return "";

		string = string.replaceAll(" +", " ");
		string = string.replace('\t', ' ');
		string = string.trim();

		return string;
	}

	public static boolean isValidID(String string) {
		Matcher matcher;

		if (string == null || string.isEmpty())
			return false;
		matcher = pattern.matcher(string.toLowerCase());

		return matcher.find();
	}

	public static int randInt(int min, int max, Random random) {

		return random.nextInt(max - min) + min;
	}

	public static String generateString(int length, Random random) {
		char[] text = new char[length];
		String characters = "abcdefghijklmnopqrstuvwxyz0123456789";

		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(random.nextInt(characters.length()));
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
		return stack != ItemStack.EMPTY && stack.getTag() != null && stack.getTag().get("cdwd") != null;
	}
}
