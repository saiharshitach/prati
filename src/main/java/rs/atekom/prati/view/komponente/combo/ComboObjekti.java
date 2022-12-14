package rs.atekom.prati.view.komponente.combo;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.ComboBox;

import pratiBaza.tabele.Grupe;
import pratiBaza.tabele.Korisnici;
import pratiBaza.tabele.Objekti;
import rs.atekom.prati.server.Servis;

public class ComboObjekti extends ComboBox<Objekti>{

	private static final long serialVersionUID = 1L;

	public ComboObjekti(Korisnici korisnik, String naziv, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("објекти...");
		setItemCaptionGenerator(Objekti::getOznaka);
		if(korisnik != null && korisnik.getSistemPretplatnici() != null) {
			setItems(lista(korisnik));
		}
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(indicator);
		setWidth("100%");
	}
	
	public List<Objekti> lista(Korisnici korisnik){
		if(korisnik.isAdmin()) {
			return Servis.objekatServis.vratiSveObjekte(korisnik, true);
		}else {
			ArrayList<Grupe> grupe = new ArrayList<Grupe>();
			grupe.addAll(Servis.grupeKorisnikServis.vratiSveGrupePoKorisniku(korisnik));
			return Servis.grupeObjekatServis.nadjiSveObjektePoGrupama(grupe);
		}
	}
}
