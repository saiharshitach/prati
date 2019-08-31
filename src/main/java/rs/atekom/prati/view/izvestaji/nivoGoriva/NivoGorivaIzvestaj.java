package rs.atekom.prati.view.izvestaji.nivoGoriva;

import java.awt.Color;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import org.jfree.data.time.Second;
import org.vaadin.reports.PrintPreviewReport;
import com.ibm.icu.text.SimpleDateFormat;
import com.vaadin.server.SerializableSupplier;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.chart.DJChartOptions;
import ar.com.fdvs.dj.domain.chart.builder.DJTimeSeriesChartBuilder;
import ar.com.fdvs.dj.domain.chart.plot.DJAxisFormat;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import pratiBaza.tabele.Obd;
import pratiBaza.tabele.Objekti;
import rs.atekom.prati.server.Servis;

public class NivoGorivaIzvestaj extends PrintPreviewReport<Obd>{

	private static final long serialVersionUID = 1L;
	private String decimalFormat = "###,###,###.##";
	private static final String DATUMVREME = "dd/MM/yyyy HH:mm:ss";
	private DynamicReportBuilder drb; 

	public NivoGorivaIzvestaj(Objekti objekat, Timestamp datumVremeOd, Timestamp datumVremeDo) {
		//setImageServletPathPattern("izvestaj-slika?image={0}");
		setSizeUndefined();
		drb = new DynamicReportBuilder();
		drb.setPageSizeAndOrientation(Page.Page_A4_Landscape());
		
		SimpleDateFormat datumVreme = new SimpleDateFormat(DATUMVREME);
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Style headerStyle = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM).build();
		headerStyle.setHorizontalAlign(HorizontalAlign.LEFT);
		
		Style broj = new StyleBuilder(true).setPattern(decimalFormat).setFont(Font.ARIAL_MEDIUM).build();
		broj.setHorizontalAlign(HorizontalAlign.RIGHT);
		
		Style ceoBroj = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM).build();
		ceoBroj.setHorizontalAlign(HorizontalAlign.LEFT);
		
		Style datum = new StyleBuilder(true).setPattern(DATUMVREME).setFont(Font.ARIAL_MEDIUM).build();
		datum.setHorizontalAlign(HorizontalAlign.LEFT);
		
		Style footerStyle = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM_BOLD).build();
		
		AbstractColumn nivo = ColumnBuilder.getNew()
				.setColumnProperty("nivoGoriva", Float.class)
				.setTitle("ниво горива")
				.setWidth(10)
				.setStyle(broj)
				.build();
		
		AbstractColumn datumVr = ColumnBuilder.getNew()
				.setColumnProperty("datumVreme", Date.class)
				.setTitle("датум")
				.setStyle(datum)
				.build();
		
		DJAxisFormat timeAxisFormat = new DJAxisFormat("време");
		timeAxisFormat.setLabelFont(Font.ARIAL_SMALL);
		timeAxisFormat.setLabelColor(Color.DARK_GRAY);
		timeAxisFormat.setTickLabelFont(Font.ARIAL_SMALL);
		timeAxisFormat.setTickLabelColor(Color.DARK_GRAY);
		timeAxisFormat.setTickLabelMask("HH:mm");
		timeAxisFormat.setLineColor(Color.DARK_GRAY);
		
		DJAxisFormat categoryAxisFormat = new DJAxisFormat("x");
		categoryAxisFormat.setLabelFont(Font.ARIAL_SMALL);
		categoryAxisFormat.setLabelColor(Color.DARK_GRAY);
		categoryAxisFormat.setTickLabelFont(Font.ARIAL_SMALL);
		categoryAxisFormat.setTickLabelColor(Color.DARK_GRAY);
		categoryAxisFormat.setTickLabelMask("#,###.#");
		categoryAxisFormat.setLineColor(Color.DARK_GRAY);

		DJAxisFormat valueAxisFormat = new DJAxisFormat("ниво %");
		valueAxisFormat.setLabelFont(Font.ARIAL_SMALL);
		valueAxisFormat.setLabelColor(Color.DARK_GRAY);
		valueAxisFormat.setTickLabelFont(Font.ARIAL_SMALL);
		valueAxisFormat.setTickLabelColor(Color.DARK_GRAY);
		valueAxisFormat.setTickLabelMask("#,##0.0");
		valueAxisFormat.setLineColor(Color.DARK_GRAY);

		getReportBuilder()
		.setPageSizeAndOrientation(Page.Page_A4_Landscape())
		.setMargins(20, 20, 40, 40)
		.setDefaultEncoding(Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing)
		.setTitle("Преглед нивоа горива за " + objekat.getOznaka())
		.addAutoText("Преглед података за период: " + outputFormat.format(datumVremeOd) + " - " + outputFormat.format(datumVremeDo), AutoText.POSITION_HEADER, AutoText.ALIGMENT_LEFT, 450, headerStyle)
		.addAutoText("извештај урађен: " + datumVreme.format(new Date()), AutoText.POSITION_HEADER, AutoText.ALIGNMENT_RIGHT, 300, datum)
		.addAutoText("Атеком доо               www.atekom.rs                    info@atekom.rs ", AutoText.POSITION_FOOTER, AutoText.ALIGMENT_CENTER, 800, footerStyle)
		.addAutoText(AutoText.AUTOTEXT_PAGE_X, AutoText.POSITION_FOOTER, AutoText.ALIGMENT_RIGHT)
		.setPrintBackgroundOnOddRows(true)
		.setShowDetailBand(false)
		.setUseFullPageWidth(true)
		.addColumn(datumVr)
		.addColumn(nivo)
		.addChart(new DJTimeSeriesChartBuilder()
				//chart		
				.setX(0)
				.setY(0)
				//.setWidth(800)
				//.setHeight(250)
				.setCentered(true)
				.setBackColor(Color.LIGHT_GRAY)
				.setShowLegend(true)
				.setPosition(DJChartOptions.POSITION_HEADER)
				.setTitle("")
				.setTitleColor(Color.DARK_GRAY)
				.setTitleFont(Font.ARIAL_BIG_BOLD)
				.setSubtitle("subtitle")
				.setSubtitleColor(Color.DARK_GRAY)
				.setSubtitleFont(Font.COURIER_NEW_BIG_BOLD)
				.setLegendColor(Color.DARK_GRAY)
				.setLegendFont(Font.COURIER_NEW_MEDIUM_BOLD)
				.setLegendBackgroundColor(Color.WHITE)
				.setLegendPosition(DJChartOptions.EDGE_BOTTOM)
				.setTitlePosition(DJChartOptions.EDGE_TOP)
				.setLineStyle(DJChartOptions.LINE_STYLE_DOUBLE)
				.setLineWidth(1)
				.setLineColor(Color.DARK_GRAY)
				.setPadding(5)
				//dataset
				.setTimePeriod((PropertyColumn)datumVr)
				//.setTimePeriodClass((datumVremeDo.getTime() - datumVremeOd.getTime())/1000 > 7200 ? Hour.class : Minute.class)
				.setTimePeriodClass(Second.class)
				.addSerie(nivo)
				//plot
				.setShowShapes(true)
				.setShowLines(true)
				.setTimeAxisFormat(timeAxisFormat)
				.setValueAxisFormat(valueAxisFormat)
				.build());
		
		setItems(vratiListu(objekat, datumVremeOd, datumVremeDo));
	}
	
	public List<Obd> vratiListu(Objekti objekat, Timestamp datumVremeOd, Timestamp datumVremeDo){
		return Servis.obdServis.nadjiObdPoObjektuOdDo(objekat, datumVremeOd, datumVremeDo);
	}
	
	public SerializableSupplier<List<? extends Obd>> vratiSeriju(Objekti objekat, Timestamp datumVremeOd, Timestamp datumVremeDo){
		SerializableSupplier<List<? extends Obd>> serija = () -> Servis.obdServis.nadjiObdPoObjektuOdDo(objekat, datumVremeOd, datumVremeDo);
		return serija;
	}
	/*
	 * @WebServlet("/report-image")
           public static class ReportsImageServlet extends ImageServlet {
       }

       You can configure the URL pattern using the setImageServletPathPattern method (default to report-image?image={0}
	 */
	/*
	 * 		.addChart(new DJXYLineChartBuilder()
				.setX(100)
				.setY(10)
				.setWidth(500)
				.setHeight(250)
				.setCentered(false)
				.setBackColor(Color.LIGHT_GRAY)
				.setShowLegend(true)
				.setPosition(DJChartOptions.POSITION_FOOTER)
				.setTitle("ниво горива")
				.setTitleColor(Color.DARK_GRAY)
				.setTitleFont(Font.ARIAL_BIG_BOLD)
				.setSubtitle("преглед")
				.setSubtitleColor(Color.DARK_GRAY)
				.setSubtitleFont(Font.COURIER_NEW_BIG_BOLD)
				.setLegendColor(Color.DARK_GRAY)
				.setLegendFont(Font.COURIER_NEW_MEDIUM_BOLD)
				.setLegendBackgroundColor(Color.WHITE)
				.setLegendPosition(DJChartOptions.EDGE_BOTTOM)
				.setTitlePosition(DJChartOptions.EDGE_TOP)
				.setLineStyle(DJChartOptions.LINE_STYLE_DOTTED)
				.setLineWidth(1)
				.setLineColor(Color.DARK_GRAY)
				.setPadding(5)
				//dataset
				.setXValue((PropertyColumn) id)
				.addSerie(nivo, "ниво горива")
				//.addSerie(id)
				//plot
				.setShowShapes(true)
				.setShowLines(true)
				.setCategoryAxisFormat(categoryAxisFormat)
				.setValueAxisFormat(valueAxisFormat)
				.build());
				
	private Component buildChartReport() {
        AbstractColumn city;
        AbstractColumn calls;
        PrintPreviewReport<CityCallsCount> report = new PrintPreviewReport<>();
        report.getReportBuilder()
                .setTitle("Worldwide Distribution")
                .addColumn(city = ColumnBuilder.getNew()
                        .setColumnProperty("city", String.class)
                        .setTitle("City")
                        .build())
                .addColumn(calls = ColumnBuilder.getNew()
                        .setColumnProperty("calls", Integer.class)
                        .setTitle("Calls")
                        .build())
                .addChart(new DJPieChartBuilder()
                        .setColumnGroup((PropertyColumn) city)
                        .addSerie(calls)
                        .build());

        report.setItems(CallRepository.getCountPerCity());

        return report;
     }
	 */
}
