package rs.atekom.prati.view.izvestaji;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import org.vaadin.reports.PrintPreviewReport;
import com.ibm.icu.text.SimpleDateFormat;
import com.vaadin.server.SerializableSupplier;
import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import pratiBaza.pomocne.RadnoVremePutGPS;
import pratiBaza.tabele.Objekti;
import rs.atekom.prati.server.Servis;

public class RadnoVremeGPSIzvestaj extends PrintPreviewReport<RadnoVremePutGPS>{
	
	private static final long serialVersionUID = 1L;
	private String decimalFormat = "###,###,###.##";
	private static final String DATUMVREME = "dd/MM/yyyy HH:mm:ss";
	private static final String DATUM = "yyyy-MM-dd";

	public RadnoVremeGPSIzvestaj(Objekti objekat, Timestamp datumVremeOd, Timestamp datumVremeDo, int satiOd, int satiDo) {
		setSizeUndefined();
		SimpleDateFormat datumVreme = new SimpleDateFormat(DATUMVREME);
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Style headerStyle = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM).build();
		headerStyle.setHorizontalAlign(HorizontalAlign.LEFT);
		
		Style broj = new StyleBuilder(true).setPattern(decimalFormat).setFont(Font.ARIAL_MEDIUM).build();
		broj.setHorizontalAlign(HorizontalAlign.RIGHT);
		
		Style datum = new StyleBuilder(true).setPattern(DATUMVREME).setFont(Font.ARIAL_MEDIUM).build();
		datum.setHorizontalAlign(HorizontalAlign.LEFT);
		
		Style dan = new StyleBuilder(true).setPattern(DATUM).setFont(Font.ARIAL_MEDIUM).build();
		datum.setHorizontalAlign(HorizontalAlign.LEFT);
		
		Style footerStyle = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM_BOLD).build();
		
		AbstractColumn predjeniPut = ColumnBuilder.getNew()
				.setColumnProperty("predjeniPut", Float.class)
				.setTitle("пређени пут")
				.setWidth(10)
				.setStyle(broj)
				.build();
		
		getReportBuilder()
		.setPageSizeAndOrientation(Page.Page_A4_Landscape())
		.setMargins(20, 20, 40, 40)
		.setDefaultEncoding(Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing)
		.setTitle("Радно време - пређени пут за објекат " + objekat.getOznaka())
		.setGrandTotalLegend("укупно")
		.addAutoText("Преглед података за период: " + outputFormat.format(datumVremeOd) + " - " + outputFormat.format(datumVremeDo), AutoText.POSITION_HEADER, AutoText.ALIGMENT_LEFT, 450, headerStyle)
		.addAutoText("извештај урађен: " + datumVreme.format(new Date()), AutoText.POSITION_HEADER, AutoText.ALIGNMENT_RIGHT, 300, datum)
		.addAutoText("Атеком доо               www.atekom.rs                    info@atekom.rs ", AutoText.POSITION_FOOTER, AutoText.ALIGMENT_CENTER, 800, footerStyle)
		.addAutoText(AutoText.AUTOTEXT_PAGE_X, AutoText.POSITION_FOOTER, AutoText.ALIGMENT_RIGHT)
		.setPrintBackgroundOnOddRows(true)
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("datum", Date.class)
				.setTitle("датум")
				.setStyle(dan)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("pocetak", Date.class)
				.setTitle("почетак")
				.setStyle(datum)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("kraj", Date.class)
				.setTitle("крај")
				.setStyle(datum)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("maxBrzina", Float.class)
				.setTitle("макс брзина")
				.setWidth(10)
				.setStyle(broj)
				.build())
		.addColumn(predjeniPut)
		.addGlobalFooterVariable(predjeniPut, DJCalculation.SUM, broj);
		
		setItems(vratiListu(objekat, datumVremeOd, datumVremeDo, satiOd, satiDo));
	}
	
	public List<RadnoVremePutGPS> vratiListu(Objekti objekat, Timestamp datumVremeOd, Timestamp datumVremeDo, int satiOd, int satiDo){
		return Servis.proceduraServis.radnoVremePutGPS(objekat.getId().intValue(), datumVremeOd, datumVremeDo, satiOd, satiDo);
	}
	
	public SerializableSupplier<List<? extends RadnoVremePutGPS>> vratiSeriju(Objekti objekat, Timestamp datumVremeOd, Timestamp datumVremeDo, int satiOd, int satiDo){
		SerializableSupplier<List<? extends RadnoVremePutGPS>> serija = () -> Servis.proceduraServis.radnoVremePutGPS(objekat.getId().intValue(), datumVremeOd, datumVremeDo, satiOd, satiDo);
		return serija;
	}
}
