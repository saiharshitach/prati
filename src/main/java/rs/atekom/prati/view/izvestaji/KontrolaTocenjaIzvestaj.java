package rs.atekom.prati.view.izvestaji;

import java.sql.Timestamp;
import java.util.ArrayList;
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
import pratiBaza.pomocne.KontrolaTocenja;
import pratiBaza.tabele.Objekti;
import rs.atekom.prati.server.Servis;

@SuppressWarnings("deprecation")
public class KontrolaTocenjaIzvestaj extends PrintPreviewReport<KontrolaTocenja>{

	private static final long serialVersionUID = 1L;
	private String decimalFormat = "###,###,###.##";
	private static final String DATUMVREME = "dd/MM/yyyy HH:mm:ss";
	private List<KontrolaTocenja> lista = new ArrayList<KontrolaTocenja>();

	public KontrolaTocenjaIzvestaj(ArrayList<Objekti> objekti, Timestamp datumVremeOd, Timestamp datumVremeDo) {
		setSizeUndefined();
		SimpleDateFormat datumVreme = new SimpleDateFormat(DATUMVREME);
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		int sirina = 30;
		
		Style headerStyle = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM).build();
		headerStyle.setHorizontalAlign(HorizontalAlign.LEFT);
		
		Style broj = new StyleBuilder(true).setPattern(decimalFormat).setFont(Font.ARIAL_MEDIUM).build();
		broj.setHorizontalAlign(HorizontalAlign.RIGHT);
		
		Style datum = new StyleBuilder(true).setPattern(DATUMVREME).setFont(Font.ARIAL_MEDIUM).build();
		datum.setHorizontalAlign(HorizontalAlign.RIGHT);
		
		Style footerStyle = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM_BOLD).build();
		
		AbstractColumn brTocenja = ColumnBuilder.getNew()
				.setColumnProperty("brojSipanja", Integer.class)
				.setTitle("Бр.")
				.setWidth(10)
				.setStyle(broj)
				.build();
		
		AbstractColumn ukupno = ColumnBuilder.getNew()
				.setColumnProperty("ukupno", Float.class)
				.setTitle("Ук. лит")
				.setWidth(sirina)
				.setStyle(broj)
				.build();
		
		getReportBuilder()
		.setPageSizeAndOrientation(Page.Page_A4_Landscape())
		.setMargins(20, 20, 40, 40)
		.setDefaultEncoding(Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing)
		.setTitle("Контрола точења")
		.setGrandTotalLegend("укупно")
		.addAutoText("Преглед података за период: " + outputFormat.format(datumVremeOd) + " - " + outputFormat.format(datumVremeDo), AutoText.POSITION_HEADER, AutoText.ALIGMENT_LEFT, 450, headerStyle)
		.addAutoText("извештај урађен: " + datumVreme.format(new Date()), AutoText.POSITION_HEADER, AutoText.ALIGNMENT_RIGHT, 300, datum)
		.addAutoText("Атеком доо               www.atekom.rs                    info@atekom.rs ", AutoText.POSITION_FOOTER, AutoText.ALIGMENT_CENTER, 800, footerStyle)
		.addAutoText(AutoText.AUTOTEXT_PAGE_X, AutoText.POSITION_FOOTER, AutoText.ALIGMENT_RIGHT)
		.setPrintBackgroundOnOddRows(true)
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("oznaka", String.class)
				.setTitle("ГБ/ознака")
				.setStyle(headerStyle)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("registracija", String.class)
				.setTitle("Регистрација")
				.setStyle(headerStyle)
				.build())
		/*.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("markaTip", String.class)
				.setTitle("Марка и тип")
				.setStyle(headerStyle)
				.build())*/
		.addColumn(brTocenja)
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("pocetak", Date.class)
				.setTitle("Почетак")
				.setWidth(60)
				.setStyle(datum)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("poslednjeSipanje", Date.class)
				.setTitle("Kraj")
				.setWidth(60)
				.setStyle(datum)
				.build())
		.addColumn(ukupno)
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("gpsPut", Float.class)
				.setTitle("ГПС пут")
				.setWidth(sirina)
				.setStyle(broj)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("obdPut", Integer.class)
				.setTitle("Км. сат")
				.setWidth(sirina)
				.setStyle(broj)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("prosGps", Float.class)
				.setTitle("Прос. по ГПС")
				.setWidth(sirina)
				.setStyle(broj)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("prosObd", Float.class)
				.setTitle("Прос. по км")
				.setWidth(sirina)
				.setStyle(broj)
				.build())
		.addGlobalFooterVariable(brTocenja, DJCalculation.SUM, broj)
		.addGlobalFooterVariable(ukupno, DJCalculation.SUM, broj);
		setItems(vratiListu(objekti, datumVremeOd, datumVremeDo));
	}
	
	public List<KontrolaTocenja> vratiListu(ArrayList<Objekti> objekti, Timestamp datumVremeOd, Timestamp datumVremeDo){
		lista.clear();
		lista = Servis.javljanjeServis.vratiSipanja(objekti, datumVremeOd, datumVremeDo);
		return lista;
	}
	
	public SerializableSupplier<List<? extends KontrolaTocenja>> vratiSeriju(){
		SerializableSupplier<List<? extends KontrolaTocenja>> serija = () -> lista;
		return serija;
	}
}
