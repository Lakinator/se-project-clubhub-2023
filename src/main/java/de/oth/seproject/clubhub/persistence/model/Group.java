package de.oth.seproject.clubhub.persistence.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "club_id")
    private Club club;

    @OneToMany(mappedBy = "group", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Role> roles = new ArrayList<>();

    private String name;

    public Group() {
        // -- //
    }

    public Group(long id, Club club, List<Role> roles, String name) {
        this.id = id;
        this.club = club;
        this.roles = roles;
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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
