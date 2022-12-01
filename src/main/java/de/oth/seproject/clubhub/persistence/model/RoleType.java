package de.oth.seproject.clubhub.persistence.model;

public enum RoleType {
    MEMBER("A member"), TRAINER("A trainer"), TEST("This is a test role");

    private final String description;

    RoleType(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
