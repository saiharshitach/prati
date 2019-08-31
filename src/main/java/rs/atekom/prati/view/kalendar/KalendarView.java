package rs.atekom.prati.view.kalendar;

import com.github.appreciated.app.layout.annotations.MenuCaption;
import com.github.appreciated.app.layout.annotations.MenuIcon;
import com.github.appreciated.app.layout.annotations.NavigatorViewName;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import pratiBaza.tabele.Grupe;
import rs.atekom.prati.view.OpstiView;

@NavigatorViewName("kalendar") // an empty view name will also be the default view
@MenuCaption("Календар")
@MenuIcon(VaadinIcons.CALENDAR)
public class KalendarView extends OpstiView {

	private static final long serialVersionUID = 1L;
	private Kalendar kalendar;
	
	public KalendarView() {
		kalendar = new Kalendar(this);
		kalendar.setSizeFull();
		
		buildToolbarGrupe();
		
		potvrdi.setVisible(false);
		filter.setVisible(false);
		
		grupeCombo.addValueChangeListener(new ValueChangeListener<Grupe>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<Grupe> event) {
				if(event != null) {
					kalendar.ukloniSveDogadjaje();
					kalendar.postaviNaloge();
				}else {
					kalendar.ukloniSveDogadjaje();
				}
			}
		});
		
		barGrid.addComponent(panelToolBar);
		barGrid.addComponent(kalendar);
		barGrid.setExpandRatio(kalendar, 1);
		
		addComponent(barGrid);
	}
}
