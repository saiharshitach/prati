package rs.cybertrade.prati.view.komponente;

import java.util.List;

import com.vaadin.ui.ComboBox;

import pratiBaza.tabele.Korisnici;
import pratiBaza.tabele.Objekti;
import rs.cybertrade.prati.server.Servis;

public class ComboObjekti extends ComboBox<Objekti>{

	private static final long serialVersionUID = 1L;

	public ComboObjekti(Korisnici korisnik, String naziv, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("објекти...");
		setItemCaptionGenerator(Objekti::getOznaka);
		setItems(lista(korisnik));
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(true);
		setWidth("100%");
	}
	
	private List<Objekti> lista(Korisnici korisnik){
		return Servis.objekatServis.vratiSveObjekte(korisnik, true);
	}
}
