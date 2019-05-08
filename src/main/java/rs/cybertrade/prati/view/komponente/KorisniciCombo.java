package rs.cybertrade.prati.view.komponente;

import java.util.List;

import com.vaadin.ui.ComboBox;
import pratiBaza.tabele.Korisnici;
import rs.cybertrade.prati.Servis;

public class KorisniciCombo extends ComboBox<Korisnici>{
	
	private static final long serialVersionUID = 1L;

	public KorisniciCombo(Korisnici korisnik, String naziv, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("корисници...");
		setItemCaptionGenerator(Korisnici::toString);
		setItems(lista(korisnik));
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(true);
		setWidth("100%");
	}
	
	private List<Korisnici> lista(Korisnici korisnik){
		return Servis.korisnikServis.nadjiSveKorisnike(korisnik, true);
	}
}
