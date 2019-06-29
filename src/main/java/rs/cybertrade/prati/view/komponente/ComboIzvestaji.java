package rs.cybertrade.prati.view.komponente;

import java.util.ArrayList;
import java.util.List;
import com.vaadin.ui.ComboBox;
import pratiBaza.pomocne.IzvestajTip;

public class ComboIzvestaji extends ComboBox<IzvestajTip>{

	private static final long serialVersionUID = 1L;

	public ComboIzvestaji() {
		setCaption(null);
		setPlaceholder("извештаји...");
		setItemCaptionGenerator(IzvestajTip::getNaziv);
		setItems(lista());
		setEmptySelectionAllowed(true);
		setWidth("100%");
	}
	
	private List<IzvestajTip> lista(){
		ArrayList<IzvestajTip> izvestaji = new ArrayList<IzvestajTip>();
		izvestaji.add(new IzvestajTip(1, "пређени пут"));
		
		return izvestaji;
	}

}
