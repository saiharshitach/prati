package rs.cybertrade.prati.view.organizacije;

import java.util.ArrayList;

import com.github.appreciated.app.layout.annotations.MenuCaption;
import com.github.appreciated.app.layout.annotations.MenuIcon;
import com.github.appreciated.app.layout.annotations.NavigatorViewName;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;

import pratiBaza.tabele.Organizacije;
import rs.cybertrade.prati.Servis;
import rs.cybertrade.prati.view.OpstiView;
import rs.cybertrade.prati.view.OpstiViewInterface;

@NavigatorViewName("organizacije") // an empty view name will also be the default view
@MenuCaption("Организације")
@MenuIcon(VaadinIcons.COGS)
public class OrganizacijeView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	private Grid<Organizacije> tabela;
	private ListDataProvider<Organizacije> dataProvider;
	private SerializablePredicate<Organizacije> filterPredicate;
	private ArrayList<Organizacije> pocetno, lista;

	public OrganizacijeView() {
		topLayout = buildToolbar();
		buildlayout();
		buildTable();
		barGrid.addComponent(topLayout);
		barGrid.addComponent(tabela);
		barGrid.setExpandRatio(tabela, 1);
		addComponent(barGrid);
	}
	
	@Override
	public void buildTable() {
		tabela = new Grid<Organizacije>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		if(korisnik.isSistem() && korisnik.getSistemPretplatnici() == null) {
			tabela.addColumn(organizacije -> organizacije.getSistemPretplatnici() == null ? "" : organizacije.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(Organizacije::getNaziv).setCaption("naziv");
		tabela.addColumn(Organizacije::getOpis).setCaption("опис");
		tabela.addComponentColumn(organizacije -> {CheckBox chb = new CheckBox(); if(organizacije.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(uredjaji -> "v-align-right");
		tabela.addColumn(Organizacije::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(uredjaji -> "v-align-right");
		tabela.addColumn(Organizacije::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(uredjaji -> "v-align-right");
	}

	@Override
	public void ocistiIzbor() {
		// TODO Auto-generated method stub
		
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
	public void izmeniPodatak() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ukloniPodatak() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTable() {
		filter.clear();
		pocetno = new ArrayList<Organizacije>();
		lista = Servis.organizacijaServis.nadjiSveOrganizacije(korisnik);
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<Organizacije>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<Organizacije>() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean test(Organizacije t) {
				// TODO Auto-generated method stub
				return false;
			}
		};
	}

	@Override
	public void osveziFilter() {
		// TODO Auto-generated method stub
		
	}

}
