package rs.atekom.prati.view.komponente;

import com.vaadin.ui.TextField;

public class Filter extends TextField{

	private static final long serialVersionUID = 1L;

	public Filter(String naslov) {
		setStyleName("filter-textfield");
		setPlaceholder(naslov);
	}
}
