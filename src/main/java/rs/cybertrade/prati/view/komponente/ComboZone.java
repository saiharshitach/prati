package rs.cybertrade.prati.view.komponente;

import java.util.List;

import com.vaadin.ui.ComboBox;

import pratiBaza.tabele.Korisnici;
import pratiBaza.tabele.Zone;
import rs.cybertrade.prati.server.Servis;

public class ComboZone extends ComboBox<Zone>{

	private static final long serialVersionUID = 1L;

	public ComboZone(Korisnici korisnik, String naziv, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("корисници...");
		setItemCaptionGenerator(Zone::getNaziv);
		setItems(lista(korisnik));
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(true);
		setWidth("100%");
	}
	
	private List<Zone> lista(Korisnici korisnik){
		return Servis.zonaServis.nadjiSveZone(korisnik, true);
	}
}
