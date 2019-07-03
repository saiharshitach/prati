package rs.atekom.prati.meni;

import com.vaadin.navigator.View;
import com.vaadin.server.Resource;

import rs.atekom.prati.view.PocetnaView;

import com.vaadin.icons.VaadinIcons;

public enum PratiViewType {

	POCETNA("pocetna", "Почетна", PocetnaView.class, VaadinIcons.DASHBOARD, false, false, false);
	
	private final String viewName;
    private final String viewIme;
    private final Class<? extends View> viewClass;
    private final Resource icon;
    private final boolean stateful;
    private final boolean admin;
    private final boolean sistem;
    
    private PratiViewType(final String viewName, String viewIme,
            final Class<? extends View> viewClass, final Resource icon, final boolean stateful, final boolean admin, final boolean sistem) {
        this.viewName = viewName;
        this.viewIme = viewIme;
        this.viewClass = viewClass;
        this.icon = icon;
        this.stateful = stateful;
        this.admin = admin;
        this.sistem = sistem;
    }
    
    public boolean isStateful() {
        return stateful;
    }

    public String getViewName() {
        return viewName;
    }
    
    public String getViewIme(){
    	return viewIme;
    }

    public Class<? extends View> getViewClass() {
        return viewClass;
    }

    public Resource getIcon() {
        return icon;
    }
    
    public boolean isAdmin() {
        return admin;
    }
    
    public boolean isSistem() {
        return sistem;
    }
    
    public static PratiViewType getByViewName(final String viewIme) {
    	PratiViewType result = null;
    	for(PratiViewType viewType: values()) {
    		if(viewType.getViewName().equals(viewIme)) {
    			result = viewType;
    			break;
    		}
    	}
    	return result;
    }
}
