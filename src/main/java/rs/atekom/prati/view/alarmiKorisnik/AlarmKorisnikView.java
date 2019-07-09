package rs.atekom.prati.view.alarmiKorisnik;

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
import pratiBaza.tabele.AlarmiKorisnik;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("alarmiKorisnika")
@MenuCaption("Аларми")
@MenuIcon(VaadinIcons.MAILBOX)
public class AlarmKorisnikView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "alarmiKorisnika";
	private Grid<AlarmiKorisnik> tabela;
	private ListDataProvider<AlarmiKorisnik> dataProvider;
	private SerializablePredicate<AlarmiKorisnik> filterPredicate;
	private ArrayList<AlarmiKorisnik> pocetno, lista;
	private AlarmKorisnikLogika viewLogika;
	private AlarmKorisnikForma forma;
	private AlarmiKorisnik izabrani;
	
	public AlarmKorisnikView() {
		viewLogika = new AlarmKorisnikLogika(this);
		forma = new AlarmKorisnikForma(viewLogika);
		forma.removeStyleName("visible");
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<AlarmiKorisnik>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<AlarmiKorisnik> event) {
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
		tabela = new Grid<AlarmiKorisnik>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		if(korisnik.isSistem() && korisnik.getSistemPretplatnici() == null) {
			tabela.addColumn(alarmiKorisnik -> alarmiKorisnik.getSistemPretplatnici() == null ? "" : alarmiKorisnik.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		if(korisnik.isAdmin() && korisnik.getOrganizacija() == null) {
			tabela.addColumn(alarmiKorisnik -> alarmiKorisnik.getOrganizacija() == null ? "" : alarmiKorisnik.getOrganizacija().getNaziv()).setCaption("организација");
		}
		tabela.addColumn(alarmiKorisnik -> alarmiKorisnik.getKorisnik().toString()).setCaption("корисник");
		tabela.addColumn(alarmiKorisnik -> alarmiKorisnik.getObjekti().getOznaka()).setCaption("објекат");
		tabela.addColumn(alarmiKorisnik -> alarmiKorisnik.getSistemAlarmi().getNaziv()).setCaption("аларм");
		tabela.addComponentColumn(alarmiKorisnik -> {CheckBox chb = new CheckBox(); if(alarmiKorisnik.isEmail()) {chb.setValue(true);} return chb;}).setCaption("е-пошта").setStyleGenerator(alarmiKorisnik -> "v-align-right");
		tabela.addComponentColumn(alarmiKorisnik -> {CheckBox chb = new CheckBox(); if(alarmiKorisnik.isObavestenje()) {chb.setValue(true);} return chb;}).setCaption("обавештење").setStyleGenerator(alarmiKorisnik -> "v-align-right");
		tabela.addComponentColumn(alarmiKorisnik -> {CheckBox chb = new CheckBox(); if(alarmiKorisnik.isAktivan()) {chb.setValue(true);} return chb;}).setCaption("активан").setStyleGenerator(alarmiKorisnik -> "v-align-right");
		tabela.addColumn(AlarmiKorisnik::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(AlarmiKorisnik::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
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
		tabela.getSelectionModel().select((AlarmiKorisnik)red);
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
		AlarmiKorisnik alarmKorisnik = (AlarmiKorisnik)podatak;
		if(alarmKorisnik != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(alarmKorisnik);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			Servis.alarmKorisnikServis.izbrisiAlarmiKorisnik(izabrani);
			pokaziPorukuUspesno("аларм је избрисан");
		}else {
			pokaziPorukuGreska("аларм је већ избрисан!");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTable() {
		filter.clear();
		pocetno = new ArrayList<AlarmiKorisnik>();
		lista = Servis.alarmKorisnikServis.nadjiSveAlarmePoKorisniku(korisnik, false, false, false);
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<AlarmiKorisnik>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<AlarmiKorisnik>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(AlarmiKorisnik t) {
				return ((t.getKorisnik() == null ? "" : t.getKorisnik().toString().toLowerCase()).contains(filter.getValue().toLowerCase()) ||
						(t.getObjekti() == null ? "" : t.getObjekti().getOznaka().toLowerCase()).contains(filter.getValue().toLowerCase()) ||
						(t.getSistemAlarmi() == null ? "" : t.getSistemAlarmi().getNaziv().toLowerCase()).contains(filter.getValue().toLowerCase()));
			}
		};
	}

	@Override
	public void osveziFilter() {
		dataProvider.setFilter(filterPredicate);
		dataProvider.refreshAll();
	}

}
