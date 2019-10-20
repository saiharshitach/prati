package rs.atekom.prati.view.komponente;

import java.util.List;
import com.vaadin.ui.ComboBox;
import pratiBaza.tabele.Korisnici;
import pratiBaza.tabele.Vozila;
import rs.atekom.prati.server.Servis;

public class ComboVozila extends ComboBox<Vozila>{

	private static final long serialVersionUID = 1L;
	
	public ComboVozila(Korisnici korisnik, String naziv, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("возила...");
		setItemCaptionGenerator(vozila -> vozila.getObjekti().getOznaka());
		if(korisnik != null && korisnik.getSistemPretplatnici() != null) {
			setItems(lista(korisnik));
		}
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(indicator);
		setWidth("100%");
	}

	public List<Vozila> lista(Korisnici korisnik){
		return Servis.voziloServis.vratisvaVozila(korisnik, true);
	}
}
