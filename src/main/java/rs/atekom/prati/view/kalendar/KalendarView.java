package rs.atekom.prati.view.kalendar;

import com.github.appreciated.app.layout.annotations.MenuCaption;
import com.github.appreciated.app.layout.annotations.MenuIcon;
import com.github.appreciated.app.layout.annotations.NavigatorViewName;
import com.vaadin.icons.VaadinIcons;
import rs.atekom.prati.view.OpstiView;

@NavigatorViewName("kalendar") // an empty view name will also be the default view
@MenuCaption("Календар")
@MenuIcon(VaadinIcons.CALENDAR)
public class KalendarView extends OpstiView {

	private static final long serialVersionUID = 1L;
	private Kalendar kalendar;
	
	public KalendarView() {
		kalendar = new Kalendar();
		kalendar.setSizeFull();
		
		barGrid.addComponent(kalendar);
		barGrid.setExpandRatio(kalendar, 1);
		
		addComponent(barGrid);
	}
}
