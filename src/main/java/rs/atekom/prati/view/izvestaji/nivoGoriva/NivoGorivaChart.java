package rs.atekom.prati.view.izvestaji.nivoGoriva;

import java.util.ArrayList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.Obd;

public class NivoGorivaChart {
	
	public NivoGorivaChart() {
		// TODO Auto-generated constructor stub
	}
	
	public JFreeChart kreirajChart(ArrayList<Javljanja> javljanja, ArrayList<Obd> obd) {
		final XYDataset dataset = createDataSet(javljanja, obd);
		final JFreeChart chart = ChartFactory.createTimeSeriesChart("Преглед нивоа горива и брзине", "Време", "Ниво/Брзина", dataset, true, true, false);
		
		return chart;
	}
	
	private XYDataset createDataSet(ArrayList<Javljanja> javljanja, ArrayList<Obd> obd) {
	    TimeSeriesCollection dataset = new TimeSeriesCollection();
	    
	    TimeSeries nivo = new TimeSeries("ниво");
	    TimeSeries brzina = new TimeSeries("брзина");
	    
	    try {
	    	for(int i = 0; i < javljanja.size(); i++) {
	    		double nv;
	    		RegularTimePeriod pn;
	    		int brz;
	    		RegularTimePeriod pj;
	    		
	    		try {
	    			brz = javljanja.get(i).getBrzina();
	    			pj = new Second(javljanja.get(i).getDatumVreme());
	    		}catch (Exception e) {
					brz = 0;
					pj = null;
				}
	    		if(pj != null) {
	    			brzina.addOrUpdate(pj, brz);
	    		}
	    		
	    		if(i < obd.size()) {
		    		try {
			    		nv = obd.get(i).getNivoGoriva();
			    		pn = new Second(obd.get(i).getDatumVreme());
		    		}catch (Exception e) {
		    			nv = 0.0;
						pn = null;
					}
		    		if(pn != null) {
		    			nivo.addOrUpdate(pn, nv);
		    		}
	    		}
	    	}
	    	
	    	dataset.addSeries(nivo);
	    	dataset.addSeries(brzina);
		    return dataset;
	    }catch(Exception e){
        	System.out.println("problem sa nivoom goriva" + e);
        	return null;
        }
	}
}
