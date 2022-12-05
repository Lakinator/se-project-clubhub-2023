package de.oth.seproject.clubhub.persistence.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Table(name = "generic_events")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class GenericEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "location_id")
    private Location location;

    @NotNull
    @FutureOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate eventDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime eventStart;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime eventEnd;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    public GenericEvent() {
        // -- //
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalTime getEventStart() {
        return eventStart;
    }

    public void setEventStart(LocalTime eventStart) {
        this.eventStart = eventStart;

        if (this.eventStart != null) {
            this.eventStart = this.eventStart.withSecond(0).withNano(0);
        }
    }

    public LocalTime getEventEnd() {
        return eventEnd;
    }

    public void setEventEnd(LocalTime eventEnd) {
        this.eventEnd = eventEnd;

        if (this.eventEnd != null) {
            this.eventEnd = this.eventEnd.withSecond(0).withNano(0);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isToday() {
        return getEventDate().isEqual(LocalDate.now());
    }

    public boolean isWholeDay() {
        return eventStart.equals(LocalTime.MIN) && eventEnd.equals(LocalTime.MAX.withSecond(0).withNano(0));
    }
}
