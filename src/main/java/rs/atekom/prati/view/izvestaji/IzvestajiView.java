package rs.atekom.prati.view.izvestaji;

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
import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.Obd;
import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.SistemAlarmi;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiPanelView;
import rs.atekom.prati.view.izvestaji.nivoGoriva.NivoGorivaLayout;
import rs.atekom.prati.view.izvestaji.nivoGoriva.PregledNivoGoriva;
import rs.atekom.prati.view.komponente.DatumVreme;
import rs.atekom.prati.view.komponente.combo.ComboIzvestaji;
import rs.atekom.prati.view.komponente.combo.ComboObjekti;

@NavigatorViewName("izvestaji") // an empty view name will also be the default view
@MenuCaption("Извештаји")
@MenuIcon(VaadinIcons.BAR_CHART)
public class IzvestajiView extends OpstiPanelView{

	/*@WebServlet("/izvestaj-slika")
    public static class ReportsImageServlet extends ImageServlet {
		private static final long serialVersionUID = 1L;
	}**/
	private static final long serialVersionUID = 1L;
	public ComboObjekti objektiCombo;
	public ComboIzvestaji izvestajiCombo;
	private HorizontalLayout parametri;
	private Panel podaci;
	private HorizontalLayout preuzimanje;
	private NativeSelect<Integer> satiOd, satiDo;
	private ClickListener prikaziClick;
	
	public IzvestajiView() {
		root.addComponent(buildToolBarIzvestaji());
		podaci = new Panel();
		podaci.addStyleName(ValoTheme.PANEL_BORDERLESS);
		podaci.setSizeFull();
		podaci.setWidth("100%");
		postaviPretplatnikOrg();
		
		izvestajiCombo = new ComboIzvestaji();
		topLayout.addComponent(izvestajiCombo);

        objektiCombo = new ComboObjekti(null, null, true, false);
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
				if(preuzimanje != null) {
					topLayout.removeComponent(preuzimanje);
				}
				postaviParametre(event.getValue());
			}
		});
		
		grupeCombo.addValueChangeListener(new ValueChangeListener<Grupe>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Grupe> event) {
				objektiCombo.clear();
				podaci.setContent(null);
				if(event.getValue() != null) {
					objektiCombo.setItems(Servis.grupeObjekatServis.nadjiSveObjektePoGrupi(event.getValue()));
				}else {
					objektiCombo.setItems(objektiCombo.lista(korisnik));
				}
				if(preuzimanje != null) {
					topLayout.removeComponent(preuzimanje);
					preuzimanje = null;
				}
			}
		});
		
		objektiCombo.addValueChangeListener(new ValueChangeListener<Objekti>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Objekti> event) {
				podaci.setContent(null);
				if(preuzimanje != null) {
					topLayout.removeComponent(preuzimanje);
					preuzimanje = null;
				}
			}
		});
		
		dodajPodatke();
		setContent(root);
	}
	
	private void postaviParametre(IzvestajTip tip) {
		brisanje();
		if(tip != null) {
			switch (tip.getRb()) {
			case 1: postaviParametrePredjeniPut(tip.getRb());
				break;
			case 2: postaviParametreZone();
			    break;
			case 3: postaviParametreRadnoVremeGPS();
				break;
			case 4: postaviParametreNivoGoriva();
			    break;
			case 5: postaviParametreStajanje();
		        break;
			case 6: postaviParametrePredjeniPut(tip.getRb());
	            break;
			case 7: postaviParametrePredjeniPut(tip.getRb());
	            break;
			case 8: postaviParametreStanje();
                break;
			case 9: postaviParametrePredjeniPut(tip.getRb());
			    break;
			case 10: postaviParametrePredjeniPut(tip.getRb());
			    break;
			case 11: postaviParametrePredjeniPut(tip.getRb());
		        break;
			default:
				break;
			}
		}
	}
	
	private void postaviParametrePredjeniPut(int izvestaj) {
		vremeOd = new DatumVreme(false, "", 0, 0, 0);
        vremeDo = new DatumVreme(false, "", 0, 0, 1);
        vremeOd.addValueChangeListener(vremePromena);
        vremeDo.addValueChangeListener(vremePromena);
        
        prikaziClick = new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				ArrayList<Objekti> objekti = new ArrayList<Objekti>();
				if(preuzimanje != null) {
					topLayout.removeComponent(preuzimanje);
					preuzimanje = null;
				}
				if(vremeOd.getValue() != null && vremeDo.getValue() != null && 
						((Timestamp.valueOf(vremeDo.getValue()).getTime() - Timestamp.valueOf(vremeOd.getValue()).getTime())/1000 <= 61*24*60*60)) {
					if(grupeCombo.getValue() == null && objektiCombo.getValue() == null) {
						pokaziPorukuGreska("морате одабрати групу!");
					}else {//ako ima ili objekat ili grupaObjekata
						if(objektiCombo.getValue() == null) {
							objekti.addAll(Servis.grupeObjekatServis.nadjiSveObjektePoGrupi(grupeCombo.getValue()));
							}else {
								objekti.add(objektiCombo.getValue());
							}
						switch(izvestaj) {
						case 1: PredjeniPutLayout predjeniPut = new PredjeniPutLayout(objekti, Timestamp.valueOf(vremeOd.getValue()), Timestamp.valueOf(vremeDo.getValue()));
						        preuzimanje = predjeniPut.vratiPreuzimanje();
						        topLayout.addComponent(preuzimanje);
						        podaci.setContent(predjeniPut);
							
							/*if(objektiCombo.getValue() != null) {}else {
								pokaziPorukuGreska("морате одабрати објекат!");
								}*/
						    break;
						case 6: PredjeniPutGPSLayout predjeniPutGPS = new PredjeniPutGPSLayout(objekti, Timestamp.valueOf(vremeOd.getValue()), Timestamp.valueOf(vremeDo.getValue()));
						        preuzimanje = predjeniPutGPS.vratiPreuzimanje();
						        topLayout.addComponent(preuzimanje);
						        podaci.setContent(predjeniPutGPS);
						    break;
						case 7: PredjeniPutOBDLayout predjeniPut2 = new PredjeniPutOBDLayout(objekti, Timestamp.valueOf(vremeOd.getValue()), Timestamp.valueOf(vremeDo.getValue()));
						        preuzimanje = predjeniPut2.vratiPreuzimanje();
						        topLayout.addComponent(preuzimanje);
						        podaci.setContent(predjeniPut2);
							break;
						case 9: KontrolaGorivaLayout kontrolaGoriva = new KontrolaGorivaLayout(objekti, Timestamp.valueOf(vremeOd.getValue()), Timestamp.valueOf(vremeDo.getValue()));
						        preuzimanje = kontrolaGoriva.vratiPreuzimanje();
						        topLayout.addComponent(preuzimanje);
						        podaci.setContent(kontrolaGoriva);
							break;
						case 10:GorivoSaCenamaLayout gorivoSaCenama = new GorivoSaCenamaLayout(objekti, Timestamp.valueOf(vremeOd.getValue()), Timestamp.valueOf(vremeDo.getValue()));
						        preuzimanje = gorivoSaCenama.vratiPreuzimanje();
						        topLayout.addComponent(preuzimanje);
						        podaci.setContent(gorivoSaCenama);
						    break;
						case 11:KontrolaTocenjaLayout kontrolaTocenja = new KontrolaTocenjaLayout(objekti, Timestamp.valueOf(vremeOd.getValue()), Timestamp.valueOf(vremeDo.getValue()));
						        preuzimanje = kontrolaTocenja.vratiPreuzimanje();
						        topLayout.addComponent(preuzimanje);
						        podaci.setContent(kontrolaTocenja);
						     break;
						default:
							
						}

						dodajPodatke();
					}
				}else {
					pokaziPorukuGreska("морате одабрати време у оба поља и период може бити највише 31 дан!");
				}
			}
		};
		
		prikazi.addClickListener(prikaziClick);
		dodajParametreDatum(false, true);
	}
	
	private void postaviParametreStanje() {
		vremeDo = new DatumVreme(false, "", 0, 0, 1);
		vremeDo.addValueChangeListener(vremePromena);
		
		prikazi.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				ArrayList<Objekti> objekti = new ArrayList<Objekti>();
				if(preuzimanje != null) {
					topLayout.removeComponent(preuzimanje);
					preuzimanje = null;
				}
				if(vremeDo.getValue() != null) {
					if(grupeCombo.getValue() == null && objektiCombo.getValue() == null) {
						pokaziPorukuGreska("морате одабрати групу!");
					}else {
						if(objektiCombo.getValue() == null) {
							objekti.addAll(Servis.grupeObjekatServis.nadjiSveObjektePoGrupi(grupeCombo.getValue()));
							}else {
								objekti.add(objektiCombo.getValue());
							}
						StanjeOBDLayout izvestaj = new StanjeOBDLayout(objekti, Timestamp.valueOf(vremeDo.getValue()));
						preuzimanje = izvestaj.vratiPreuzimanje();
						topLayout.addComponent(preuzimanje);
						podaci.setContent(izvestaj);
						dodajPodatke();
					}
				}
			}
		});
		dodajParametreDatum(false, false);
	}
	
	private void postaviParametreZone() {
		vremeOd = new DatumVreme(false, "", 0, 0, 0);
        vremeDo = new DatumVreme(false, "", 0, 0, 1);
        vremeOd.addValueChangeListener(vremePromena);
        vremeDo.addValueChangeListener(vremePromena);
        
		prikazi.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				ArrayList<Objekti> objekti = new ArrayList<Objekti>();
				if(preuzimanje != null) {
					topLayout.removeComponent(preuzimanje);
					preuzimanje = null;
				}
				if(vremeOd.getValue() != null && vremeDo.getValue() != null && 
						((Timestamp.valueOf(vremeDo.getValue()).getTime() - Timestamp.valueOf(vremeOd.getValue()).getTime())/1000 < 32*86400)) {
					if(grupeCombo.getValue() == null && objektiCombo.getValue() == null) {
						pokaziPorukuGreska("морате одабрати групу или објекат!");
					}else {//ako ima ili objekat ili grupaObjekata
						if(objektiCombo.getValue() == null) {
							objekti.addAll(Servis.grupeObjekatServis.nadjiSveObjektePoGrupi(grupeCombo.getValue()));
							}else {
								objekti.add(objektiCombo.getValue());
							}
						ArrayList<SistemAlarmi> alarmi = new ArrayList<SistemAlarmi>();
						alarmi.add(Servis.sistemAlarmServis.nadjiAlarmPoSifri("1101"));
						alarmi.add(Servis.sistemAlarmServis.nadjiAlarmPoSifri("1100"));
						ZoneLayout zone = new ZoneLayout(objekti, Timestamp.valueOf(vremeOd.getValue()), Timestamp.valueOf(vremeDo.getValue()), alarmi);
						preuzimanje = zone.vratiPreuzimanje();
						topLayout.addComponent(preuzimanje);
						podaci.setContent(zone);
						dodajPodatke();
					}
				}else {
					pokaziPorukuGreska("морате одабрати време у оба поља и период може бити највише 31 дан!");
				}
			}
		});
        
		dodajParametreDatum(false, true);
	}
	
	private void postaviParametreStajanje() {
		vremeOd = new DatumVreme(false, "", 0, 0, 0);
        vremeDo = new DatumVreme(false, "", 0, 0, 1);
        vremeOd.addValueChangeListener(vremePromena);
        vremeDo.addValueChangeListener(vremePromena);
        
		prikazi.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				ArrayList<Objekti> objekti = new ArrayList<Objekti>();
				if(preuzimanje != null) {
					topLayout.removeComponent(preuzimanje);
					preuzimanje = null;
				}
				if(vremeOd.getValue() != null && vremeDo.getValue() != null && 
						((Timestamp.valueOf(vremeDo.getValue()).getTime() - Timestamp.valueOf(vremeOd.getValue()).getTime())/1000 < 32*86400)) {
					if(grupeCombo.getValue() == null && objektiCombo.getValue() == null) {
						pokaziPorukuGreska("морате одабрати групу!");
					}else {//ako ima ili objekat ili grupaObjekata
						if(objektiCombo.getValue() == null) {
							objekti.addAll(Servis.grupeObjekatServis.nadjiSveObjektePoGrupi(grupeCombo.getValue()));
							}else {
								objekti.add(objektiCombo.getValue());
							}
						StajanjeLayout stajanje = new StajanjeLayout(objekti, Timestamp.valueOf(vremeOd.getValue()), Timestamp.valueOf(vremeDo.getValue()), 60);
						preuzimanje = stajanje.vratiPreuzimanje();
						topLayout.addComponent(preuzimanje);
						podaci.setContent(stajanje);
						dodajPodatke();
					}
				}else {
					pokaziPorukuGreska("морате одабрати време у оба поља и период може бити највише 31 дан!");
				}
			}
		});
        
		dodajParametreDatum(false, true);
	}
	
	private void postaviParametreRadnoVremeGPS() {
		vremeOd = new DatumVreme(false, "", 0, 0, 0);
        vremeDo = new DatumVreme(false, "", 0, 0, 1);
		vremeOd.setResolution(DateTimeResolution.DAY);
		vremeDo.setResolution(DateTimeResolution.DAY);
        vremeOd.addValueChangeListener(vremePromena);
        vremeDo.addValueChangeListener(vremePromena);
        
		prikazi.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				if(preuzimanje != null) {
					topLayout.removeComponent(preuzimanje);
					preuzimanje = null;
				}
				if(vremeOd.getValue() != null && vremeDo.getValue() != null && ((Timestamp.valueOf(vremeDo.getValue()).getTime() - Timestamp.valueOf(vremeOd.getValue()).getTime())/1000 < 32*86400)) {
					if(objektiCombo.getValue() != null) {
						RadnoVremeGPSLayout rv = new RadnoVremeGPSLayout(objektiCombo.getValue(), 
								Timestamp.valueOf(vremeOd.getValue()), Timestamp.valueOf(vremeDo.getValue()), satiOd.getValue(), satiDo.getValue());
						preuzimanje = rv.vratiPreuzimanje();
						topLayout.addComponent(preuzimanje);
						podaci.setContent(rv);
						dodajPodatke();
					}else {
						pokaziPorukuGreska("морате изабрати објекат!");
					}
				}else {
					pokaziPorukuGreska("морате одабрати време у оба поља и период може бити највише 31 дан!");
				}
			}
		});
		
		dodajParametreDatum(true, true);
	}
	
	public void postaviParametreNivoGoriva() {
		vremeOd = new DatumVreme(false, "", 0, 0, 0);
        vremeDo = new DatumVreme(false, "", 0, 0, 1);
        vremeOd.addValueChangeListener(vremePromena);
        vremeDo.addValueChangeListener(vremePromena);
        
		prikazi.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				if(preuzimanje != null) {
					topLayout.removeComponent(preuzimanje);
					preuzimanje = null;
				}
				if(vremeOd.getValue() != null && vremeDo.getValue() != null && 
						((Timestamp.valueOf(vremeDo.getValue()).getTime() - Timestamp.valueOf(vremeOd.getValue()).getTime())/1000 <= 1*24*60*60*31)) {
					if(objektiCombo.getValue() != null) {
						NivoGorivaLayout gorivo = new NivoGorivaLayout(objektiCombo.getValue(), Timestamp.valueOf(vremeOd.getValue()), Timestamp.valueOf(vremeDo.getValue()));
						preuzimanje = gorivo.vratiPreuzimanje();
						topLayout.addComponent(preuzimanje);
						List<Javljanja> javljanja = Servis.javljanjeServis.vratiJavljanjaObjektaOdDo(objektiCombo.getValue(), Timestamp.valueOf(vremeOd.getValue()), Timestamp.valueOf(vremeDo.getValue()));
						ArrayList<Obd> obd = Servis.obdServis.nadjiObdPoObjektuOdDo(objektiCombo.getValue(), Timestamp.valueOf(vremeOd.getValue()), Timestamp.valueOf(vremeDo.getValue()));
						PregledNivoGoriva nivoGoriva = new PregledNivoGoriva(javljanja, obd);
						podaci.setContent(nivoGoriva);
						dodajPodatke();
					}else {
						pokaziPorukuGreska("морате изабрати објекат!");
					}
				}else {
					pokaziPorukuGreska("морате одабрати време у оба поља и период може бити највише 31 дан!");
				}
			}
		});
		
		dodajParametreDatum(false, true);
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
	
	private ValueChangeListener<LocalDateTime> vremePromena = new ValueChangeListener<LocalDateTime>() {
		private static final long serialVersionUID = 1L;
		@Override
		public void valueChange(ValueChangeEvent<LocalDateTime> event) {
			if(preuzimanje != null) {
				topLayout.removeComponent(preuzimanje);
				preuzimanje = null;
			}
		}
	};
	
	private void dodajPodatke() {
		root.addComponent(podaci);
		root.setExpandRatio(podaci, 1);
	}
}
