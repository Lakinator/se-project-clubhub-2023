package de.oth.seproject.clubhub.persistence.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "clubs")
@Entity
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "club", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Announcement> announcements = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Group> groups = new ArrayList<>();

    public Club() {
        // -- //
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}
