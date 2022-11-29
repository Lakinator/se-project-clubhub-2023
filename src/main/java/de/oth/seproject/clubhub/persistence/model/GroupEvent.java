package de.oth.seproject.clubhub.persistence.model;

import javax.persistence.*;

@Table(name = "group_events")
@Entity
public class GroupEvent extends GenericEvent {

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    public GroupEvent() {
        // -- //
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
