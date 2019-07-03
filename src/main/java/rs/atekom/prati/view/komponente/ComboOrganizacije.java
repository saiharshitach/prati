package rs.atekom.prati.view.komponente;

import java.util.List;
import com.vaadin.ui.ComboBox;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import rs.atekom.prati.server.Servis;

public class ComboOrganizacije extends ComboBox<Organizacije>{

	private static final long serialVersionUID = 1L;

	public ComboOrganizacije(SistemPretplatnici pretplatnici, String naziv, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("организацијe...");
		setItemCaptionGenerator(Organizacije::getNaziv);
		setItems(lista(pretplatnici));
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(indicator);
		setWidth("100%");
	}
	
	private List<Organizacije> lista(SistemPretplatnici pretplatnik){
		return Servis.organizacijaServis.nadjiSveOrganizacije(pretplatnik, true);
	}
}
