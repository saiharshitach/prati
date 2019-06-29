package rs.cybertrade.prati.view.sistemSesije;

import java.util.ArrayList;
import com.github.appreciated.app.layout.annotations.MenuCaption;
import com.github.appreciated.app.layout.annotations.MenuIcon;
import com.github.appreciated.app.layout.annotations.NavigatorViewName;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import pratiBaza.tabele.SistemSesije;
import rs.cybertrade.prati.server.Servis;
import rs.cybertrade.prati.view.OpstiView;
import rs.cybertrade.prati.view.OpstiViewInterface;

@NavigatorViewName("sesije") // an empty view name will also be the default view
@MenuCaption("Сесије")
@MenuIcon(VaadinIcons.GLASSES)
public class SistemSesijeView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	public static final String VIEW_NAME = "sesije";
	private Grid<SistemSesije> tabela;
	private ListDataProvider<SistemSesije> dataProvider;
	private SerializablePredicate<SistemSesije> filterPredicate;
	private ArrayList<SistemSesije> pocetno, lista;
	
	public SistemSesijeView() {
		buildToolbar();
		buildTable();
		
		dodaj.setVisible(false);
		
		barGrid.addComponent(topLayout);
		barGrid.addComponent(tabela);
		barGrid.setExpandRatio(tabela, 1);
		
		addComponent(barGrid);
	}

	@Override
	public void buildTable() {
		tabela = new Grid<SistemSesije>();
		updateTable();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.SINGLE);
		if(korisnik.isSistem() && korisnik.getSistemPretplatnici() == null) {
			tabela.addColumn(sistemSesije -> sistemSesije.getSistemPretplatnici() == null ? "" : sistemSesije.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		tabela.addColumn(sistemSesije -> sistemSesije.getKorisnici() == null ? "" : (sistemSesije.getKorisnici().getIme() + " " + sistemSesije.getKorisnici().getPrezime())).setCaption("корисник");
		tabela.addColumn(SistemSesije::getIpAdresa).setCaption("ИП адреса");
		tabela.addColumn(SistemSesije::getDatumPocetak,  new DateRenderer(DANSATFORMAT)).setCaption("почетак").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(SistemSesije::getDatumKraj,  new DateRenderer(DANSATFORMAT)).setCaption("крај").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(sistemSesije -> sistemSesije.getOrganizacija() == null ? "" : sistemSesije.getOrganizacija().getNaziv()).setCaption("организација");
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
		pocetno = new ArrayList<SistemSesije>();
		lista = Servis.sistemSesijaServis.nadjiSveSesije(korisnik);
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		dataProvider = (ListDataProvider<SistemSesije>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<SistemSesije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(SistemSesije t) {
				return ((t.getSistemPretplatnici() == null ? "" : t.getSistemPretplatnici().getNaziv().toLowerCase()).contains(filter.getValue().toLowerCase()) ||
						(t.getOrganizacija() == null ? "" : t.getOrganizacija().getNaziv().toLowerCase()).contains(filter.getValue().toLowerCase()) ||
						(t.getKorisnici() == null ? "" : (t.getKorisnici().getIme() + " " + t.getKorisnici().getPrezime()).toLowerCase()).contains(filter.getValue().toLowerCase()) ||
						(t.getIpAdresa() == null ? "" : t.getIpAdresa()).contains(filter.getValue().toLowerCase()) ||
						(t.getDatumPocetak() ==  null ? "" : t.getDatumPocetak().toString().toLowerCase()).contains(filter.getValue().toLowerCase()) ||
						(t.getDatumKraj() ==  null ? "" : t.getDatumKraj().toString().toLowerCase()).contains(filter.getValue().toLowerCase()));
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
