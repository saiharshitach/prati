package rs.atekom.prati.view.komponente;

import java.util.List;

import com.vaadin.ui.ComboBox;

import pratiBaza.tabele.Korisnici;
import pratiBaza.tabele.Objekti;
import rs.atekom.prati.server.Servis;

public class ComboVozila extends ComboBox<Objekti>{

	private static final long serialVersionUID = 1L;
	
	public ComboVozila(Korisnici korisnik, String naziv, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("возила...");
		setItemCaptionGenerator(Objekti::getOznaka);
		if(korisnik != null) {
			setItems(lista(korisnik));
		}
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(indicator);
		setWidth("100%");
	}

	public List<Objekti> lista(Korisnici korisnik){
		return Servis.objekatServis.vratiSvaVozila(korisnik);
	}
}
