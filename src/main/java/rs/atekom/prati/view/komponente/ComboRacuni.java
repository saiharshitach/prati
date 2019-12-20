package rs.atekom.prati.view.komponente;

import java.util.List;

import com.vaadin.ui.ComboBox;

import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.Racuni;
import pratiBaza.tabele.SistemPretplatnici;
import rs.atekom.prati.server.Servis;

public class ComboRacuni extends ComboBox<Racuni>{

	private static final long serialVersionUID = 1L;
	
	public ComboRacuni(SistemPretplatnici pretplatnik, Organizacije organizacija, String naziv, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("рачуни...");
		setItemCaptionGenerator(racuni -> racuni == null ? "" : racuni.getPartner().getNaziv() + " " + racuni.getBrojRacuna());
		setItems(lista(pretplatnik, organizacija));
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(true);
		setWidth("100%");
	}
	
	private List<Racuni> lista(SistemPretplatnici pretplatnik, Organizacije organizacija){
		return Servis.racunServis.nadjiRacunePoPretplatniku(pretplatnik, organizacija, true, null, null, null);
	}

}
