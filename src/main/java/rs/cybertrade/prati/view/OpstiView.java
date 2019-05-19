package rs.cybertrade.prati.view;

import java.text.DecimalFormat;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.themes.ValoTheme;
import pratiBaza.tabele.Korisnici;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import rs.cybertrade.prati.Prati;
import rs.cybertrade.prati.mape.StaraGMapa;
import rs.cybertrade.prati.server.Servis;
import rs.cybertrade.prati.view.komponente.ComboGrupe;
import rs.cybertrade.prati.view.komponente.ComboOrganizacije;
import rs.cybertrade.prati.view.komponente.ComboPretplatnici;

public abstract class OpstiView extends CssLayout implements View {
	private static final long serialVersionUID = 1L;
	public static final String DANFORMAT = "%1$td/%1$tm/%1$tY";
	public static final String DANSATFORMAT = "%1$td/%1$tm/%1$tY %1$tH:%1$tM:%1$tS";
	public static final String MESECFORMAT = "%1$tm/%1$tY";
	public static final String OBAVEZNAPOLJA = "Сва обавезна поља морају бити попуњена исправно!!!";
	public static final String PODATAKDODAT = "Податак је већ унет!!!";
	public static final String PRAZNO = "Нисте ништа изабрали!!!";
	public static final String DECIMALNI = "###,##0.00";
	public HorizontalLayout topLayout;
	public VerticalLayout barGrid;
	public DateField datum;
	public Panel panelToolBar;
	public Button dodaj, potvrdi;
	public NumberRenderer decimalni;
	public TextField filter;
	public Korisnici korisnik;
	public ComboPretplatnici pretplatniciCombo;
	public ComboOrganizacije organizacijeCombo;
	public ComboGrupe grupeCombo;
	public CssLayout expander;
	public CssLayout paneli;
	public StaraGMapa mapa;
	
	public OpstiView() {
		setSizeFull();
		addStyleName("crud-view");
		decimalni  = new NumberRenderer(new DecimalFormat(DECIMALNI));
		korisnik = (Korisnici) VaadinSession.getCurrent().getAttribute(Korisnici.class.getName());
		topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		topLayout.setMargin(new MarginInfo(false, false, false, false));
		buildPretraga();
	}
	
	//opšti toolbar
	public void buildToolbar() {
		postaviSirinu();
        dodaj = new Button();
        dodaj.addStyleName(ValoTheme.BUTTON_PRIMARY);
        dodaj.setIcon(VaadinIcons.PLUS_CIRCLE);
        topLayout.addComponent(filter);
        topLayout.addComponent(dodaj);
        topLayout.setComponentAlignment(filter, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(filter, 1);
		topLayout.setStyleName("top-bar");
	}
	
	//toolabar sa grupama
	public Panel buildToolbarGrupe() {
		panelToolBar = new Panel();
		panelToolBar.setWidth("100%");
		panelToolBar.addStyleName(ValoTheme.PANEL_BORDERLESS);
		
        pretplatniciCombo = new ComboPretplatnici(null, true, false);
        pretplatniciCombo.addStyleName("lista-combo");
        organizacijeCombo = new ComboOrganizacije(pretplatniciCombo.getValue(), null, true, false);
        organizacijeCombo.addStyleName("lista-combo");
        grupeCombo = new ComboGrupe(pretplatniciCombo.getValue(), organizacijeCombo.getValue(), null, true, false);
        grupeCombo.addStyleName("lista-combo");
        potvrdi = new Button();
        potvrdi.addStyleName(ValoTheme.BUTTON_PRIMARY);
        potvrdi.setIcon(VaadinIcons.CHECK);
        expander = new CssLayout();
		expander.addStyleName("expander");
		
        topLayout.addComponent(filter);
        topLayout.setComponentAlignment(filter, Alignment.MIDDLE_LEFT);
        topLayout.addComponent(expander);
        pretplatniciCombo.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				organizacijeCombo.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(pretplatniciCombo.getValue(), true));
				organizacijeCombo.clear();
				grupeCombo.clear();
				grupeCombo.setItems(Servis.grupeServis.vratiGrupeAktivne(pretplatniciCombo.getValue(), organizacijeCombo.getValue()));
			}
		});
        organizacijeCombo.addValueChangeListener(new ValueChangeListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Organizacije> event) {
				grupeCombo.clear();
				grupeCombo.setItems(Servis.grupeServis.vratiGrupeAktivne(pretplatniciCombo.getValue(), organizacijeCombo.getValue()));
			}
		});
        
        if(isAdmin()) {
        	topLayout.addComponent(pretplatniciCombo);
        }else {
        	pretplatniciCombo.setValue(korisnik.getSistemPretplatnici());
        }
        if(isAdmin() || korisnik.getOrganizacija() == null) {
        	topLayout.addComponent(organizacijeCombo);
        }else {
        	organizacijeCombo.setValue(korisnik.getOrganizacija());
        }
        topLayout.addComponent(grupeCombo);
        topLayout.addComponent(potvrdi);
        topLayout.setExpandRatio(filter, 1);
		panelToolBar.setContent(topLayout);
        return panelToolBar;
	}
	
	//osnovni vertikalni layout
	public void buildlayout() {
		barGrid = new VerticalLayout();
		barGrid.setSizeFull();
		barGrid.setMargin(true);
		barGrid.setSpacing(true);
		//barGrid.setStyleName("crud-main-layout"); - ovo mi nije dalo da skrolujem panel toolbar sa komandama!!!
	}
	
	//generisanje TextField za pretragu
	public void buildPretraga() {
		filter = new TextField();
		filter.setStyleName("filter-textfield");
        filter.setPlaceholder("претрага...");
	}
	
	public void postaviSirinu() {
		if(Prati.getCurrent().sirina()) {
			topLayout.setWidth("100%");
			//topLayout.setSizeUndefined();
		}else {
			topLayout.setSizeUndefined();
		}
	}
	
	public void postaviNoviOmoguceno(boolean omoguceno) {
		dodaj.setEnabled(omoguceno);
	}
	
	public void pokaziPorukuGreska(String msg) {
		Notification.show(msg, Type.ERROR_MESSAGE);
	}

	public void pokaziPorukuUspesno(String msg) {
		Notification.show(msg, Type.TRAY_NOTIFICATION);
	}
	
	public boolean isAdmin() {
		return (korisnik.isSistem() && korisnik.getSistemPretplatnici() == null);
	}
	
}
