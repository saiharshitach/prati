package rs.atekom.prati.view.izvestajiVozilo;

import com.github.appreciated.app.layout.annotations.MenuCaption;
import com.github.appreciated.app.layout.annotations.MenuIcon;
import com.github.appreciated.app.layout.annotations.NavigatorViewName;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ValoTheme;

import rs.atekom.prati.view.OpstiPanelView;

@NavigatorViewName("izvestajiVozilo") // an empty view name will also be the default view
@MenuCaption("Извештаји возило")
@MenuIcon(VaadinIcons.BAR_CHART)
public class IzvestajiVoziloView extends OpstiPanelView{

	private static final long serialVersionUID = 1L;
	private HorizontalLayout parametri;
	private Panel podaci;
	private HorizontalLayout preuzimanje;
	
	public IzvestajiVoziloView() {
		
		podaci = new Panel();
		podaci.addStyleName(ValoTheme.PANEL_BORDERLESS);
		podaci.setSizeFull();
		podaci.setWidth("100%");
		
	}
}
