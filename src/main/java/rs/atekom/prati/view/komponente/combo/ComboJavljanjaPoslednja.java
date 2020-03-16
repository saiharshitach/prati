package rs.atekom.prati.view.komponente.combo;

import com.vaadin.ui.ComboBox;

import pratiBaza.tabele.Grupe;
import pratiBaza.tabele.JavljanjaPoslednja;

public class ComboJavljanjaPoslednja extends ComboBox<JavljanjaPoslednja>{

	private static final long serialVersionUID = 1L;

	public ComboJavljanjaPoslednja(Grupe grupa, String naziv, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("објекти...");
		setItemCaptionGenerator(javljanjaPoslednja -> javljanjaPoslednja.getObjekti().getOznaka());
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(indicator);
		setWidth("100%");
	}
}
