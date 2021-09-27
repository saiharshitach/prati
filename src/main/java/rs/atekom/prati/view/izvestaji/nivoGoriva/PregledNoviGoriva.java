package rs.atekom.prati.view.izvestaji.nivoGoriva;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.addon.JFreeChartWrapper;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;
import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.Obd;

public class PregledNoviGoriva extends Panel{

	private static final long serialVersionUID = 1L;
	private FormLayout noseci;
	private JFreeChartWrapper nivoChart;

	public PregledNoviGoriva(List<Javljanja> javljanja, ArrayList<Obd> obd) {
		noseci = new FormLayout();
		setHeight("100%");
		noseci.setMargin(new MarginInfo(false, true, false, true));
		noseci.setSpacing(false);
		noseci.setSizeUndefined();
		NivoGorivaChart chart = new NivoGorivaChart();
		nivoChart = new JFreeChartWrapper(chart.kreirajChart(javljanja, obd));
		noseci.addComponent(nivoChart);
		setContent(noseci);
	}
}
