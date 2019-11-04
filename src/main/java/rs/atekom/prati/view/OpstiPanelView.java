package rs.atekom.prati.view;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Iterator;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.themes.ValoTheme;
import pratiBaza.tabele.Korisnici;
import pratiBaza.tabele.Organizacije;
import pratiBaza.tabele.SistemPretplatnici;
import rs.atekom.prati.Prati;
import rs.atekom.prati.mape.Gmap;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.komponente.ComboAlarmi;
import rs.atekom.prati.view.komponente.ComboGrupe;
import rs.atekom.prati.view.komponente.ComboObjekti;
import rs.atekom.prati.view.komponente.ComboOrganizacije;
import rs.atekom.prati.view.komponente.ComboPretplatnici;
import rs.atekom.prati.view.komponente.DatumVreme;

public abstract class OpstiPanelView extends Panel implements View, Serializable{

	private static final long serialVersionUID = 1L;
	public static final String DANFORMAT = "%1$td/%1$tm/%1$tY";
	public static final String DANSATFORMAT = "%1$td/%1$tm/%1$tY %1$tH:%1$tM:%1$tS";
	public static final String MESECFORMAT = "%1$tm/%1$tY";
	public static final String OBAVEZNAPOLJA = "Сва обавезна поља морају бити попуњена исправно!!!";
	public static final String PODATAKDODAT = "Податак је већ унет!!!";
	public static final String PRAZNO = "Нисте ништа изабрали!!!";
	public static final String DECIMALNI = "###,##0.00";
	public static final String DECIMALNIVISE = "###,##0.000000";
	public NumberRenderer decimalni, decimalniVise;
	public final VerticalLayout root;
	public CssLayout paneli;
	public Korisnici korisnik;
	public HorizontalLayout topLayout;
	public TextField filter;
	public Button dodaj, lociraj, prikazi;
	public Gmap mapa;
	public Panel panelToolBar, panelObjDatumVreme;
	public ComboPretplatnici pretplatniciCombo;
	public ComboOrganizacije organizacijeCombo;
	public ComboGrupe grupeCombo;
	public CheckBox centriraj, prikaziMarkere, sortiraj;
	public DatumVreme vremeOd, vremeDo;
	public ComboObjekti objektiCombo;
	public ComboAlarmi alarmiCombo;

	public OpstiPanelView() {
        setSizeFull();
		addStyleName(ValoTheme.PANEL_BORDERLESS);
        setWidth("100%");
        Prati.getCurrent().pracenjeView = null;
        root = new VerticalLayout();
        root.setSizeFull();
        root.addStyleName("dupli-view");
        root.setMargin(false);
        root.setSpacing(true);
        
        decimalni  = new NumberRenderer(new DecimalFormat(DECIMALNI));
        decimalniVise = new NumberRenderer(new DecimalFormat(DECIMALNIVISE));
        
        korisnik = (Korisnici) VaadinSession.getCurrent().getAttribute(Korisnici.class.getName());
        
		topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		topLayout.setMargin(false);
        
		buildPretraga();
	}
	
	public void postaviPretplatnikOrg() {
		pretplatniciCombo = new ComboPretplatnici(null, true, false);
        pretplatniciCombo.addStyleName("lista-combo");
        organizacijeCombo = new ComboOrganizacije(pretplatniciCombo.getValue(), null, true, false);
        organizacijeCombo.addStyleName("lista-combo");
        grupeCombo = new ComboGrupe(pretplatniciCombo.getValue(), organizacijeCombo.getValue(), null, true, false);
        if(!korisnik.isAdmin()) {
        	grupeCombo.setItems(Servis.grupeKorisnikServis.vratiSveGrupePoKorisniku(korisnik));
        }
        grupeCombo.addStyleName("lista-combo");
        
        pretplatniciCombo.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				organizacijeCombo.clear();
				grupeCombo.clear();
				if(event.getValue() != null) {
					organizacijeCombo.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(pretplatniciCombo.getValue(), true));
					if(!korisnik.isAdmin()) {
						grupeCombo.setItems(Servis.grupeKorisnikServis.vratiSveGrupePoKorisniku(korisnik));
					}else {
						grupeCombo.setItems(Servis.grupeServis.vratiGrupeAktivne(pretplatniciCombo.getValue(), organizacijeCombo.getValue()));
					}
				}
			}
		});
        
        organizacijeCombo.addValueChangeListener(new ValueChangeListener<Organizacije>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Organizacije> event) {
				grupeCombo.clear();
				if(!korisnik.isAdmin()) {
					grupeCombo.setItems(Servis.grupeKorisnikServis.vratiSveGrupePoKorisniku(korisnik));
				}else {
					grupeCombo.setItems(Servis.grupeServis.vratiGrupeAktivne(pretplatniciCombo.getValue(), organizacijeCombo.getValue()));
				}
			}
		});
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
	
	public Panel buildPanelToolBar() {
		postaviPretplatnikOrg();
		panelToolBar = new Panel();
		panelToolBar.setWidth("100%");
		panelToolBar.addStyleName(ValoTheme.PANEL_BORDERLESS);
		
        lociraj = new Button();
        lociraj.setIcon(VaadinIcons.MAP_MARKER);
        lociraj.setDescription("прикажи објекте на мапи");
        centriraj = new CheckBox();
        centriraj.setDescription("центрирај мапу према објектима");
        sortiraj = new CheckBox();
        sortiraj.setDescription("сортирај аутоматски према времену податке у табели?");

        topLayout.setSizeUndefined();
        topLayout.addComponent(filter);
        topLayout.setComponentAlignment(filter, Alignment.MIDDLE_LEFT);
        
        if(isSistem()) {
        	topLayout.addComponent(pretplatniciCombo);
        }else {
        	pretplatniciCombo.setValue(korisnik.getSistemPretplatnici());
        }
        if(isSistem() || (korisnik.isAdmin() && korisnik.getOrganizacija() == null)) {
        	topLayout.addComponent(organizacijeCombo);
        }else {
        	organizacijeCombo.setValue(korisnik.getOrganizacija());
        }
        topLayout.addComponent(grupeCombo);
        topLayout.addComponent(centriraj);
        topLayout.addComponent(lociraj);
        topLayout.addComponent(sortiraj);
        topLayout.setExpandRatio(filter, 1);
		panelToolBar.setContent(topLayout);
		return panelToolBar;
	}
	
	public Panel buildObjektiDatumVremeToolBar() {
		postaviPretplatnikOrg();
		panelObjDatumVreme = new Panel();
		panelObjDatumVreme.setWidth("100%");
		panelObjDatumVreme.addStyleName(ValoTheme.PANEL_BORDERLESS);

        vremeOd = new DatumVreme(false, "", 0, 0, 0);
        vremeDo = new DatumVreme(false, "", 0, 0, 1);
        topLayout.setSizeUndefined();
        
        objektiCombo = new ComboObjekti(null, null, true, false);
        alarmiCombo = new ComboAlarmi(null, true, true, false, true, false);
        
        prikaziMarkere = new CheckBox();
        prikaziMarkere.setDescription("прикажи маркере");
        prikazi = new Button();
        prikazi.setIcon(VaadinIcons.CHECK);
        prikazi.setDescription("прикажи историју");
        
        if(isSistem()) {
        	topLayout.addComponent(pretplatniciCombo);
        }else {
        	pretplatniciCombo.setValue(korisnik.getSistemPretplatnici());
        }
        if(isSistem() || (korisnik.isAdmin() && korisnik.getOrganizacija() == null)) {
        	topLayout.addComponent(organizacijeCombo);
        }else {
        	organizacijeCombo.setValue(korisnik.getOrganizacija());
        }
        
        topLayout.addComponent(grupeCombo);
        topLayout.addComponent(objektiCombo);
        topLayout.addComponent(vremeOd);
        topLayout.addComponent(vremeDo);
        topLayout.addComponent(alarmiCombo);
        topLayout.addComponent(prikaziMarkere);
        topLayout.addComponent(prikazi);
        
		panelObjDatumVreme.setContent(topLayout);
		return panelObjDatumVreme;
	}
	
	public Panel buildToolBarIzvestaji() {
		panelToolBar = new Panel();
		panelToolBar.setWidth("100%");
		panelToolBar.addStyleName(ValoTheme.PANEL_BORDERLESS);
        
		panelToolBar.setContent(topLayout);
		return panelToolBar;
	}
	
	public Component buildContent(Component prva, Component druga) {
		paneli = new CssLayout();
		paneli.addStyleName("dupli-panels");
		Responsive.makeResponsive(paneli);//BEZ OVOGA NE RADE CARDS
		paneli.addComponent(prva);
		paneli.addComponent(druga);
		return paneli;
	}
	
	public Component buildGMapa() {
		mapa = new Gmap(Servis.apiGoogle, null, "serbian");
		mapa.centriraj();
		String slot = "dupli-panel-slot";
		return createContentWraper(mapa, slot, true);
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
	
	public boolean isSistem() {
		return (korisnik.isSistem() && korisnik.getSistemPretplatnici().isSistem());
	}
	
	public Component createContentWraper(final Component content, String slotStyle, Boolean maxSize) {
		final CssLayout slot = new CssLayout();
		slot.setWidth("100%");
		slot.addStyleName(slotStyle);
	        
		CssLayout card = new CssLayout();
		card.setWidth("100%");

		card.addStyleName(ValoTheme.LAYOUT_CARD);
		
		HorizontalLayout toolbar = new HorizontalLayout();
		
		if(slotStyle.equals("dupli-panel-slot")) {
			toolbar.addStyleName("dupli-panel-toolbar");
		}else {
			toolbar.addStyleName("dashboard-panel-toolbar");
		}
		
		toolbar.setWidth("100%");

		Label caption = new Label();
		caption.addStyleName(ValoTheme.LABEL_H4);
		caption.addStyleName(ValoTheme.LABEL_COLORED);
		caption.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		content.setCaption(null);

		MenuBar tools = new MenuBar();
		tools.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
		MenuItem max = tools.addItem("", VaadinIcons.EXPAND, new Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(final MenuItem selectedItem) {
				if (!slot.getStyleName().contains("max")) {
					selectedItem.setIcon(VaadinIcons.COMPRESS);
					toggleMaximized(slot, true);
					} else {
						slot.removeStyleName("max");
						selectedItem.setIcon(VaadinIcons.EXPAND);
						toggleMaximized(slot, false);
						}
				}
			});
	        max.setStyleName("icon-only");
	        /*MenuItem root = tools.addItem("", VaadinIcons.COG, null);
	        root.addItem("Configure", new Command() {
	            @Override
	            public void menuSelected(final MenuItem selectedItem) {
	                Notification.show("Not implemented in this demo");
	            }
	        });
	        root.addSeparator();
	        root.addItem("Close", new Command() {
	            @Override
	            public void menuSelected(final MenuItem selectedItem) {
	                Notification.show("Not implemented in this demo");
	            }
	        });**/

	        toolbar.addComponents(caption, tools);
	        toolbar.setExpandRatio(caption, 1);
	        toolbar.setComponentAlignment(caption, Alignment.MIDDLE_LEFT);
	        if(maxSize == true){
	        	card.addComponents(toolbar, content);
	        	}else{
	        		card.addComponents(content);
	        		}
	        slot.addComponent(card);
	        return slot;
		}
		
	private void toggleMaximized(final Component panel, final boolean maximized) {
		for(Iterator<Component> it = root.iterator(); it.hasNext();) {
			it.next().setVisible(!maximized);
		}
		paneli.setVisible(true);
	        
		for(Iterator<Component> it = paneli.iterator(); it.hasNext();) {
			Component c = it.next();
			c.setVisible(!maximized);
		}

		if(maximized) {
			panel.setVisible(true);
			panel.addStyleName("max");
		} else {
			panel.removeStyleName("max");
			}
		}
}
