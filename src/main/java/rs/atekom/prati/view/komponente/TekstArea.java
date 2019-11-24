package rs.atekom.prati.view.komponente;

import com.vaadin.ui.TextArea;

public class TekstArea extends TextArea{

	private static final long serialVersionUID = 1L;

	public TekstArea(String naziv) {
		setCaption(naziv);
		setWidth("100%");
	}
}
