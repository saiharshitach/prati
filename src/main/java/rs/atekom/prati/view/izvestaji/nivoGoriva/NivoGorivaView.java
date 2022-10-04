package rs.atekom.prati.view.izvestaji.nivoGoriva;

import java.sql.Timestamp;
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
import pratiBaza.tabele.Grupe;
import pratiBaza.tabele.Objekti;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.OpstiPanelView;
import rs.atekom.prati.view.komponente.DatumVreme;
import rs.atekom.prati.view.komponente.combo.ComboIzvestaji;
import rs.atekom.prati.view.komponente.combo.ComboObjekti;

@NavigatorViewName("nivoGoriva") // an empty view name will also be the default view
@MenuCaption("Ниво горива")
@MenuIcon(VaadinIcons.CHART_TIMELINE)
public class NivoGorivaView extends OpstiPanelView{

	private static final long serialVersionUID = 1L;
	public ComboObjekti objektiCombo;
	public ComboIzvestaji izvestajiCombo;
	private HorizontalLayout parametri;
	private Panel podaci;
	private HorizontalLayout preuzimanje;

	public NivoGorivaView() {
		root.addComponent(buildToolBarIzvestaji());
		podaci = new Panel();
		podaci.addStyleName(ValoTheme.PANEL_BORDERLESS);
		podaci.setSizeFull();
		podaci.setWidth("100%");
		postaviPretplatnikOrg();
		
        objektiCombo = new ComboObjekti(korisnik, null, true, false);
        prikazi = new Button();
        prikazi.setIcon(VaadinIcons.CHECK);
        
		parametri = new HorizontalLayout();
		parametri.setSpacing(true);
		parametri.setMargin(false);
		parametri.setSizeUndefined();
		
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
		
		prikazi.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {

				if(preuzimanje != null) {
					topLayout.removeComponent(preuzimanje);
					preuzimanje = null;
				}
				if(vremeOd.getValue() != null && vremeDo.getValue() != null && 
						((Timestamp.valueOf(vremeDo.getValue()).getTime() - Timestamp.valueOf(vremeOd.getValue()).getTime())/1000 < 1*24*60*60*31)) {
					if(objektiCombo.getValue() != null) {
						NivoGorivaLayout gorivo = new NivoGorivaLayout(objektiCombo.getValue(), Timestamp.valueOf(vremeOd.getValue()), Timestamp.valueOf(vremeDo.getValue()));
						preuzimanje = gorivo.vratiPreuzimanje();
						topLayout.addComponent(preuzimanje);
						podaci.setContent(gorivo);
					}else {
						pokaziPorukuGreska("морате изабрати објекат!");
					}
				}else {
					pokaziPorukuGreska("морате одабрати време у оба поља и период може бити највише 31 дан!");
				}
			}
		});
		
		postaviParametreNivoGoriva();
		root.addComponent(podaci);
		root.setExpandRatio(podaci, 1);
		setContent(root);
	}
	
	public void postaviParametreNivoGoriva() {
		vremeOd = new DatumVreme(false, "", 0, 0, 0);
        vremeDo = new DatumVreme(false, "", 0, 0, 1);

		
		dodajParametreDatum(false);
	}
	
	private void dodajParametreDatum(boolean dodajSate) {

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
        parametri.addComponent(vremeOd);
        parametri.addComponent(vremeDo);

        parametri.addComponent(prikazi);
        
        topLayout.addComponent(parametri);
	}

}
