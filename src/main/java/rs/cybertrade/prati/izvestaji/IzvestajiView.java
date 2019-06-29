package rs.cybertrade.prati.izvestaji;

import java.sql.Timestamp;
import java.util.ArrayList;
import com.github.appreciated.app.layout.annotations.MenuCaption;
import com.github.appreciated.app.layout.annotations.MenuIcon;
import com.github.appreciated.app.layout.annotations.NavigatorViewName;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import pratiBaza.pomocne.IzvestajTip;
import pratiBaza.tabele.Grupe;
import pratiBaza.tabele.Objekti;
import rs.cybertrade.prati.server.Servis;
import rs.cybertrade.prati.view.OpstiPanelView;
import rs.cybertrade.prati.view.komponente.ComboIzvestaji;
import rs.cybertrade.prati.view.komponente.ComboObjekti;
import rs.cybertrade.prati.view.komponente.DatumVreme;

@NavigatorViewName("izvestaji") // an empty view name will also be the default view
@MenuCaption("Извештаји")
@MenuIcon(VaadinIcons.BAR_CHART)
public class IzvestajiView extends OpstiPanelView{

	private static final long serialVersionUID = 1L;
	public ComboObjekti objektiCombo;
	public ComboIzvestaji izvestajiCombo;
	private HorizontalLayout parametri;
	private Panel podaci;
	private HorizontalLayout preuzimanje;
	
	public IzvestajiView() {
		root.addComponent(buildToolBarIzvestaji());
		podaci = new Panel();
		podaci.addStyleName(ValoTheme.PANEL_BORDERLESS);
		podaci.setSizeFull();
		podaci.setWidth("100%");
		postaviPretplatnikOrg();
		
		izvestajiCombo = new ComboIzvestaji();
		topLayout.addComponent(izvestajiCombo);
        vremeOd = new DatumVreme(false, "", 0, 0, 0);
        vremeDo = new DatumVreme(false, "", 0, 0, 1);
        objektiCombo = new ComboObjekti(korisnik, null, true, false);
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
		
		root.addComponent(podaci);
		root.setExpandRatio(podaci, 1);
		setContent(root);
	}
	
	private void postaviParametre(IzvestajTip tip) {
		brisanje();
		if(tip != null) {
			switch (tip.getRb()) {
			case 1: postaviParametrePredjeniPut();
			
				break;

			default:
				break;
			}
		}
	}
	
	private void postaviParametrePredjeniPut() {
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
						if(!korisnik.isAdmin()) {
							ArrayList<Grupe> grupe = Servis.grupeKorisnikServis.vratiSveGrupePoKorisniku(korisnik);
							objekti = Servis.grupeObjekatServis.nadjiSveObjektePoGrupama(grupe);
						}else {
							if(korisnik.getSistemPretplatnici() != null) {
								objekti = Servis.objekatServis.vratiSveObjekte(korisnik, true);
							}
						}
					}else {//ako ima ili objekat ili grupaObjekata
						if(objektiCombo.getValue() == null) {
							objekti = Servis.grupeObjekatServis.nadjiSveObjektePoGrupi(grupeCombo.getValue());
							}else {
								objekti.add(objektiCombo.getValue());
							}
					}
					PredjeniPutLayout  predjeniPut = new PredjeniPutLayout(objekti, Timestamp.valueOf(vremeOd.getValue()), Timestamp.valueOf(vremeDo.getValue()));
					preuzimanje = predjeniPut.vratiPreuzimanje();
					topLayout.addComponent(preuzimanje);
					podaci.setContent(predjeniPut);
				}else {
					pokaziPorukuGreska("морате одабрати време у оба поља и период може бити највише 31 дан!");
				}
			}
		});
        
        if(isAdmin()) {
        	parametri.addComponent(pretplatniciCombo);
        }else {
        	pretplatniciCombo.setValue(korisnik.getSistemPretplatnici());
        }
        if(isAdmin() || (korisnik.isAdmin() && korisnik.getOrganizacija() == null)) {
        	parametri.addComponent(organizacijeCombo);
        }else {
        	organizacijeCombo.setValue(korisnik.getOrganizacija());
        }
        
        parametri.addComponent(grupeCombo);
        parametri.addComponent(objektiCombo);
        parametri.addComponent(vremeOd);
        parametri.addComponent(vremeDo);
        parametri.addComponent(prikazi);
        
        topLayout.addComponent(parametri);
	}
	
	private void brisanje() {
		podaci.setContent(null);
		vremeOd.clear();
		vremeDo.clear();
		objektiCombo.clear();
		if(preuzimanje != null) {
			topLayout.removeComponent(preuzimanje);
			preuzimanje = null;
		}
		podaci.setContent(null);
		parametri.removeAllComponents();
	}
	

}
