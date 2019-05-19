package rs.cybertrade.prati.view.komponente;

import java.util.List;

import com.vaadin.ui.ComboBox;

import pratiBaza.tabele.SistemUredjajiModeli;
import rs.cybertrade.prati.server.Servis;

public class ComboUredjajiModeli extends ComboBox<SistemUredjajiModeli>{

	private static final long serialVersionUID = 1L;

	public ComboUredjajiModeli(String naziv, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("сим картице...");
		setItemCaptionGenerator(SistemUredjajiModeli::getNaziv);
		setItems(lista());
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(indicator);
		setWidth("100%");
	}
	
	private List<SistemUredjajiModeli> lista(){
		return Servis.sistemUredjajModelServis.nadjiSveUredjajModele();
	}
}
