package rs.cybertrade.prati.view.komponente;

import java.util.List;

import com.vaadin.ui.ComboBox;

import pratiBaza.tabele.SistemOperateri;
import rs.cybertrade.prati.Servis;

public class OperateriCombo extends ComboBox<SistemOperateri>{

	private static final long serialVersionUID = 1L;
	
	public OperateriCombo(String naziv, boolean prazno, boolean indicator) {
		setCaption(naziv);
		setPlaceholder("сим картице...");
		setItemCaptionGenerator(SistemOperateri::getNaziv);
		setItems(lista());
		setEmptySelectionAllowed(prazno);
		setRequiredIndicatorVisible(indicator);
		setWidth("100%");
	}
	
	private List<SistemOperateri> lista(){
		return Servis.sistemOperaterServis.nadjiSveOperatere();
	}

}
