package rs.cybertrade.prati.view.komponente;

import com.vaadin.ui.PasswordField;

public class TekstLozinka extends PasswordField{

	private static final long serialVersionUID = 1L;

	public TekstLozinka(String naziv, boolean obavezno) {
		setRequiredIndicatorVisible(obavezno);
		setCaption(naziv);
		setWidth("100%");
	}
}
