package rs.atekom.prati.view.komponente.combo;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.ComboBox;

import pratiBaza.tabele.Grupe;
import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import rs.atekom.prati.server.Servis;

public class ComboObjektiSaVozilima extends ComboBox<Objekti>{

	private static final long serialVersionUID = 1L;

	public ComboObjektiSaVozilima(SistemPretplatnici pretplatnik, Organizacije organizacija, String naziv, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("објекти...");
		setItemCaptionGenerator(Objekti::getOznaka);
		setItems(lista(pretplatnik, organizacija));
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(indicator);
		setWidth("100%");
	}
	
	public List<Objekti> lista(SistemPretplatnici pretplatnik, Organizacije organizacija){
		return Servis.objekatServis.nadjiSveObjekteSavozilom(pretplatnik, organizacija);
	}
	
	public ArrayList<Objekti> lista(Grupe grupa){
		ArrayList<Objekti> objekti = new ArrayList<Objekti>();
		objekti.addAll(Servis.grupeObjekatServis.nadjiSveObjektePoGrupiSaVozilom(grupa));
		return objekti;
	}
}
