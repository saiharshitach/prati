package rs.atekom.prati.view.grupe;

import java.util.ArrayList;
import org.vaadin.dialogs.ConfirmDialog;
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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Button.ClickListener;
import pratiBaza.tabele.Grupe;
import pratiBaza.tabele.GrupeObjekti;
import pratiBaza.tabele.Objekti;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiView;
import rs.atekom.prati.view.OpstiViewInterface;

@NavigatorViewName("grupeObjekti")
@MenuCaption("Групе Објеката")
@MenuIcon(VaadinIcons.RECORDS)
public class GrupeObjektiView extends OpstiView implements OpstiViewInterface{

	private static final long serialVersionUID = 1L;
	private Grid<Objekti> tabela;
	private ListDataProvider<Objekti> dataProvider;
	private SerializablePredicate<Objekti> filterPredicate;
	private ArrayList<Objekti> pocetno, lista;
	private GrupeObjektiView view;

	public GrupeObjektiView() {
		view = this;
		
		barGrid.addComponent(buildToolbarGrupe());
		buildTable();

		barGrid.addComponent(tabela);
		barGrid.setExpandRatio(tabela, 1);
		
		grupeCombo.addValueChangeListener(new ValueChangeListener<Grupe>() {	
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Grupe> event) {
				updateTable();
			}
		});
		
		potvrdi.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				if(grupeCombo.getValue() != null) {
					ConfirmDialog.show(view.getUI(), "Провера", "Сачувај унете податке?", "да", "не", new ConfirmDialog.Listener() {
					private static final long serialVersionUID = 1L;
					@Override
					public void onClose(ConfirmDialog dialog) {
						if(dialog.isConfirmed()) {
							Servis.grupeObjekatServis.izbrisiSveGrupaObjekti(grupeCombo.getValue());
							for(Objekti objekat: tabela.getSelectedItems()) {
								GrupeObjekti grupaObjekat = new GrupeObjekti();
								grupaObjekat.setSistemPretplatnici(pretplatniciCombo.getValue());
								grupaObjekat.setOrganizacija(organizacijeCombo.getValue());
								grupaObjekat.setGrupe(grupeCombo.getValue());
								grupaObjekat.setObjekti(objekat);
								Servis.grupeObjekatServis.unesiGrupaObjekat(grupaObjekat);
								}
							pokaziPorukuUspesno("подаци сачувани");
							}
						}
					});
					}else {
						pokaziPorukuGreska("морате одабрати групу!");
					}
				}
			});
		
		addComponent(barGrid);
	}

	@Override
	public void buildTable() {
		tabela = new Grid<Objekti>();
		pocetno = new ArrayList<Objekti>();
		updateTable();
		dodajFilter();
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

	@Override
	public void updateTable() {
		filter.clear();
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
	}

	@Override
	public void osveziFilter() {
		dataProvider.setFilter(filterPredicate);
		dataProvider.refreshAll();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void dodajFilter() {
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
	
	
}
