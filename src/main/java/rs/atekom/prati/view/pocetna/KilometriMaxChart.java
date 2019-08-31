package rs.atekom.prati.view.pocetna;

import java.awt.Color;
import java.util.ArrayList;
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
import pratiBaza.pomocne.PredjeniPut;


public class KilometriMaxChart {

	public KilometriMaxChart() {
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings("deprecation")
	public JFreeChart kreirajChart(ArrayList<PredjeniPut> predjeniPut) {
		final CategoryDataset dataset1 = createDataset1(predjeniPut);
		final JFreeChart chart = ChartFactory.createBarChart(
	            "Највише километара",        // chart title
	            "Објекти",               // domain axis label
	            "Укупно километара",                  // range axis label
	            dataset1,                 // data
	            PlotOrientation.VERTICAL,
	            true,                     // include legend
	            true,                     // tooltips?
	            false                     // URL generator?  Not required...
	        );
		 // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);
        chart.addSubtitle(new TextTitle("преглед највише остварених километара за претходни дан")); 
//        chart.getLegend().setAnchor(Legend.SOUTH);

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

        // add the chart to a panel...
        //final ChartPanel chartPanel = new ChartPanel(chart);
        //chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        //setContentPane(chartPanel);

		return chart;
	}
	
	private CategoryDataset createDataset1(ArrayList<PredjeniPut> predjeniPut){
		final String gps = "ГПС";
        final String obd = "ОБД";
        try{
        	final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        	for(PredjeniPut put: predjeniPut){
        		dataset.addValue(put.getVirtualOdo(), gps, put.getObjekatNaziv());
        		dataset.addValue(put.getUkupnoKm(), obd, put.getObjekatNaziv());
        		}
        	return dataset;
        	}catch(Exception e){
            	System.out.println("problem " + e);
            	return null;
            }
	}
}
