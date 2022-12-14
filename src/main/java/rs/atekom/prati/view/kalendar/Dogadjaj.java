package rs.atekom.prati.view.kalendar;

import java.time.ZonedDateTime;

import static rs.atekom.prati.view.kalendar.Dogadjaj.State.empty;

public class Dogadjaj {
	
    enum State {
        empty,
        planned,
        confirmed
    }

    private ZonedDateTime start;

    private ZonedDateTime end;

    private String name;

    private String details;

    private State state = empty;

    private boolean longTime;

    public Dogadjaj(boolean longTime) {
        this.longTime = longTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isEditable() {
        return state != State.confirmed;
    }

    public boolean isLongTimeEvent() {
        return longTime;
    }}
