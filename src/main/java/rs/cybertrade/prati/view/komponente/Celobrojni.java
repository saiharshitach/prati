package rs.cybertrade.prati.view.komponente;

import org.vaadin.textfieldformatter.NumeralFieldFormatter;
import com.vaadin.ui.TextField;

public class Celobrojni extends TextField{

	private static final long serialVersionUID = 1L;

	public Celobrojni(String naziv, boolean obavezno) {
		setRequiredIndicatorVisible(obavezno);
		setCaption(naziv);
		setStyleName("numerical");
		setWidth("100%");
		new NumeralFieldFormatter("", ".", 0).extend(this);
	}
}
