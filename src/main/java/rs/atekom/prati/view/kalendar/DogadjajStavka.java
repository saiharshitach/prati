package rs.atekom.prati.view.kalendar;

import java.time.ZonedDateTime;

import org.vaadin.addon.calendar.item.BasicItem;

import com.vaadin.icons.VaadinIcons;

public class DogadjajStavka extends BasicItem{

	private static final long serialVersionUID = 1L;
	private final Dogadjaj meeting;

	public DogadjajStavka(Dogadjaj meeting) {
        super(meeting.getDetails(), null, meeting.getStart(), meeting.getEnd());
        this.meeting = meeting;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof DogadjajStavka)) {
			return false;
		}
		DogadjajStavka that = (DogadjajStavka) o;
		return getMeeting().equals(that.getMeeting());
	}

	public Dogadjaj getMeeting() {
		return meeting;
	}

	@Override
	public String getStyleName() {
		return "state-" + meeting.getState().name().toLowerCase();
	}

	@Override
	public int hashCode() {
		return getMeeting().hashCode();
	}

	@Override
	public boolean isAllDay() {
		return meeting.isLongTimeEvent();
	}

    @Override
    public boolean isMoveable() {
        return meeting.isEditable();
    }

    @Override
    public boolean isResizeable() {
        return meeting.isEditable();
    }

//    @Override
//    public boolean isClickable() {
//        return meeting.isEditable();
//    }

    @Override
	public void setEnd(ZonedDateTime end) {
		meeting.setEnd(end);
		super.setEnd(end);
	}

	@Override
	public void setStart(ZonedDateTime start) {
		meeting.setStart(start);
		super.setStart(start);
	}

    @Override
    public String getDateCaptionFormat() {
        //return CalendarItem.RANGE_TIME;
        return VaadinIcons.CLOCK.getHtml() + " %s<br>" +
               VaadinIcons.ARROW_CIRCLE_RIGHT_O.getHtml() + " %s";
	}


}
