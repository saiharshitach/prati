package rs.atekom.prati.view.komponente.combo;

import java.util.List;
import com.vaadin.ui.ComboBox;
import pratiBaza.tabele.SistemGoriva;
import rs.atekom.prati.server.Servis;

public class ComboGorivo extends ComboBox<SistemGoriva>{

	private static final long serialVersionUID = 1L;

	public ComboGorivo(String naziv, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("гориво...");
		setItemCaptionGenerator(SistemGoriva::getNaziv);
		setItems(lista());
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(indicator);
		setWidth("100%");
	}
	
	private List<SistemGoriva> lista(){
		return Servis.sistemGorivoServis.vratiSvaGoriva(false);
	}
}
