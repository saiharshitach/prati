package rs.cybertrade.prati.view;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Iterator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
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
import rs.cybertrade.prati.Prati;
import rs.cybertrade.prati.mape.Gmap;
import rs.cybertrade.prati.server.Servis;

public class OpstiPanelView extends Panel implements View, Serializable{

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
	public CssLayout noseci;
	public CssLayout paneli;
	public Korisnici korisnik;
	public HorizontalLayout topLayout;
	public TextField filter;
	public Button dodaj;
	public Gmap mapa;

	public OpstiPanelView() {
		addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();
        noseci =  new CssLayout();
        noseci.setSizeFull();
        noseci.addStyleName("crud-view");
        
        root = new VerticalLayout();
        root.setSizeFull();
        root.addStyleName("dupli-view");
        root.setMargin(new MarginInfo(false, false, false, false));
        root.setSpacing(true);
        
        decimalni  = new NumberRenderer(new DecimalFormat(DECIMALNI));
        decimalniVise = new NumberRenderer(new DecimalFormat(DECIMALNIVISE));
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
	
	public Component buildContent(Component prva, Component druga) {
		paneli = new CssLayout();
		paneli.addStyleName("dupli-panels");
		Responsive.makeResponsive(paneli);//BEZ OVOGA NE RADE CARDS
		paneli.addComponent(prva);
		paneli.addComponent(druga);
		return paneli;
	}
	
	public Component buildGMapa(boolean dodavanjeMarkera) {
		mapa = new Gmap(Servis.apiGoogle, null, "serbian");
		if(dodavanjeMarkera) {
			mapa.dodavanjeMarkera();
		}
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
	
	public boolean isAdmin() {
		return (korisnik.isSistem() && korisnik.getSistemPretplatnici() == null);
	}
	
	public Component createContentWraper(final Component content, String slotStyle, Boolean maxSize) {
		final CssLayout slot = new CssLayout();
		slot.setWidth("100%");
		slot.addStyleName(slotStyle);
	        
		CssLayout card = new CssLayout();
		card.setWidth("100%");

		card.addStyleName(ValoTheme.LAYOUT_CARD);
		
		HorizontalLayout toolbar = new HorizontalLayout();
		toolbar.addStyleName("dupli-panel-toolbar");
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
