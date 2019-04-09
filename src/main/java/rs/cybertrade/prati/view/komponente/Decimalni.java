package rs.cybertrade.prati.view.komponente;

import org.vaadin.textfieldformatter.NumeralFieldFormatter;

import com.vaadin.ui.TextField;

public class Decimalni extends TextField{

	private static final long serialVersionUID = 1L;

	public Decimalni(String naziv, boolean obavezno) {
		setRequiredIndicatorVisible(obavezno);
		setCaption(naziv);
		addStyleName("numerical");
		setWidth("100%");
		new NumeralFieldFormatter(".", ",", 2).extend(this);
	}
}
