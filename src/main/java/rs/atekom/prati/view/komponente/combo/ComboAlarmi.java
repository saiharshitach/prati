package rs.atekom.prati.view.komponente.combo;

import java.util.List;

import com.vaadin.ui.ComboBox;

import pratiBaza.tabele.SistemAlarmi;
import rs.atekom.prati.server.Servis;

public class ComboAlarmi extends ComboBox<SistemAlarmi>{

	private static final long serialVersionUID = 1L;

	public ComboAlarmi(String naziv, boolean prazno, boolean aktivan, boolean email, boolean pregled, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("аларми...");
		setItemCaptionGenerator(SistemAlarmi::getNaziv);
		setItems(lista(aktivan, email, pregled));
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(indicator);
		setWidth("100%");
	}
	
	private List<SistemAlarmi> lista(boolean aktivan, boolean email, boolean pregled){
		return Servis.sistemAlarmServis.vratiAlarmePoZahtevu(aktivan, email, pregled);
	}
}
