package rs.atekom.prati.view.pocetna;

import java.util.ArrayList;

import org.vaadin.addon.JFreeChartWrapper;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;

import pratiBaza.pomocne.PredjeniPut;

public class PregledKilometara extends Panel{

	private static final long serialVersionUID = 1L;
	private FormLayout noseci;
	private JFreeChartWrapper kmChart;

	public PregledKilometara(ArrayList<PredjeniPut> predjeniPut) {
		noseci = new FormLayout();
		setHeight("100%");
		noseci.setMargin(new MarginInfo(false, true, false, true));
		noseci.setSpacing(false);
		noseci.setSizeUndefined();
		KilometriMaxChart kilometri = new KilometriMaxChart();
		kmChart = new JFreeChartWrapper(kilometri.kreirajChart(predjeniPut));
		noseci.addComponent(kmChart);
		setContent(noseci);
	}
}
