package rs.atekom.prati.view.komponente;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.ComboBox;

import pratiBaza.pomocne.IzvestajTip;

public class ComboIzvestajiVozilo extends ComboBox<IzvestajTip>{

	private static final long serialVersionUID = 1L;

	public ComboIzvestajiVozilo() {
		setCaption(null);
		setPlaceholder("извештаји...");
		setItemCaptionGenerator(IzvestajTip::getNaziv);
		setEmptySelectionAllowed(false);
		setItems(lista());
		setWidth("100%");
	}
	
	private List<IzvestajTip> lista(){
		ArrayList<IzvestajTip> izvestaji = new ArrayList<IzvestajTip>();
		izvestaji.add(new IzvestajTip(1, "возило"));
		return izvestaji;
	}
}