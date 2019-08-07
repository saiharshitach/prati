package rs.atekom.prati.view.komponente;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.ui.DateTimeField;

public class DatumVreme extends DateTimeField{

	private static final long serialVersionUID = 1L;

	public DatumVreme(boolean caption, String captionText, int sat, int minut, int dan) {
		setTextFieldEnabled(false);
		setStyleName("datefieldcentered");
		if(caption) {
			setCaption(captionText);
		}
		podesiVreme(sat, minut, dan);
	}
	
	private void podesiVreme(int sat, int minut, int dan) {
		Date datum = new Date();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, LocalDate.now().getDayOfMonth() + dan);
		cal.set(Calendar.HOUR_OF_DAY, sat);
        cal.set(Calendar.MINUTE, minut);
        datum = cal.getTime();
        setValue(datum.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        setDateFormat("dd-MM HH:mm");
	    setResolution(DateTimeResolution.MINUTE);
	    setLocale(new Locale("sr", "RS"));
	}
	
	public void podesiVreme(Date datum) {
        setValue(datum.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        setDateFormat("dd-MM HH:mm");
	    setResolution(DateTimeResolution.MINUTE);
	    setLocale(new Locale("sr", "RS"));
	}
}
