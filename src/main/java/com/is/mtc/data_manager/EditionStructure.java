package com.is.mtc.data_manager;

import com.google.gson.JsonElement;
import com.is.mtc.root.Tools;
import com.is.mtc.util.Functions;

public class EditionStructure {
	public int cCount, eNI; // Cards count, edition numeral id
	private String name, id;
	private int color;

	public EditionStructure(JsonElement jsonId, JsonElement jsonName, JsonElement jsonColor) {
		setInput(jsonId != null ? jsonId.getAsString() : null, jsonName != null ? jsonName.getAsString() : null, jsonColor != null ? jsonColor.getAsString() : null);
	}

	public EditionStructure(String id, String name, String color) {
		setInput(id, name, color);
	}

	private void setInput(String id, String name, String color) {
		this.id = Tools.clean(id).toLowerCase();
		this.name = Tools.clean(name);
		this.color = Functions.parseColorInteger(Tools.clean(color), Functions.string_to_color_code(id));

		if (!Tools.isValidID(this.id)) {
			this.id = "";
		}

		cCount = 0;
		eNI = -1;
	}

	public boolean isValid() {
		return !(id.isEmpty() || name.isEmpty());
	}

	@Override
	public String toString() {
		return "{id:" + id + ", name:'" + name + "', color:" + color + ", cards_count:" + cCount + ", numeral_id:" + eNI + "}";
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
