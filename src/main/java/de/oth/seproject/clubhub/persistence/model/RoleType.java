package de.oth.seproject.clubhub.persistence.model;

public enum RoleType {
    MEMBER("Member of a group in a club"), TRAINER("Trainer of a group in a club"), TEST("This is a test role");

    private final String description;

    RoleType(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
