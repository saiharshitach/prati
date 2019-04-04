package rs.cybertrade.prati.view;

import java.text.DecimalFormat;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
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

import rs.cybertrade.prati.Prati;

public abstract class OpstiView extends CssLayout implements View{
	private static final long serialVersionUID = 1L;
	public static final String DANFORMAT = "%1$td/%1$tm/%1$tY";
	public static final String DANSATFORMAT = "%1$td/%1$tm/%1$tY %1$tH:%1$tM:%1$tS";
	public static final String MESECFORMAT = "%1$tm/%1$tY";
	public static final String OBAVEZNAPOLJA = "Сва обавезна поља морају бити попуњена исправно!!!";
	public static final String PODATAKDODAT = "Податак је већ унет!!!";
	public static final String PRAZNO = "Нисте ништа изабрали!!!";
	public HorizontalLayout topLayout;
	public VerticalLayout barGrid;
	public DateField datum;
	public Panel panel;
	public Button dodaj;
	public NumberRenderer decimalni;
	public TextField filter;

	
	public OpstiView() {
		setSizeFull();
		addStyleName("crud-view");
		decimalni  = new NumberRenderer(new DecimalFormat("###,##0.00"));
	}
	
	//opšti toolbar
	public HorizontalLayout buildToolbar() {
		topLayout = new HorizontalLayout();
        topLayout.setSpacing(true);
        postaviSirinu();
        dodaj = new Button();
        dodaj.addStyleName(ValoTheme.BUTTON_PRIMARY);
        dodaj.setIcon(VaadinIcons.PLUS_CIRCLE);
        buildPretraga();
        topLayout.addComponent(filter);
        topLayout.addComponent(dodaj);
        topLayout.setComponentAlignment(filter, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(filter, 1);
		topLayout.setStyleName("top-bar");
        return topLayout;
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
	
}
