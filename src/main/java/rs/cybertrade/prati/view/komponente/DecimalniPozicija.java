package rs.cybertrade.prati.view.komponente;

import org.vaadin.textfieldformatter.NumeralFieldFormatter;

import com.vaadin.ui.TextField;

public class DecimalniPozicija extends TextField{

	private static final long serialVersionUID = 1L;

	public DecimalniPozicija(String naziv, boolean obavezno) {
		setRequiredIndicatorVisible(obavezno);
		setCaption(naziv);
		addStyleName("numerical");
		setWidth("100%");
		new NumeralFieldFormatter("", ".", 10).extend(this);
	}
}
