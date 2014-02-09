package org.overlord.rtgov.ui.client.shared.beans;

public enum ResolutionState {
    ANY("Any"), NULL("null"), OPEN("Open"), RESOLVED("Resolved"), CLOSED("Closed"), IN_PROGRESS("In Progress"), REOPENED(
            "Reopened");

    private String name;

    private ResolutionState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
