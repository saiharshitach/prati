package rs.atekom.prati.meni;

import com.google.common.eventbus.Subscribe;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Tree;
import com.vaadin.ui.themes.ValoTheme;
import pratiBaza.tabele.Korisnici;
import rs.atekom.prati.meni.PratiEvent.KorisnikLoggedOutEvent;
import rs.atekom.prati.meni.PratiEvent.PostViewChangeEvent;
import rs.atekom.prati.meni.PratiEvent.ProfileUpdatedEvent;
import rs.atekom.prati.meni.PratiEvent.ReportsCountUpdatedEvent;


public final class PratiMeni extends CustomComponent{

	private static final long serialVersionUID = 1L;
	private Korisnici korisnik = VaadinSession.getCurrent().getAttribute(Korisnici.class);
	public static final String ID = "dashboard-menu";
    public static final String REPORTS_BADGE_ID = "dashboard-menu-reports-badge";
    public static final String NOTIFICATIONS_BADGE_ID = "dashboard-menu-notifications-badge";
    private static final String STYLE_VISIBLE = "valo-menu-visible";
    private Label izvestajiLogo;
    private MenuItem podesavanjeStavka;
    public Label logo = new Label("<strong>Праћење</strong>", ContentMode.HTML);
    
    public PratiMeni() {
    	setPrimaryStyleName(ValoTheme.MENU_ROOT);
    	setId(ID);
    	setSizeUndefined();
    	PratiEventBus.register(this);
    	setCompositionRoot(buildContent());
    }
    
    private Component buildContent() {
    	final CssLayout menuContent = new CssLayout();
        menuContent.addStyleName("sidebar");
        menuContent.addStyleName(ValoTheme.MENU_PART);
        menuContent.addStyleName("no-vertical-drag-hints");
        menuContent.addStyleName("no-horizontal-drag-hints");
        menuContent.setWidth(null);
        menuContent.setHeight("100%");
        
        menuContent.addComponent(buildTitle());
        menuContent.addComponent(buildUserMenu());
        menuContent.addComponent(buildToggleButton());
        menuContent.addComponent(buildMenuItems());
        return menuContent;
    }

    private Component buildTitle() {
        logo.setSizeUndefined();
        HorizontalLayout logoWrapper = new HorizontalLayout(logo);
        logoWrapper.setComponentAlignment(logo, Alignment.MIDDLE_CENTER);
        logoWrapper.addStyleName("valo-menu-title");
        return logoWrapper;
    }
    
    private Component buildUserMenu() {
    	final MenuBar settings = new MenuBar();
        settings.addStyleName("user-menu");
        podesavanjeStavka = settings.addItem("", new ThemeResource("img/profile-pic-300px.jpg"), null);
        updateKorisnickoIme(null);
        podesavanjeStavka.addSeparator();
        podesavanjeStavka.addItem("Одјава", new Command() {
			private static final long serialVersionUID = 1L;

			@Override
            public void menuSelected(final MenuItem selectedItem) {
                PratiEventBus.post(new KorisnikLoggedOutEvent());
            }
        });
        return settings;
    }
    
    private Component buildToggleButton() {
        Button valoMenuToggleButton = new Button("Избор", new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
            public void buttonClick(final ClickEvent event) {
                if (getCompositionRoot().getStyleName().contains(STYLE_VISIBLE)) {
                    getCompositionRoot().removeStyleName(STYLE_VISIBLE);
                } else {
                    getCompositionRoot().addStyleName(STYLE_VISIBLE);
                }
            }
        });
        valoMenuToggleButton.setIcon(VaadinIcons.LIST);
        valoMenuToggleButton.addStyleName("valo-menu-toggle");
        valoMenuToggleButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        valoMenuToggleButton.addStyleName(ValoTheme.BUTTON_SMALL);
        return valoMenuToggleButton;
    }
    
    private Component buildMenuItems() {
    	CssLayout menuItemsLayout = new CssLayout();
        menuItemsLayout.addStyleName("valo-menuitems");
        for(final PratiViewType view : PratiViewType.values()) {
        	Component menuItemComponent = new ValoMenuItemButton(view);
        	menuItemsLayout.addComponent(menuItemComponent);
        }
        Label izvestaji = new Label("--------Извештаји--------");
        menuItemsLayout.addComponent(izvestaji);
        return menuItemsLayout;
    }
    
    @Override
    public void attach() {
        super.attach();
        //updateNotificationsCount(null);
    }
    
    @Subscribe
    public void postViewChange(final PostViewChangeEvent event) {
        // After a successful view change the menu can be hidden in mobile view.
        getCompositionRoot().removeStyleName(STYLE_VISIBLE);
    }
    
    @Subscribe
    public void updateReportsCount(final ReportsCountUpdatedEvent event) {
        izvestajiLogo.setValue(String.valueOf(event.getCount()));
        izvestajiLogo.setVisible(event.getCount() > 0);
    }
    
    @Subscribe
    public void updateKorisnickoIme(final ProfileUpdatedEvent event) {
        podesavanjeStavka.setText(korisnik.getIme() + " " + korisnik.getPrezime());
    }
    
    public final class ValoMenuItemButton extends Button {
		private static final long serialVersionUID = 1L;
		private static final String STYLE_SELECTED = "selected";
        private final PratiViewType view;
        public ValoMenuItemButton(final PratiViewType view) {
            this.view = view;
            setPrimaryStyleName("valo-menu-item");
            setIcon(view.getIcon());
            setCaption(view.getViewIme());
            PratiEventBus.register(this);
            addClickListener(new ClickListener() {
				private static final long serialVersionUID = 1L;

				@Override
                public void buttonClick(final ClickEvent event) {
                    UI.getCurrent().getNavigator().navigateTo(view.getViewName());
                }
            });
        }

        @Subscribe
        public void postViewChange(final PostViewChangeEvent event) {
            removeStyleName(STYLE_SELECTED);
            if (event.getView() == view) {
                addStyleName(STYLE_SELECTED);
            }
        }
    }
}
