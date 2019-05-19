package rs.cybertrade.prati.view.komponente;

import java.util.List;
import com.vaadin.ui.ComboBox;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.Sim;
import pratiBaza.tabele.SistemPretplatnici;
import rs.cybertrade.prati.server.Servis;

public class ComboSim extends ComboBox<Sim>{

	private static final long serialVersionUID = 1L;

	public ComboSim(SistemPretplatnici pretplatnici, Organizacije organizacija, Sim sim, String naziv, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("сим картице...");
		setItemCaptionGenerator(Sim::getIccid);
		setItems(lista(pretplatnici, organizacija, sim));
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(indicator);
		setWidth("100%");
	}
	
	private List<Sim> lista(SistemPretplatnici pretplatnici, Organizacije organizacija, Sim sim){
		return Servis.simServis.vratiSveAktivneSimKartice(pretplatnici, organizacija, sim);
	}
}
