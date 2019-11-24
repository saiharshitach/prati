package rs.atekom.prati.view.vozila.izvestaji;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.github.appreciated.app.layout.annotations.MenuCaption;
import com.github.appreciated.app.layout.annotations.MenuIcon;
import com.github.appreciated.app.layout.annotations.NavigatorViewName;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import pratiBaza.pomocne.IzvestajTip;
import pratiBaza.tabele.Grupe;
import pratiBaza.tabele.Objekti;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiPanelView;
import rs.atekom.prati.view.komponente.ComboIzvestajiVozilo;
import rs.atekom.prati.view.komponente.ComboObjektiSaVozilima;
import rs.atekom.prati.view.komponente.ComboTipServisa;
import rs.atekom.prati.view.komponente.DatumVreme;
import rs.atekom.prati.view.komponente.TipServisa;

@NavigatorViewName("izvestajiVozila") // an empty view name will also be the default view
@MenuCaption("Извештаји")
@MenuIcon(VaadinIcons.BAR_CHART)
public class IzvestajiVozilaView extends OpstiPanelView{

	private static final long serialVersionUID = 1L;
	public ComboObjektiSaVozilima objektiCombo;
	public ComboIzvestajiVozilo izvestajiCombo;
	private HorizontalLayout parametri;
	private Panel podaci;
	private HorizontalLayout preuzimanje;
	private NativeSelect<Integer> satiOd, satiDo;
	private ClickListener prikaziClick;
	private ComboTipServisa tipTroska;
	
	public IzvestajiVozilaView() {
		root.addComponent(buildToolBarIzvestaji());
		podaci = new Panel();
		podaci.addStyleName(ValoTheme.PANEL_BORDERLESS);
		podaci.setSizeFull();
		podaci.setWidth("100%");
		tipTroska = new ComboTipServisa(null, true, false);
		postaviPretplatnikOrg();
		
		izvestajiCombo = new ComboIzvestajiVozilo();
		topLayout.addComponent(izvestajiCombo);
		
		objektiCombo = new ComboObjektiSaVozilima(pretplatniciCombo.getValue(), organizacijeCombo.getValue(), null, true, false);
        prikazi = new Button();
        prikazi.setIcon(VaadinIcons.CHECK);
        
		parametri = new HorizontalLayout();
		parametri.setSpacing(true);
		parametri.setMargin(false);
		parametri.setSizeUndefined();
		
		izvestajiCombo.addValueChangeListener(new ValueChangeListener<IzvestajTip>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<IzvestajTip> event) {
				ukloniSve();
				postaviParametre(event.getValue());
			}
		});
		
		grupeCombo.addValueChangeListener(new ValueChangeListener<Grupe>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Grupe> event) {
				objektiCombo.clear();
				if(event.getValue() != null) {
					objektiCombo.setItems(objektiCombo.lista(event.getValue()));
				}else {
					objektiCombo.setItems(Servis.objekatServis.nadjiSveObjekteSavozilom(pretplatniciCombo.getValue(), organizacijeCombo.getValue()));
				}
				ukloniSve();
			}
		});
		
		objektiCombo.addValueChangeListener(new ValueChangeListener<Objekti>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Objekti> event) {
				ukloniSve();
			}
		});
		
		tipTroska.addValueChangeListener(new ValueChangeListener<TipServisa>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<TipServisa> event) {
				ukloniSve();
			}
		});
		
		dodajPodatke();
		setContent(root);
	}

	private void postaviParametre(IzvestajTip tip) {
		brisanje();
		if(tip != null) {
			switch (tip.getRb()) {
			case 1: postaviParametreUkupnoTroskovi();
				break;

			default:
				break;
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void brisanje() {
		prikazi.getListeners(ClickEvent.class) .forEach( listener -> prikazi.removeListener(ClickEvent.class, listener));
		podaci.setContent(null);
		if(vremeOd != null)
			vremeOd.clear();
		if(vremeDo != null)
			vremeDo.clear();
		grupeCombo.clear();
		objektiCombo.clear();
		objektiCombo.setItems(new ArrayList<Objekti>());
		if(preuzimanje != null) {
			topLayout.removeComponent(preuzimanje);
			preuzimanje = null;
		}
		if(satiOd != null)
			satiOd.clear();
		if(satiDo != null)
			satiDo.clear();
		root.removeComponent(podaci);
		parametri.removeAllComponents();
	}
	
	private void postaviParametreUkupnoTroskovi() {
		vremeOd = new DatumVreme(false, "", 0, 0, -30);
        vremeDo = new DatumVreme(false, "", 0, 0, 0);
        vremeOd.setResolution(DateTimeResolution.DAY);
        vremeDo.setResolution(DateTimeResolution.DAY);
        vremeOd.addValueChangeListener(vremePromena);
        vremeDo.addValueChangeListener(vremePromena);
        
		prikaziClick = new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				if(pretplatniciCombo.getValue() != null) {

					ArrayList<Objekti> objekti = new ArrayList<>();
					if(preuzimanje != null) {
						topLayout.removeComponent(preuzimanje);
						preuzimanje = null;
					}
					Timestamp vremeOdTS = Timestamp.valueOf(vremeOd.getValue());
					Timestamp vremeDoTS = Timestamp.valueOf(vremeDo.getValue());
					if(vremeOd.getValue() != null && vremeDo.getValue() != null && vremeDoTS.after(vremeOdTS)) {
						if(objektiCombo.getValue() == null) {
							if(grupeCombo.getValue() == null) {
								objekti.addAll(Servis.objekatServis.nadjiSveObjekteSavozilom(pretplatniciCombo.getValue(), organizacijeCombo.getValue()));
							}else {
								objekti.addAll(Servis.grupeObjekatServis.nadjiSveObjektePoGrupiSaVozilom(grupeCombo.getValue()));
							}
						}else {
							objekti.add(objektiCombo.getValue());
						}
						Integer tipTroskaInt = null;
						if(tipTroska.getValue() != null) {
							tipTroskaInt = Integer.valueOf(tipTroska.getValue().getRb());
						}
						UkupniTroskoviLayout ukupniTroskovi = new UkupniTroskoviLayout(objekti, vremeOdTS, vremeDoTS, tipTroskaInt);
						preuzimanje = ukupniTroskovi.vratiPreuzimanje();
						topLayout.addComponent(preuzimanje);
						podaci.setContent(ukupniTroskovi);
						dodajPodatke();
					}else {
						pokaziPorukuGreska("морате одабрати време у оба поља и и време до мора бити касније од времена од!");
					}
				}else {
					pokaziPorukuGreska("морате одабрати претплатника!");
				}
			}
		};
		prikazi.addClickListener(prikaziClick);
		dodajParametreDatum(false, true);
	}
	
	public ValueChangeListener<LocalDateTime> vremePromena = new ValueChangeListener<LocalDateTime>() {
		private static final long serialVersionUID = 1L;
		@Override
		public void valueChange(ValueChangeEvent<LocalDateTime> event) {
			if(preuzimanje != null) {
				topLayout.removeComponent(preuzimanje);
				preuzimanje = null;
			}
		}
	};
	
	private void dodajParametreDatum(boolean dodajSate, boolean datumVremeOd) {
        if(dodajSate) {
            List<Integer> data = IntStream.range(0, 24).mapToObj(i -> i).collect(Collectors.toList());
            satiOd = new NativeSelect<>(null, data);
            satiOd.setEmptySelectionAllowed(false);
            satiOd.setSelectedItem(data.get(7));
            satiDo = new NativeSelect<>(null, data);
            satiDo.setEmptySelectionAllowed(false);
            satiDo.setSelectedItem(data.get(17));
            satiOd.addValueChangeListener(new ValueChangeListener<Integer>() {
				private static final long serialVersionUID = 1L;
				@Override
				public void valueChange(ValueChangeEvent<Integer> event) {
					if(preuzimanje != null) {
						topLayout.removeComponent(preuzimanje);
						preuzimanje = null;
					}
				}
			});
            satiDo.addValueChangeListener(new ValueChangeListener<Integer>() {
				private static final long serialVersionUID = 1L;
				@Override
				public void valueChange(ValueChangeEvent<Integer> event) {
					if(preuzimanje != null) {
						topLayout.removeComponent(preuzimanje);
						preuzimanje = null;
					}
				}
			});
            
            satiDo = new NativeSelect<>(null, data);
            satiDo.setEmptySelectionAllowed(false);
            satiDo.setSelectedItem(data.get(17));
        }
        
        if(isSistem()) {
        	parametri.addComponent(pretplatniciCombo);
        }else {
        	pretplatniciCombo.setValue(korisnik.getSistemPretplatnici());
        }
        if(isSistem() || (korisnik.isAdmin() && korisnik.getOrganizacija() == null)) {
        	parametri.addComponent(organizacijeCombo);
        }else {
        	organizacijeCombo.setValue(korisnik.getOrganizacija());
        }
        
        parametri.addComponent(grupeCombo);
        parametri.addComponent(objektiCombo);
        parametri.addComponent(tipTroska);
        if(datumVremeOd) {
        	parametri.addComponent(vremeOd);
        }
        parametri.addComponent(vremeDo);
        if(dodajSate) {
            parametri.addComponent(satiOd);
            parametri.addComponent(satiDo);
        }
        parametri.addComponent(prikazi);
        
        topLayout.addComponent(parametri);
	}
	
	private void dodajPodatke() {
		root.addComponent(podaci);
		root.setExpandRatio(podaci, 1);
	}
	
	private void ukloniSve() {
		podaci.setContent(null);
		if(preuzimanje != null) {
			topLayout.removeComponent(preuzimanje);
			preuzimanje = null;
		}
	}
}
