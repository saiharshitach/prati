package rs.atekom.prati.view.komponente.combo;

import java.util.List;

import com.vaadin.ui.ComboBox;

import pratiBaza.tabele.Grupe;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import rs.atekom.prati.server.Servis;

public class ComboGrupe extends ComboBox<Grupe>{
	
	private static final long serialVersionUID = 1L;

	public ComboGrupe(SistemPretplatnici pretplatnik, Organizacije organizacija, String naziv, boolean  prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("групе...");
		setItemCaptionGenerator(Grupe::getNaziv);
		setItems(lista(pretplatnik, organizacija));
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(indicator);
		setWidth("100%");
	}
	
	private List<Grupe> lista(SistemPretplatnici pretplatnik, Organizacije organizacija){
		return Servis.grupeServis.vratiGrupeAktivne(pretplatnik, organizacija);
	}
}
