package de.oth.seproject.clubhub.web.dto;

public class EventExtraDTO {

    private Boolean wholeDay;

    public EventExtraDTO() {
        this.wholeDay = true;
    }

    public EventExtraDTO(Boolean wholeDay) {
        this.wholeDay = wholeDay;
    }

    public Boolean getWholeDay() {
        return wholeDay;
    }

    public void setWholeDay(Boolean wholeDay) {
        this.wholeDay = wholeDay;
    }
}
