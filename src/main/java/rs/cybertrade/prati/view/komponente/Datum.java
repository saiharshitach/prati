package rs.cybertrade.prati.view.komponente;

import java.time.LocalDate;
import com.vaadin.ui.DateField;

public class Datum extends DateField{

	private static final long serialVersionUID = 1L;

	public Datum(String naziv, boolean obavezno) {
		setRequiredIndicatorVisible(obavezno);
		setStyleName("datefieldcentered");
		setCaption(naziv);
		setWidth("100%");
		setDateStyle(LocalDate.now(), null);
		setDateFormat("dd-MM-yyyy");
		//Date dat = new Date();
		//setRangeEnd(dat.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
	}
}
