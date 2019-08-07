package rs.atekom.prati.view.komponente;

import java.util.List;

import com.vaadin.ui.ComboBox;
import pratiBaza.tabele.Korisnici;
import rs.atekom.prati.server.Servis;


public class ComboKorisniciVozaci extends ComboBox<Korisnici>{

	private static final long serialVersionUID = 1L;
	
	public ComboKorisniciVozaci(Korisnici korisnik, String naziv, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("корисници...");
		setItemCaptionGenerator(Korisnici::toString);
		setItems(lista(korisnik));
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(indicator);
		setWidth("100%");
	}

	public List<Korisnici> lista(Korisnici korisnik){
		return Servis.korisnikServis.nadjiSveKorisnikeVozace(korisnik, true);
	}
}
