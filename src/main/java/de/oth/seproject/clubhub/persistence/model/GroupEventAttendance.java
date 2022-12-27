package de.oth.seproject.clubhub.persistence.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name = "group_event_attendances")
@Entity
public class GroupEventAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;	

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "group_event_id")
    private GroupEvent groupEvent;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;

    private boolean isNotRemoved;

    public GroupEventAttendance() {
        // -- //
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public GroupEvent getGroupEvent() {
        return groupEvent;
    }

    public void setGroupEvent(GroupEvent groupEvent) {
        this.groupEvent = groupEvent;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public boolean getIsNotRemoved() {
        return isNotRemoved;
    }

    public void setIsNotRemoved(boolean isNotRemoved) {
        this.isNotRemoved = isNotRemoved;
    }
}
