package rs.atekom.prati.view.komponente;

import java.util.ArrayList;
import com.vaadin.ui.ComboBox;

import pratiBaza.pomocne.TipServisa;

public class ComboTipServisa extends ComboBox<TipServisa>{

	private static final long serialVersionUID = 1L;

	public ComboTipServisa(String naziv, boolean gorivo, boolean obavezno) {
		setCaption(naziv);
		if(gorivo) {
			setPlaceholder("тип трошка...");
		}else {
			setPlaceholder("тип сервиса...");
		}
		setItemCaptionGenerator(TipServisa::getNaziv);
		setItems(lista(gorivo));
		setRequiredIndicatorVisible(obavezno);
		setWidth("100%");
	}
	
	public ArrayList<TipServisa> lista(boolean gorivo){
		ArrayList<TipServisa> lista = new ArrayList<TipServisa>();
		if(gorivo) {
			lista.add(new TipServisa("гориво", 0));
		}
		lista.add(new TipServisa("мали сервис", 1));
		lista.add(new TipServisa("велики сервис", 2));
		lista.add(new TipServisa("регистрација", 3));
		lista.add(new TipServisa("одржавање", 4));
		lista.add(new TipServisa("остало", 5));
		return lista;
	}
	
	public ArrayList<TipServisa> listaServisa(){
		ArrayList<TipServisa> lista = new ArrayList<TipServisa>();
		lista.add(new TipServisa("мали сервис", 1));
		lista.add(new TipServisa("велики сервис", 2));
		lista.add(new TipServisa("регистрација", 3));
		return lista;
	}
}



