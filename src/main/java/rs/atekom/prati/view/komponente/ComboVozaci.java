package rs.atekom.prati.view.komponente;

import java.util.List;

import com.vaadin.ui.ComboBox;
import pratiBaza.tabele.Korisnici;
import pratiBaza.tabele.Vozaci;
import rs.atekom.prati.server.Servis;


public class ComboVozaci extends ComboBox<Vozaci>{

	private static final long serialVersionUID = 1L;
	
	public ComboVozaci(Korisnici korisnik, String naziv, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("корисници...");
		setItemCaptionGenerator(vozaci -> vozaci.getKorisnici().toString());
		setItems(lista(korisnik));
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(indicator);
		setWidth("100%");
	}

	public List<Vozaci> lista(Korisnici korisnik){
		return Servis.vozacServis.nadjiSveVozace(korisnik);
	}
}
