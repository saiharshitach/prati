package rs.atekom.prati.view.pocetna;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import pratiBaza.tabele.Javljanja;

public class BrzinaMaxChart {
	
	public BrzinaMaxChart() {
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings("deprecation")
	public JFreeChart kreirajChart(ArrayList<Javljanja> javljanja) {
		CategoryDataset dataset1 = createDataSet1(javljanja);
		JFreeChart chart = ChartFactory.createBarChart(
	            "Највеће брзине",        // chart title
	            "Објекат",               // domain axis label
	            "Брзина",                  // range axis label
	            dataset1,                 // data
	            PlotOrientation.HORIZONTAL,
	            true,                     // include legend
	            true,                     // tooltips?
	            false                     // URL generator?  Not required...
	        );
		 // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);
        chart.addSubtitle(new TextTitle("преглед највећих брзина за претходни дан")); 
        //chart.getLegend().setAnchor(Legend.SOUTH);
        // get a reference to the plot for further customisation...
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(0xEE, 0xEE, 0xFF));
        plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
        //final CategoryDataset dataset2 = createDataset2();
        //plot.setDataset(1, dataset2);
        //plot.mapDatasetToRangeAxis(1, 1);
        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
        //final ValueAxis axis2 = new NumberAxis("Secondary");
        //plot.setRangeAxis(1, axis2);
        final LineAndShapeRenderer renderer2 = new LineAndShapeRenderer();
        renderer2.setToolTipGenerator(new StandardCategoryToolTipGenerator());
        plot.setRenderer(1, renderer2);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
        // OPTIONAL CUSTOMISATION COMPLETED.
		return chart;
	}
	
	private CategoryDataset createDataSet1(ArrayList<Javljanja> javljanja) {
		try {
			if(javljanja != null && !javljanja.isEmpty()) {
				javljanja.sort(Comparator.comparing(Javljanja::getBrzina).reversed());
				final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
				final String brzina = "брзина";
				for(Javljanja javljanje : javljanja) {
					dataset.addValue(javljanje.getBrzina(), brzina, javljanje.getObjekti().getOznaka());
				}
				return dataset;
			}else {
				return null;
			}
		}catch (Exception e) {
			return null;
		}

		
	}
}
