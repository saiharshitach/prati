package rs.atekom.prati.view.komponente;

import com.vaadin.ui.TextField;

public class Tekst extends TextField{

	private static final long serialVersionUID = 1L;
	
	public Tekst(String naziv, boolean obavezno) {
		setRequiredIndicatorVisible(obavezno);
		setCaption(naziv);
		setWidth("100%");
	}
}
