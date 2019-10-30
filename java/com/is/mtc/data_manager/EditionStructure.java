package com.is.mtc.data_manager;

import com.google.gson.JsonElement;
import com.is.mtc.root.Tools;

public class EditionStructure {
	private String name, id;
	public int cCount, eNI; // Cards count, edition numeral id

	public EditionStructure(JsonElement jsonId, JsonElement jsonName) {
		setInput(jsonId != null ? jsonId.getAsString() : null, jsonName != null ? jsonName.getAsString() : null);
	}

	public EditionStructure(String id, String name) {
		setInput(id, name);
	}

	private void setInput(String id, String name) {
		this.id = Tools.clean(id).toLowerCase();
		this.name = Tools.clean(name);

		if (!Tools.isValidID(this.id))
			this.id = "";

		cCount = 0;
		eNI = -1;
	}

	public boolean isValid() {
		return !(id.isEmpty() || name.isEmpty());
	}

	@Override
	public String toString() {
		return "{id:" + id + " name:'" + name + "' cards_count:" + cCount + " numeral_id:" + eNI + "}";
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
