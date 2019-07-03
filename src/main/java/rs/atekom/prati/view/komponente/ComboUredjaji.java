package rs.atekom.prati.view.komponente;

import java.util.List;
import com.vaadin.ui.ComboBox;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import pratiBaza.tabele.Uredjaji;
import rs.atekom.prati.server.Servis;

public class ComboUredjaji extends ComboBox<Uredjaji>{

	private static final long serialVersionUID = 1L;

	public ComboUredjaji(SistemPretplatnici pretplatnik, Organizacije organizacija, String naziv, boolean prazno, boolean indicator, Uredjaji uredjaj) {
		setCaption(naziv);
		setPlaceholder("уређаји...");
		setItemCaptionGenerator(Uredjaji::getSerijskiBr);
		setItems(lista(pretplatnik, organizacija, uredjaj));
		if(uredjaj != null) {
			setSelectedItem(uredjaj);
		}
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(indicator);
		setWidth("100%");
	}
	
	private List<Uredjaji> lista(SistemPretplatnici pretplatnik, Organizacije organizacija, Uredjaji uredjaj){
		return Servis.uredjajServis.nadjiSveAktivneSlobodneUredjajePoPretplatniku(pretplatnik, organizacija, uredjaj);
	}
}
