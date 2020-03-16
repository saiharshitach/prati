package rs.atekom.prati.view.kalendar;

import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import org.vaadin.addon.calendar.Calendar;
import org.vaadin.addon.calendar.handler.BasicDateClickHandler;
import org.vaadin.addon.calendar.item.BasicItemProvider;
import org.vaadin.addon.calendar.ui.CalendarComponentEvents;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import pratiBaza.tabele.Grupe;
import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.VozilaNalozi;
import rs.atekom.prati.server.Servis;

import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import static java.time.temporal.ChronoUnit.DAYS;

import java.sql.Timestamp;
//import java.util.GregorianCalendar;
//import java.util.Random;
//import com.vaadin.ui.UI;


public class Kalendar extends CustomComponent{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "kalendar";
    private MeetingDataProvider eventProvider;
    private Calendar<DogadjajStavka> calendar;
    //private final Random R = new Random(0);
    public Panel panel;
    public KalendarView view;

	public Kalendar(KalendarView v) {
		view = v;
        setId("kalendar");
        setSizeFull();
        initCalendar();
        
        VerticalLayout barGrid = new VerticalLayout();
        barGrid.setMargin(false);
        barGrid.setSpacing(false);
        barGrid.setSizeFull();
        panel = new Panel(calendar);
        panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        panel.setSizeFull();
        panel.setWidth("100%");

        barGrid.addComponent(panel);
        barGrid.setExpandRatio(panel, 1);

        setCompositionRoot(barGrid);
	}
	
	
    public void switchToMonth(Month month) {
        calendar.withMonth(month);
    }

    public Calendar<DogadjajStavka> getCalendar() {
        return calendar;
    }
    
    private void onCalendarRangeSelect(CalendarComponentEvents.RangeSelectEvent event) {
    	Grupe grupa = view.grupeCombo.getValue();
    	if(grupa != null) {
        	ArrayList<VozilaNalozi> nalozi = Servis.nalogServis.nadjiNalogeZaGrupuUPeriodu(Servis.objekatServis.vratiObjektePoGrupi(grupa), 
        			Timestamp.valueOf(event.getStart().toLocalDateTime()), Timestamp.valueOf(event.getEnd().toLocalDateTime()));
        	System.out.println(" nalozi " + nalozi.size());
        	for(VozilaNalozi nalog : nalozi) {
        		Dogadjaj dog = new Dogadjaj(!event.getStart().truncatedTo(DAYS).equals(event.getEnd().truncatedTo(DAYS)));
        		dog.setStart(ZonedDateTime.ofInstant(nalog.getOcekivaniPolazak().toInstant(), ZoneId.systemDefault()));
        		dog.setEnd(ZonedDateTime.ofInstant(nalog.getOcekivaniDolazak().toInstant(), ZoneId.systemDefault()));
        		dog.setName(nalog.getVozilo() + " " + nalog.getVozac() + " " + nalog.getDoMesta());
        		dog.setDetails("Полазак: " + nalog.getOcekivaniPolazak() + " долазак: " + nalog.getOcekivaniDolazak() + " " + nalog.getKomentar());
        		dog.setState(Dogadjaj.State.confirmed);
        		eventProvider.addItem(new DogadjajStavka(dog));
        	}
    	}

        /*Dogadjaj meeting = new Dogadjaj(!event.getStart().truncatedTo(DAYS).equals(event.getEnd().truncatedTo(DAYS)));
        meeting.setStart(event.getStart());
        meeting.setEnd(event.getEnd());
        meeting.setName("A Name");
        meeting.setDetails("A Detail<br>with HTML<br> with more lines");
        // Random state
        meeting.setState(R.nextInt(2) == 1 ? Dogadjaj.State.planned : Dogadjaj.State.confirmed);
        eventProvider.addItem(new DogadjajStavka(meeting));**/
	}
    
    private void onCalendarClick(CalendarComponentEvents.ItemClickEvent event) {
        DogadjajStavka item = (DogadjajStavka) event.getCalendarItem();
        final Dogadjaj meeting = item.getMeeting();
        Notification.show(meeting.getName(), meeting.getDetails(), Type.HUMANIZED_MESSAGE);
    }
	
    private void initCalendar() {
        eventProvider = new MeetingDataProvider();
        calendar = new Calendar<>(eventProvider);
        calendar.addStyleName("meetings");
        calendar.setSizeFull();
        //calendar.setWidth("100%");
        //calendar.setHeight(100.0f, Unit.PERCENTAGE);
        //calendar.setResponsive(true);
        calendar.setItemCaptionAsHtml(true);
        calendar.setContentMode(ContentMode.HTML);

        calendar.withVisibleDays(1, 7);

        addCalendarEventListeners();

        //setupBlockedTimeSlots();
    }
	
    /*private void setupBlockedTimeSlots() {
    	java.util.Calendar cal = java.util.Calendar.getInstance();
	    cal.set(java.util.Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
	    cal.clear(java.util.Calendar.MINUTE);
	    cal.clear(java.util.Calendar.SECOND);
	    cal.clear(java.util.Calendar.MILLISECOND);
	    
	    GregorianCalendar bcal = new GregorianCalendar(UI.getCurrent().getLocale());
	    bcal.clear();
	    long start = bcal.getTimeInMillis();
	    bcal.add(java.util.Calendar.HOUR, 7);
	    bcal.add(java.util.Calendar.MINUTE, 30);
	    long end = bcal.getTimeInMillis();
	    
	    calendar.addTimeBlock(start, end, "my-blocky-style");
	    cal.add(java.util.Calendar.DAY_OF_WEEK, 2);
	    bcal.clear();
	    bcal.add(java.util.Calendar.HOUR, 14);
	    bcal.add(java.util.Calendar.MINUTE, 30);
	    start = bcal.getTimeInMillis();
	    bcal.add(java.util.Calendar.MINUTE, 60);
	    end = bcal.getTimeInMillis();
	    calendar.addTimeBlock(start, end);
	    }**/
	  
    private void addCalendarEventListeners() {
        calendar.setHandler(new BasicDateClickHandler(true));
        calendar.setHandler(this::onCalendarClick);
        calendar.setHandler(this::onCalendarRangeSelect);
    }
	
    private final class MeetingDataProvider extends BasicItemProvider<DogadjajStavka> {
		private static final long serialVersionUID = 1L;
		void removeAllEvents() {
            this.itemList.clear();
            fireItemSetChanged();
        }
    }

    public void ukloniSveDogadjaje() {
    	eventProvider.removeAllEvents();
    }
    
    public void postaviNaloge() {
    	Grupe grupa = view.grupeCombo.getValue();
    	//System.out.println(" grupa " + grupa.getNaziv());
    	if(grupa != null) {
    		ArrayList<Objekti> objekti = Servis.grupeObjekatServis.nadjiSveObjektePoGrupi(grupa);//Servis.objekatServis.vratiObjektePoGrupi(grupa);
    		//System.out.println(" objekti " + objekti.size());
        	ArrayList<VozilaNalozi> nalozi = Servis.nalogServis.nadjiNalogeZaGrupuUPeriodu(objekti, 
        			Timestamp.valueOf(calendar.getStartDate().toLocalDateTime()), Timestamp.valueOf(calendar.getEndDate().toLocalDateTime()));
        	//System.out.println(" nalozi " + nalozi.size());
        	for(VozilaNalozi nalog : nalozi) {
        		Dogadjaj dog = new Dogadjaj(!calendar.getStartDate().truncatedTo(DAYS).equals(calendar.getEndDate().truncatedTo(DAYS)));
        		dog.setStart(ZonedDateTime.ofInstant(nalog.getOcekivaniPolazak().toInstant(), ZoneId.systemDefault()));
        		dog.setEnd(ZonedDateTime.ofInstant(nalog.getOcekivaniDolazak().toInstant(), ZoneId.systemDefault()));
        		dog.setName(nalog.getVozilo().getOznaka() + " " + nalog.getVozac().toString() + " " + nalog.getBrojNaloga());
        		dog.setDetails(nalog.getOdMesta() + " - " + nalog.getDoMesta() + " полазак: " + nalog.getOcekivaniPolazak() + " долазак: " + nalog.getOcekivaniDolazak() + " " + nalog.getKomentar());
        		eventProvider.addItem(new DogadjajStavka(dog));
        	}
    	}
    }
}
