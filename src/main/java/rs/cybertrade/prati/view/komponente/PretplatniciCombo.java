package rs.cybertrade.prati.view.komponente;

import java.util.List;

import com.vaadin.ui.ComboBox;

import pratiBaza.tabele.SistemPretplatnici;
import rs.cybertrade.prati.Servis;

public class PretplatniciCombo extends ComboBox<SistemPretplatnici>{

	private static final long serialVersionUID = 1L;
	
	public PretplatniciCombo(String naziv, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("претплатници...");
		setItemCaptionGenerator(SistemPretplatnici::getNaziv);
		setItems(lista());
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(indicator);
		setWidth("100%");
	}

	private List<SistemPretplatnici> lista(){
		return Servis.sistemPretplatnikServis.nadjiSveAktivnePretplatnike();
	}
}
