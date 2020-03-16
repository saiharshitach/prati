package rs.atekom.prati.view.komponente.combo;

import java.util.ArrayList;

import com.vaadin.ui.ComboBox;

import pratiBaza.tabele.Korisnici;
import pratiBaza.tabele.VozilaSaobracajne;
import rs.atekom.prati.server.Servis;

public class ComboSaobracajna extends ComboBox<VozilaSaobracajne>{

	private static final long serialVersionUID = 1L;

	public ComboSaobracajna(Korisnici korisnik, String naziv, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("објекти...");
		setItemCaptionGenerator(saobracajna -> saobracajna.getVozilo().getRegistracija());
		
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(indicator);
		setWidth("100%");
	}
	
	public ArrayList<VozilaSaobracajne> lista(Korisnici korisnik){
		return Servis.saobracajnaServis.nadjiSlobodneSaobracajne(korisnik, true);
	}
}
