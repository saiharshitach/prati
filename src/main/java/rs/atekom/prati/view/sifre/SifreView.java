package rs.atekom.prati.view.sifre;

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
import pratiBaza.tabele.Sifre;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("sifre")
@MenuCaption("Шифре партнера")
@MenuIcon(VaadinIcons.CODE)
public class SifreView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "projekti";
	private Grid<Sifre> tabela;
	private ListDataProvider<Sifre> dataProvider;
	private SerializablePredicate<Sifre> filterPredicate;
	private ArrayList<Sifre> pocetno, lista;
	private SifreLogika viewLogika;
	private SifreForma forma;
	private Sifre izabrani;

	public SifreView() {
		viewLogika = new SifreLogika(this);
		forma = new SifreForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<Sifre>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<Sifre> event) {
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
	public void enter(ViewChangeEvent event) {
		viewLogika.enter(event.getParameters());
	}
	
	@Override
	public void buildTable() {
		tabela = new Grid<Sifre>();
		pocetno = new ArrayList<Sifre>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		
		if(isSistem()) {
			tabela.addColumn(projekti -> projekti.getSistemPretplatnici() == null ? "" : projekti.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(sifra -> sifra.getPartner() == null ? "" : sifra.getPartner().getNaziv()).setCaption("партнер");
		tabela.addColumn(Sifre::getSifra).setCaption("шифра");
		tabela.addColumn(Sifre::getOpis).setCaption("опис");
		if(isSistem() || (korisnik.isAdmin() && korisnik.getOrganizacija() == null)) {
			tabela.addColumn(sifra -> sifra.getOrganizacija() == null ? "" : sifra.getOrganizacija().getNaziv()).setCaption("организација");
		}
		if(isSistem()) {
			tabela.addComponentColumn(sifra -> {CheckBox chb = new CheckBox(); if(sifra.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("izbrisan").setStyleGenerator(objekti -> "v-align-right");
		}
		tabela.addColumn(Sifre::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(Sifre::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
	}

	@Override
	public void ocistiIzbor() {
		tabela.getSelectionModel().deselectAll();
	}

	@Override
	public void izaberiRed(Object red) {
		tabela.getSelectionModel().select((Sifre)red);
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
		Sifre sifra = (Sifre)podatak;
		if(sifra != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(sifra);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(!izabrani.isIzbrisan()) {
				Servis.sifraServis.izbrisiSifru(izabrani);
				pokaziPorukuUspesno("шифра избрисана");
			}else {
				pokaziPorukuGreska("шифра већ избрисана!");
			}
		}
	}

	@Override
	public void updateTable() {
		filter.clear();
		lista = Servis.sifraServis.nadjiSveSifre(korisnik);
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dodajFilter();
	}

	@Override
	public void osveziFilter() {
		dataProvider.setFilter(filterPredicate);
		dataProvider.refreshAll();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void dodajFilter() {
		dataProvider = (ListDataProvider<Sifre>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<Sifre>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(Sifre t) {
				return ((t.getSistemPretplatnici() == null ? "" : t.getSistemPretplatnici().getNaziv()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getSifra() == null ? "" : t.getSifra()).toLowerCase().contains(filter.getValue().toLowerCase()) ||
						(t.getPartner() == null ? "" : t.getPartner().getNaziv()).toLowerCase().contains(filter.getValue().toLowerCase()));
			}
		};
		filter.addValueChangeListener(e -> {osveziFilter();});
	}

}
