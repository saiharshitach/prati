package rs.atekom.prati.view.komponente.combo;

import java.util.List;
import com.vaadin.ui.ComboBox;
import pratiBaza.tabele.Korisnici;
import pratiBaza.tabele.VozilaNalozi;
import rs.atekom.prati.server.Servis;

public class ComboNalozi extends ComboBox<VozilaNalozi>{

	private static final long serialVersionUID = 1L;

	public ComboNalozi(Korisnici korisnik, String naziv, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("налози...");
		setItemCaptionGenerator(VozilaNalozi::getBrojNaloga);
		setItems(lista(korisnik));
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(indicator);
		setWidth("100%");
	}
	
	private List<VozilaNalozi> lista(Korisnici korisnik){
		return Servis.nalogServis.nadjiSveVozilaNaloge(korisnik);
	}
}
