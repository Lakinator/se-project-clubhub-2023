package de.oth.seproject.clubhub.persistence.model;

import javax.persistence.*;

@Table(name = "club_events")
@Entity
public class ClubEvent extends GenericEvent {
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "club_id")
    private Club club;

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }
}
