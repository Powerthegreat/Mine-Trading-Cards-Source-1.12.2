package com.is.mtc.data_manager;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.is.mtc.root.Tools;
import com.is.mtc.util.Functions;

public class CustomPackStructure {
	private String name, id;
	private int color;
	public Map<String, int[]> categoryQuantities;
	// Category, quantity, rarity
	public int customPackNumeralID;

	public CustomPackStructure(JsonElement jsonId, JsonElement jsonName, JsonElement jsonColor) {
		setInput(jsonId != null ? jsonId.getAsString() : null, jsonName != null ? jsonName.getAsString() : null, jsonColor != null ? jsonColor.getAsString() : null);

		categoryQuantities = new LinkedHashMap<String, int[]>();
	}

	public CustomPackStructure(String id, String name, String color) {
		setInput(id, name, color);
	}

	private void setInput(String id, String name, String color) {
		this.id = Tools.clean(id).toLowerCase();
		this.name = Tools.clean(name);
		this.color = Functions.parseColorInteger(Tools.clean(color), Functions.string_to_color_code(id));
		
		if (!Tools.isValidID(this.id)) {
			this.id = "";
		}
		
		customPackNumeralID = -1;
	}

	public boolean addCategoryQuantity(String category, int quantity, int rarity) {
		if (categoryQuantities.containsKey(category)) {
			return false;
		}

		categoryQuantities.put(category, new int[]{quantity, rarity});

		return true;
	}

	public boolean isValid() {
		return !(id.isEmpty() || name.isEmpty());
	}

	@Override
	public String toString() {
		return "{id:" + id + " name:'" + name + "' numeral_id:" + customPackNumeralID  + ", color:" + color + "}";
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getColor() {
		return color;
	}
}
