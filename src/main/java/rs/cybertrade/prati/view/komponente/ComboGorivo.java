package rs.cybertrade.prati.view.komponente;

import java.util.List;
import com.vaadin.ui.ComboBox;
import pratiBaza.tabele.SistemGoriva;
import rs.cybertrade.prati.server.Servis;

public class ComboGorivo extends ComboBox<SistemGoriva>{

	private static final long serialVersionUID = 1L;

	public ComboGorivo(String naziv, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("објекти...");
		setItemCaptionGenerator(SistemGoriva::getNaziv);
		setItems(lista());
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(true);
		setWidth("100%");
	}
	
	private List<SistemGoriva> lista(){
		return Servis.sistemGorivoServis.vratiSvaGoriva(false);
	}
}
