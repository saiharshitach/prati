package rs.atekom.prati.view.vozila.zbirni;

import java.util.ArrayList;
import com.github.appreciated.app.layout.annotations.MenuCaption;
import com.github.appreciated.app.layout.annotations.MenuIcon;
import com.github.appreciated.app.layout.annotations.NavigatorViewName;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.SelectionMode;
import pratiBaza.tabele.Racuni;
import pratiBaza.tabele.RacuniRaspodela;
import pratiBaza.tabele.Troskovi;
import rs.atekom.prati.view.Opsti;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("zbirni")
@MenuCaption("Збирни")
@MenuIcon(VaadinIcons.WALLET)
public class RacuniView extends Opsti implements OpstiViewInterface{
	
	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "zbirni";
	private Grid<Racuni> tabela;
	private ListDataProvider<Racuni> dataProvider;
	private SerializablePredicate<Racuni> filterPredicate;
	private ArrayList<Racuni> pocetno, lista;
	private RacuniLogika viewLogika;
	private RacuniForma forma;
	private Racuni izabrani;
	
	private Grid<RacuniRaspodela> raspodela;
	private Grid<Troskovi> troskovi;
	
	public RacuniView() {
		viewLogika = new RacuniLogika(this);
		forma = new RacuniForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<Racuni>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<Racuni> event) {
				if(event.getFirstSelectedItem().isPresent()) {
					izabrani = event.getFirstSelectedItem().get();
				}else {
					izabrani = null;
				}
				viewLogika.redIzabran(izabrani);
			}
		});
		
		dodaj.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				viewLogika.noviPodatak();
			}
		});
		
		barGrid.addComponent(topLayout);
		barGrid.addComponent(tabela);
		barGrid.setExpandRatio(tabela, 1);
		
		addComponent(barGrid);
		addComponent(forma);
		
		viewLogika.init();
	}
	
	@Override
	public void buildTable() {
		tabela = new Grid<Racuni>();
		pocetno = new ArrayList<Racuni>();
		updateTable();
		tabela.setSizeFull();
		tabela.setHeight("25%");
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
	}

	@Override
	public void ocistiIzbor() {
		
	}

	@Override
	public void izaberiRed(Object red) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object dajIzabraniRed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ukloniPodatak() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateTable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void osveziFilter() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dodajFilter() {
		// TODO Auto-generated method stub
		
	}

}
