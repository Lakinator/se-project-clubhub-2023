package de.oth.seproject.clubhub.persistence.model;

import javax.persistence.*;

@Table(name = "group_event_requests")
@Entity
public class GroupEventRequest extends GenericEvent {
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "creator_group_id", referencedColumnName = "id")
    private Group creatorGroup;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "requested_group_id", referencedColumnName = "id")
    private Group requestedGroup;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus = RequestStatus.PENDING;

    public GroupEventRequest() {
        // -- //
    }

    public Group getCreatorGroup() {
        return creatorGroup;
    }

    public void setCreatorGroup(Group creatorGroup) {
        this.creatorGroup = creatorGroup;
    }

    public Group getRequestedGroup() {
        return requestedGroup;
    }

    public void setRequestedGroup(Group requestedGroup) {
        this.requestedGroup = requestedGroup;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }
}
