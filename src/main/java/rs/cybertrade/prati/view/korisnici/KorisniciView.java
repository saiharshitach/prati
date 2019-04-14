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
import com.vaadin.ui.renderers.DateRenderer;

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
		if(korisnik.isSistem() && korisnik.getSistemPretplatnici() == null) {
			tabela.addColumn(korisnici -> korisnici.getSistemPretplatnici() == null ? "" : korisnici.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(Korisnici::getIme).setCaption("име");
		tabela.addColumn(Korisnici::getPrezime).setCaption("презиме");
		tabela.addColumn(Korisnici::getEmail).setCaption("е-пошта");
		tabela.addColumn(Korisnici::getLozinka).setCaption("лозинка");
		tabela.addComponentColumn(korisnici -> {CheckBox chb = new CheckBox(); if(korisnici.isAktivan()) {chb.setValue(true);} return chb;}).setCaption("активан").setStyleGenerator(korisnici -> "v-align-right");
		tabela.addColumn(Korisnici::getAktivanDo, new DateRenderer(DANFORMAT)).setCaption("активан до").setStyleGenerator(objekti -> "v-align-right");
		tabela.addComponentColumn(korisnici -> {CheckBox chb = new CheckBox(); if(korisnici.isKorisnik()) {chb.setValue(true);} return chb;}).setCaption("корисник").setStyleGenerator(korisnici -> "v-align-right");
		tabela.addComponentColumn(korisnici -> {CheckBox chb = new CheckBox(); if(korisnici.isVozac()) {chb.setValue(true);} return chb;}).setCaption("возач").setStyleGenerator(korisnici -> "v-align-right");
		tabela.addComponentColumn(korisnici -> {CheckBox chb = new CheckBox(); if(korisnici.isAdmin()) {chb.setValue(true);} return chb;}).setCaption("администратор").setStyleGenerator(korisnici -> "v-align-right");
		tabela.addColumn(Korisnici::getTelefon).setCaption("телефон");
		tabela.addColumn(Korisnici::getMobilni).setCaption("мобилни");
		tabela.addColumn(Korisnici::getIbutton).setCaption("и-дугме");
		if(korisnik.isSistem() && korisnik.getSistemPretplatnici() == null) {
			tabela.addComponentColumn(korisnici -> {CheckBox chb = new CheckBox(); if(korisnici.isSistem()) {chb.setValue(true);} return chb;}).setCaption("систем").setStyleGenerator(korisnici -> "v-align-right");
		}
		tabela.addColumn(korisnici -> korisnici.getOrganizacija() == null ? "" : korisnici.getOrganizacija().getNaziv()).setCaption("организација");
		tabela.addComponentColumn(korisnici -> {CheckBox chb = new CheckBox(); if(korisnici.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(korisnici -> "v-align-right");
		tabela.addColumn(Korisnici::getIzmenjeno, new DateRenderer(DANFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(Korisnici::getKreirano, new DateRenderer(DANFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
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
	public void izmeniPodatak(Object podatak) {
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
				return ((t.getIme().toLowerCase() == null ? "" : t.getIme()).contains(filter.getValue().toLowerCase()) ||
						(t.getPrezime() == null ? "" : t.getPrezime()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getEmail() == null ? "" : t.getEmail()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
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
