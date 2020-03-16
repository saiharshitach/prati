package rs.atekom.prati.view.vozila.evidencija;

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
import com.vaadin.ui.Grid;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;

import pratiBaza.tabele.EvidencijaVoznji;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("evidencija")
@MenuCaption("Евиденција")
@MenuIcon(VaadinIcons.EDIT)
public class EvidencijaVoznjiView  extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public final String VIEW_NAME = "evidencija";
	private Grid<EvidencijaVoznji> tabela;
	private ListDataProvider<EvidencijaVoznji> dataProvider;
	private SerializablePredicate<EvidencijaVoznji> filterPredicate;
	private ArrayList<EvidencijaVoznji> pocetno, lista;
	private EvidencijaVoznjiLogika viewLogika;
	private EvidencijaVoznjiForma forma;
	private EvidencijaVoznji izabrani;
	
	public EvidencijaVoznjiView() {
		viewLogika = new EvidencijaVoznjiLogika(this);
		forma = new EvidencijaVoznjiForma(viewLogika);
		forma.setEnabled(false);
		
		buildToolbar();
		buildTable();
		
		tabela.addSelectionListener(new SelectionListener<EvidencijaVoznji>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<EvidencijaVoznji> event) {
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
		tabela = new Grid<EvidencijaVoznji>();
		pocetno = new ArrayList<EvidencijaVoznji>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		
		//tabela.addColumn(evidencija -> evidencija.getVoziloNalog() == null ? "" : evidencija.getVoziloNalog().getBrojNaloga()).setCaption("налог");
		tabela.addColumn(evidencija -> evidencija.getBrojPutnogNaloga() == null ? "" : evidencija.getBrojPutnogNaloga()).setCaption("бр путног налога");
		tabela.addColumn(evidencija -> evidencija.getVozac() == null ? "" : evidencija.getVozac().toString()).setCaption("возач");
		tabela.addColumn(evidencija -> evidencija.getRegistracijaVozila() == null ? "" : evidencija.getRegistracijaVozila()).setCaption("регистрација");
		tabela.addColumn(evidencija -> evidencija.getRelacija() == null ? "" : evidencija.getRelacija()).setCaption("релација");
		tabela.addColumn(EvidencijaVoznji::getDatumVremePolaska, new DateRenderer(DANSATFORMAT)).setCaption("полазак").setStyleGenerator(evidencija -> "v-align-right");
		tabela.addColumn(EvidencijaVoznji::getDatumVremeDolaska, new DateRenderer(DANSATFORMAT)).setCaption("долазак").setStyleGenerator(evidencija -> "v-align-right");
		tabela.addColumn(EvidencijaVoznji::getPocetnaKm).setCaption("почетна км").setStyleGenerator(evidencija -> "v-align-right");
		tabela.addColumn(EvidencijaVoznji::getZavrsnaKm).setCaption("завршна км").setStyleGenerator(evidencija -> "v-align-right");
		tabela.addColumn(EvidencijaVoznji::getRazlikaKm).setCaption("разлика км").setStyleGenerator(evidencija -> "v-align-right");
		tabela.addColumn(EvidencijaVoznji::getPotrosnja).setCaption("потрошња").setStyleGenerator(evidencija -> "v-align-right");
		tabela.addColumn(EvidencijaVoznji::getGorivoCena).setCaption("цена горива дин/лит").setStyleGenerator(evidencija -> "v-align-right");
		tabela.addColumn(EvidencijaVoznji::getPrevozCena).setCaption("превоз дин/км").setStyleGenerator(evidencija -> "v-align-right");
		tabela.addColumn(EvidencijaVoznji::getPutniTroskovi).setCaption("путни трошкови").setStyleGenerator(evidencija -> "v-align-right");
		
		tabela.addColumn(EvidencijaVoznji::getDana).setCaption("дана").setStyleGenerator(evidencija -> "v-align-right");
		tabela.addColumn(EvidencijaVoznji::getSati).setCaption("сати").setStyleGenerator(evidencija -> "v-align-right");
		tabela.addColumn(EvidencijaVoznji::getTroskoviGoriva).setCaption("трошак горива").setStyleGenerator(evidencija -> "v-align-right");
		tabela.addColumn(EvidencijaVoznji::getPrevozUkupno).setCaption("превоз укупно").setStyleGenerator(evidencija -> "v-align-right");
		tabela.addColumn(EvidencijaVoznji::getPrevozPutniTrosak).setCaption("превоз и путни").setStyleGenerator(evidencija -> "v-align-right");
		tabela.addColumn(evidencija -> evidencija.getPreuzetoIz() == null ? "" : evidencija.getPreuzetoIz()).setCaption("преузето из");
		tabela.addColumn(evidencija -> evidencija.getVrstaRobe() == null ? "" : evidencija.getVrstaRobe()).setCaption("врста робе");
		tabela.addColumn(EvidencijaVoznji::getKolicina).setCaption("количина ком/кг").setStyleGenerator(evidencija -> "v-align-right");
		tabela.addColumn(evidencija -> evidencija.getMagacin() == null ? "" : evidencija.getMagacin()).setCaption("магацин");
		tabela.addColumn(evidencija -> evidencija.getOtpremnica() == null ? "" : evidencija.getOtpremnica()).setCaption("отпремница");
		tabela.addColumn(evidencija -> evidencija.getSifra() == null ? "" : evidencija.getSifra()).setCaption("шифра партнера");
		tabela.addColumn(evidencija -> evidencija.getSifraPrograma() == null ? "" : evidencija.getSifraPrograma()).setCaption("шифра програма");
		tabela.addColumn(evidencija -> evidencija.getProgram() == null ? "" : evidencija.getProgram()).setCaption("назив програма");
		tabela.addColumn(EvidencijaVoznji::getUtrosenoLitara).setCaption("утрошено литара").setStyleGenerator(evidencija -> "v-align-right");
		tabela.addColumn(evidencija -> evidencija.getUradio() == null ? "" : evidencija.getUradio().toString()).setCaption("урадио");
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
		tabela.getSelectionModel().select((EvidencijaVoznji)red);
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
		EvidencijaVoznji evidencija = (EvidencijaVoznji)podatak;
		if(evidencija != null) {
			forma.addStyleName("visible");
			forma.setEnabled(true);
		}else {
			forma.removeStyleName("visible");
			forma.setEnabled(false);
		}
		forma.izmeniPodatak(evidencija);
	}

	@Override
	public void ukloniPodatak() {
		if(izabrani != null) {
			if(izabrani != null) {
				Servis.evidencijaServis.izbrisiEvidenciju(izabrani);
				pokaziPorukuUspesno("подаци избрисани");
			}else {
				pokaziPorukuGreska("подаци су већ избрисани!");
			}
		}
		
	}

	@Override
	public void updateTable() {
		filter.clear();
		lista = Servis.evidencijaServis.vratiEvidencije(korisnik.getSistemPretplatnici(), korisnik.getOrganizacija(), null, null, null, null, null);
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
		dataProvider = (ListDataProvider<EvidencijaVoznji>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<EvidencijaVoznji>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(EvidencijaVoznji t) {
				return ((t.getVoziloNalog() == null ? "" : t.getVoziloNalog().getBrojNaloga().toLowerCase()).contains(filter.getValue().toLowerCase()) ||
						(t.getVoziloNalog() == null ? "" : t.getVoziloNalog().getBrojNaloga().toLowerCase()).contains(filter.getValue().toLowerCase()));
			}
		};
		filter.addValueChangeListener(e -> {osveziFilter();});
	}

}
