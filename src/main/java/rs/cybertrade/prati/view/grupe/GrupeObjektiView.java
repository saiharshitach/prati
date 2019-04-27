package rs.cybertrade.prati.view.grupe;

import java.util.ArrayList;
import com.github.appreciated.app.layout.annotations.MenuCaption;
import com.github.appreciated.app.layout.annotations.MenuIcon;
import com.github.appreciated.app.layout.annotations.NavigatorViewName;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import pratiBaza.tabele.Grupe;
import pratiBaza.tabele.GrupeObjekti;
import pratiBaza.tabele.Objekti;
import rs.cybertrade.prati.Servis;
import rs.cybertrade.prati.view.OpstiView;
import rs.cybertrade.prati.view.OpstiViewInterface;

@NavigatorViewName("grupeObjekti")
@MenuCaption("Групе Објеката")
@MenuIcon(VaadinIcons.RECORDS)
public class GrupeObjektiView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	private Grid<Objekti> tabela;
	private ListDataProvider<Objekti> dataProvider;
	private SerializablePredicate<Objekti> filterPredicate;
	private ArrayList<Objekti> pocetno, lista;

	public GrupeObjektiView() {

		buildlayout();
		buildTable();
		
		barGrid.addComponent(buildToolbarGrupe());
		barGrid.addComponent(tabela);
		barGrid.setExpandRatio(tabela, 1);
		grupeCombo.addValueChangeListener(new ValueChangeListener<Grupe>() {	
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Grupe> event) {
				updateTable();
			}
		});
		
		addComponent(barGrid);
	}

	@Override
	public void buildTable() {
		tabela = new Grid<Objekti>();
		tabela.setSizeFull();
		tabela.setStyleName("list");
		tabela.setSelectionMode(SelectionMode.MULTI);
		tabela.addColumn(Objekti::getOznaka).setCaption("ознака");
		tabela.addColumn(objekti -> objekti.getUredjaji() == null ? "" : objekti.getUredjaji().getSerijskiBr()).setCaption("уређај сер.бр");
		tabela.addColumn(objekti -> objekti.getUredjaji() == null ? "" : objekti.getUredjaji().getSim() == null ? "" : objekti.getUredjaji().getSim().getBroj()).setCaption("сим број");
		tabela.addComponentColumn(objekti -> {CheckBox chb = new CheckBox(); if(objekti.getTip()) {chb.setValue(true);} return chb;}).setCaption("возило").setStyleGenerator(objekti -> "v-align-right");
		tabela.addColumn(objekti -> objekti.getOrganizacija() == null ? "" : objekti.getOrganizacija().getNaziv()).setCaption("организација");
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
		pocetno = new ArrayList<Objekti>();
		lista = Servis.objekatServis.vratiSveObjekte(pretplatniciCombo.getValue(), organizacijeCombo.getValue());
		if(lista != null) {
			tabela.setItems(lista);
		}else {
			tabela.setItems(pocetno);
		}
		ArrayList<GrupeObjekti> uGrupi = Servis.grupeObjekatServis.nadjiSveGrupaObjektePoGrupi(grupeCombo.getValue());
		for(GrupeObjekti grupaObjekat : uGrupi) {
			for(Objekti objekat : lista) {
				if(grupaObjekat.getObjekti().getId().equals(objekat.getId())) {
					tabela.getSelectionModel().select(objekat);
				}
			}
		}
		dataProvider = (ListDataProvider<Objekti>)tabela.getDataProvider();
		filterPredicate = new SerializablePredicate<Objekti>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(Objekti t) {
				return (t.getOznaka().toLowerCase().contains(filter.getValue().toLowerCase()) ||
						t.getUredjaji().getSerijskiBr().toLowerCase().contains(filter.getValue().toLowerCase()) ||
						t.getUredjaji().getSim().getBroj().toLowerCase().contains(filter.getValue().toLowerCase()) ||
						t.getUredjaji().getSim().getIccid().toLowerCase().contains(filter.getValue().toLowerCase()));
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
