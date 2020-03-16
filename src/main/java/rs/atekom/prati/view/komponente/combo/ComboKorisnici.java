package rs.atekom.prati.view.komponente.combo;

import java.util.List;

import com.vaadin.ui.ComboBox;
import pratiBaza.tabele.Korisnici;
import rs.atekom.prati.server.Servis;

public class ComboKorisnici extends ComboBox<Korisnici>{
	
	private static final long serialVersionUID = 1L;

	public ComboKorisnici(Korisnici korisnik, String naziv, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("корисници...");
		setItemCaptionGenerator(Korisnici::toString);
		if(korisnik != null && korisnik.getSistemPretplatnici() != null) {
			setItems(lista(korisnik));
		}
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(true);
		setWidth("100%");
	}
	
	private List<Korisnici> lista(Korisnici korisnik){
		return Servis.korisnikServis.nadjiSveKorisnike(korisnik, true);
	}
}
