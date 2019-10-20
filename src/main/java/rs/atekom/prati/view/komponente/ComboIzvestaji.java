package rs.atekom.prati.view.komponente;

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
		setEmptySelectionAllowed(false);
		setItems(lista());
		setWidth("100%");
	}
	
	private List<IzvestajTip> lista(){
		ArrayList<IzvestajTip> izvestaji = new ArrayList<IzvestajTip>();
		izvestaji.add(new IzvestajTip(7, "обд пређени пут"));
		izvestaji.add(new IzvestajTip(8, "обд стање"));
		izvestaji.add(new IzvestajTip(6, "гпс пређени пут"));
		izvestaji.add(new IzvestajTip(1, "пређени пут"));
		izvestaji.add(new IzvestajTip(2, "зоне"));
		izvestaji.add(new IzvestajTip(5, "стајање"));
		izvestaji.add(new IzvestajTip(3, "радно време ГПС"));
		izvestaji.add(new IzvestajTip(4, "гориво"));
		return izvestaji;
	}

}
