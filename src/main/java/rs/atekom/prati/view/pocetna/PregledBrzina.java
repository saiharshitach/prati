package rs.atekom.prati.view.pocetna;

import java.util.ArrayList;

import org.vaadin.addon.JFreeChartWrapper;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;
import pratiBaza.tabele.Javljanja;

public class PregledBrzina extends Panel{

	private static final long serialVersionUID = 1L;
	private FormLayout noseci;
	private JFreeChartWrapper brzinaChart;
	
	public PregledBrzina(ArrayList<Javljanja> javljanja) {
		noseci = new FormLayout();
		setHeight("100%");
		noseci.setMargin(new MarginInfo(false, true, false, true));
		noseci.setSpacing(false);
		noseci.setSizeUndefined();
		BrzinaMaxChart chart = new BrzinaMaxChart();
		brzinaChart = new JFreeChartWrapper(chart.kreirajChart(javljanja));
		noseci.addComponent(brzinaChart);
		setContent(noseci);
	}

}
