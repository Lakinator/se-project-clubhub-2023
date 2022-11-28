package de.oth.seproject.clubhub.persistence.model;

import javax.persistence.*;

@Entity
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "club_id")
    private Club club;

    private String name;

    public Group() {
        // -- //
    }

    public Group(long id, Club club, String name) {
        this.id = id;
        this.club = club;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
