package org.overlord.rtgov.ui.client.shared.beans;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ResolutionState {
	ANY("Any"), NULL(null), UNRESOLVED("Unresolved"), RESOLVED("Resolved"), IN_PROGRESS("In Progress"), WAITING(
			"Waiting"), REOPENED("Reopened");

	private String name;
	private static final Map<String, ResolutionState> nameToResolutionState = new HashMap<String, ResolutionState>();

	private ResolutionState(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	static {
		for (ResolutionState s : EnumSet.allOf(ResolutionState.class))
			nameToResolutionState.put(s.name, s);
	}

	public static ResolutionState get(String name) {
		return nameToResolutionState.get(name);
	}

}
