package de.oth.seproject.clubhub.persistence.model;

public enum EventType {

    TRAINING("A training"), MATCH("A match"), CHRISTMAS_PARTY("Vollgas!");

    private final String description;

    EventType(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
