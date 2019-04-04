package rs.cybertrade.prati.view.korisnici;

import java.util.ArrayList;
import com.github.appreciated.app.layout.annotations.MenuCaption;
import com.github.appreciated.app.layout.annotations.MenuIcon;
import com.github.appreciated.app.layout.annotations.NavigatorViewName;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import pratiBaza.tabele.Korisnici;
import rs.cybertrade.prati.Servis;
import rs.cybertrade.prati.view.OpstiView;
import rs.cybertrade.prati.view.OpstiViewInterface;

@NavigatorViewName("korisnici")
@MenuCaption("Корисници")
@MenuIcon(VaadinIcons.USER)
public class KorisniciView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	private Grid<Korisnici> tabela;
	private ListDataProvider<Korisnici> dataProvider;
	private SerializablePredicate<Korisnici> filterPredicate;
	private ArrayList<Korisnici> pocetno, lista;

	public KorisniciView() {
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
		tabela = new Grid<Korisnici>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		tabela.addColumn(korisnici -> korisnici.getSistemPretplatnici() == null ? "" : korisnici.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		tabela.addColumn(korisnici -> korisnici.getOrganizacija() == null ? "" : korisnici.getOrganizacija().getNaziv()).setCaption("организација");
		tabela.addColumn(Korisnici::getIme).setCaption("име");
		tabela.addColumn(Korisnici::getPrezime).setCaption("презиме");
		tabela.addColumn(Korisnici::getEmail).setCaption("е-пошта");
		tabela.addColumn(Korisnici::getLozinka).setCaption("лозинка");
		tabela.addComponentColumn(korisnici -> {CheckBox chb = new CheckBox(); if(korisnici.isVozac()) {chb.setValue(true);} return chb;}).setCaption("возач");
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
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTable() {
		filter.clear();
		pocetno = new ArrayList<Korisnici>();
		lista = Servis.korisnikServis.nadjiSveKorisnike((Korisnici) VaadinSession.getCurrent().getAttribute(Korisnici.class.getName()));
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<Korisnici>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<Korisnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(Korisnici t) {
				return (t.getIme().toLowerCase().contains(filter.getValue().toLowerCase()) ||
						t.getPrezime().toLowerCase().contains(filter.getValue().toLowerCase()) ||
						t.getEmail().toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getIbutton() == null ? "" : t.getIbutton()).toLowerCase().contains(filter.getValue().toLowerCase()));
			}
		};
		filter.addValueChangeListener(e -> {osveziFilter();});
	}

	@Override
	public void osveziFilter() {
		dataProvider.setFilter(filterPredicate);
		dataProvider.refreshAll();
	}

}