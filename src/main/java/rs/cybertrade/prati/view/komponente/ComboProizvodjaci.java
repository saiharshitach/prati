package rs.cybertrade.prati.view.komponente;

import java.util.List;
import com.vaadin.ui.ComboBox;
import pratiBaza.tabele.SistemUredjajiProizvodjac;
import rs.cybertrade.prati.server.Servis;

public class ComboProizvodjaci extends ComboBox<SistemUredjajiProizvodjac>{

	private static final long serialVersionUID = 1L;

	public ComboProizvodjaci(String naziv, boolean prazno) {
		setCaption(naziv);
		setPlaceholder("изабери произвођача...");
		setItemCaptionGenerator(SistemUredjajiProizvodjac::getNaziv);
		setItems(lista());
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(true);
		setWidth("100%");
	}
	
	private List<SistemUredjajiProizvodjac> lista(){
		return Servis.sistemUredjajProizvodjacServis.nadjiSveSistemUredjajeProizvodjace();
	}

}
