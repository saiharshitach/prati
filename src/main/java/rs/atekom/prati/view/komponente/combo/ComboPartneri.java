package rs.atekom.prati.view.komponente.combo;

import java.util.List;

import com.vaadin.ui.ComboBox;

import pratiBaza.tabele.Korisnici;
import pratiBaza.tabele.Partneri;
import rs.atekom.prati.server.Servis;

public class ComboPartneri extends ComboBox<Partneri>{

	private static final long serialVersionUID = 1L;

	public ComboPartneri(Korisnici korisnik, String naziv, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("партнери...");
		setItemCaptionGenerator(Partneri::getNaziv);
		setItems(lista(korisnik));
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(indicator);
		setWidth("100%");
	}
	
	public List<Partneri> lista(Korisnici korisnik){
		return Servis.partnerServis.nadjiSvePartnerePoPretplatniku(korisnik.getSistemPretplatnici(), true);
		}
}
