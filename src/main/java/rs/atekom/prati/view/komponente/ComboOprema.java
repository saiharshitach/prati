package rs.atekom.prati.view.komponente;

import java.util.List;

import com.vaadin.ui.ComboBox;
import pratiBaza.tabele.Korisnici;
import pratiBaza.tabele.VozilaOprema;
import rs.atekom.prati.server.Servis;

public class ComboOprema extends ComboBox<VozilaOprema>{

	private static final long serialVersionUID = 1L;

	public ComboOprema(String naziv, Korisnici korisnik, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("сим картице...");
		setItemCaptionGenerator(VozilaOprema::getNaziv);
		setItems(lista(korisnik));
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(indicator);
		setWidth("100%");
	}
	
	public List<VozilaOprema> lista(Korisnici korisnik){
		return Servis.opremaServis.nadjiSveVozilaOprema(korisnik);
	}
}
