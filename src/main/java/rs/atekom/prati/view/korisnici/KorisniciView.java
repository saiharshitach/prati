package rs.atekom.prati.view.korisnici;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import com.github.appreciated.app.layout.annotations.MenuCaption;
import com.github.appreciated.app.layout.annotations.MenuIcon;
import com.github.appreciated.app.layout.annotations.NavigatorViewName;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;

import pratiBaza.tabele.GrupeKorisnici;
import pratiBaza.tabele.Korisnici;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("korisnici")
@MenuCaption("Корисници")
@MenuIcon(VaadinIcons.USER)
public class KorisniciView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "korisnici";
	private Grid<Korisnici> tabela;
	private ListDataProvider<Korisnici> dataProvider;
	private SerializablePredicate<Korisnici> filterPredicate;
	private ArrayList<Korisnici> pocetno, lista;
	private KorisniciLogika viewLogika;
	public KorisniciForma forma;
	private Korisnici izabrani;

	public KorisniciView() {
		viewLogika = new KorisniciLogika(this);
		forma = new KorisniciForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<Korisnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<Korisnici> event) {
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
		tabela.addComponentColumn(korisnici -> {CheckBox chb = new CheckBox(); if(korisnici.isAktivan()) {chb.setValue(true);} return chb;}).setCaption("активан").setStyleGenerator(korisnici -> "v-align-right");
		tabela.addColumn(Korisnici::getAktivanDo, new DateRenderer(DANFORMAT)).setCaption("активан до").setStyleGenerator(objekti -> "v-align-right");
		tabela.addComponentColumn(korisnici -> {CheckBox chb = new CheckBox(); if(korisnici.isKorisnik()) {chb.setValue(true);} return chb;}).setCaption("корисник").setStyleGenerator(korisnici -> "v-align-right");
		tabela.addComponentColumn(korisnici -> {CheckBox chb = new CheckBox(); if(korisnici.isVozac()) {chb.setValue(true);} return chb;}).setCaption("возач").setStyleGenerator(korisnici -> "v-align-right");
		tabela.addComponentColumn(korisnici -> {CheckBox chb = new CheckBox(); if(korisnici.isAdmin()) {chb.setValue(true);} return chb;}).setCaption("администратор").setStyleGenerator(korisnici -> "v-align-right");
		tabela.addColumn(Korisnici::getTelefon).setCaption("телефон");
		tabela.addColumn(Korisnici::getMobilni).setCaption("мобилни");
		tabela.addColumn(Korisnici::getIbutton).setCaption("и-дугме");
		tabela.addColumn(korisnici -> korisnici.getOrganizacija() == null ? "" : korisnici.getOrganizacija().getNaziv()).setCaption("организација");
		if(this.isAdmin()) {
			tabela.addComponentColumn(korisnici -> {CheckBox chb = new CheckBox(); if(korisnici.isSistem()) {chb.setValue(true);} return chb;}).setCaption("систем").setStyleGenerator(korisnici -> "v-align-right");
			tabela.addComponentColumn(korisnici -> {CheckBox chb = new CheckBox(); if(korisnici.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(korisnici -> "v-align-right");
		}
		tabela.addColumn(Korisnici::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(Korisnici::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
	}

	@Override
	public void enter(ViewChangeEvent event) {
		viewLogika.enter(event.getParameters());
	}

	@Override
	public void ocistiIzbor() {
		tabela.getSelectionModel().deselectAll();
	}

	@Override
	public void izaberiRed(Object red) {
		tabela.getSelectionModel().select((Korisnici)red);
	}

	@Override
	public Object dajIzabraniRed() {
		try {
			return tabela.getSelectionModel().getFirstSelectedItem().get();
		}catch (NoSuchElementException e) {
			return null;
		}
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		Korisnici korisnik = (Korisnici)podatak;
		if(korisnik != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(korisnik);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				ArrayList<GrupeKorisnici> grupeKorisnik = Servis.grupeKorisnikServis.vratiSveGrupeKorisnikPoKorisniku(izabrani);
				for(GrupeKorisnici grKorisnik: grupeKorisnik) {
					Servis.grupeKorisnikServis.izbrisiGrupaZaposleni(grKorisnik);
				}
				Servis.korisnikServis.izbrisiKorisnika(izabrani);
				pokaziPorukuUspesno("корисник " + izabrani.getIme() + " " + izabrani.getPrezime() + " је избрисан");
			}else {
				pokaziPorukuGreska("корисник је већ избрисан!");
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTable() {
		filter.clear();
		pocetno = new ArrayList<Korisnici>();
		lista = Servis.korisnikServis.nadjiSveKorisnike(korisnik, false);
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
				return ((t.getIme() == null ? "" : t.getIme().toLowerCase()).contains(filter.getValue().toLowerCase()) ||
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
