package de.oth.seproject.clubhub.persistence.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Vorname darf nicht leer sein")
    private String firstName;

    @NotEmpty(message = "Nachname darf nicht leer sein")
    private String lastName;

    @NotEmpty(message = "E-Mail darf nicht leer sein")
    @Email(message = "Gebe eine gültige Email an")
    private String email;

    @Size(min = 4, message = "Das Passwort muss mindestens 4 Zeichen enthalten")
    private String password;

    private Boolean active;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "club_id")
    @NotNull(message = "Verein muss gewählt werden")
    private Club club;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Announcement> announcements = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<GenericEvent> genericEvents = new ArrayList<>();

    @ManyToMany(mappedBy = "users", cascade = CascadeType.PERSIST)
    private List<ChatRoom> chatRooms = new ArrayList<>();

    public User() {
        // -- //
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<GenericEvent> getGenericEvents() {
        return genericEvents;
    }

    public void setGenericEvents(List<GenericEvent> genericEvents) {
        this.genericEvents = genericEvents;
    }

    public List<ChatRoom> getChatRooms() {
        return chatRooms;
    }

    public void setChatRooms(List<ChatRoom> chatRooms) {
        this.chatRooms = chatRooms;
    }

    public List<ChatRoom> getChatRoomsInGroup(Long groupId) {
        return this.getChatRooms().stream().filter(chatRoom -> chatRoom.getGroup().getId() == groupId).toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
