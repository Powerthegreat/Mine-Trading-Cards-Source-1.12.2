package com.is.mtc.data_manager;

import com.google.gson.JsonElement;
import com.is.mtc.root.Tools;

import java.util.LinkedHashMap;
import java.util.Map;

public class CustomPackStructure {
	private String name, id;
	public Map<String, int[]> categoryQuantities;
	// Category, quantity, rarity
	public int customPackNumeralID;

	public CustomPackStructure(JsonElement jsonId, JsonElement jsonName) {
		setInput(jsonId != null ? jsonId.getAsString() : null, jsonName != null ? jsonName.getAsString() : null);

		categoryQuantities = new LinkedHashMap<String, int[]>();
	}

	public CustomPackStructure(String id, String name) {
		setInput(id, name);
	}

	private void setInput(String id, String name) {
		this.id = Tools.clean(id).toLowerCase();
		this.name = Tools.clean(name);

		if (!Tools.isValidID(this.id))
			this.id = "";

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
		return "{id:" + id + " name:'" + name + "' numeral_id:" + customPackNumeralID + "}";
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
