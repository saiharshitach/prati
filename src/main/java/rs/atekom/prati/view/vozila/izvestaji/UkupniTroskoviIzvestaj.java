package rs.atekom.prati.view.vozila.izvestaji;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.vaadin.reports.PrintPreviewReport;
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
import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.Troskovi;
import rs.atekom.prati.server.Servis;

@SuppressWarnings("deprecation")
public class UkupniTroskoviIzvestaj extends PrintPreviewReport<Troskovi>{

	private static final long serialVersionUID = 1L;
	private String decimalFormat = "###,###,###.##";
	private static final String DANFORMAT2 = "dd/MM/yyyy";
	private static final String DATUMVREME = "dd/MM/yyyy HH:mm:ss";
	private List<Troskovi> lista;

	public UkupniTroskoviIzvestaj(ArrayList<Objekti> vozila, Timestamp datumVremeOd, Timestamp datumVremeDo, Integer tipTroska) {
		lista = new ArrayList<Troskovi>();
		setSizeUndefined();
		SimpleDateFormat datumVreme = new SimpleDateFormat(DATUMVREME);
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");//"dd-MM-yyyy HH:mm:ss"
		
		Style headerStyle = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM).build();
		headerStyle.setHorizontalAlign(HorizontalAlign.LEFT);
		
		Style broj = new StyleBuilder(true).setPattern(decimalFormat).setFont(Font.ARIAL_MEDIUM).build();
		broj.setHorizontalAlign(HorizontalAlign.RIGHT);
		
		Style tekst = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM).build();

		broj.setHorizontalAlign(HorizontalAlign.RIGHT);
		
		Style datum = new StyleBuilder(true).setPattern(DANFORMAT2).setFont(Font.ARIAL_MEDIUM).build();
		datum.setHorizontalAlign(HorizontalAlign.LEFT);
		
		Style footerStyle = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM_BOLD).build();
		
		AbstractColumn ukupno = ColumnBuilder.getNew()
				.setColumnProperty("ukupno", Float.class)
				.setTitle("????????????")
				.setWidth(25)
				.setStyle(broj)
				.build();
		
		AbstractColumn kolicina = ColumnBuilder.getNew()
				.setColumnProperty("kolicina", Float.class)
				.setTitle("????????????????")
				.setWidth(25)
				.setStyle(broj)
				.build();
		
		getReportBuilder()
		.setPageSizeAndOrientation(Page.Page_A4_Landscape())
		.setMargins(20, 20, 40, 40)
		.setDefaultEncoding(Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing)
		.setTitle("?????????????? ????????????????")
		.setGrandTotalLegend("????????????")
		.addAutoText("?????????????? ???????????????? ???? ????????????: " + outputFormat.format(datumVremeOd) + " - " + outputFormat.format(datumVremeDo), AutoText.POSITION_HEADER, AutoText.ALIGMENT_LEFT, 450, headerStyle)
		.addAutoText("???????????????? ????????????: " + datumVreme.format(new Date()), AutoText.POSITION_HEADER, AutoText.ALIGNMENT_RIGHT, 300, datum)
		.addAutoText("???????????? ??????               www.atekom.rs                    info@atekom.rs ", AutoText.POSITION_FOOTER, AutoText.ALIGMENT_CENTER, 800, footerStyle)
		.addAutoText(AutoText.AUTOTEXT_PAGE_X, AutoText.POSITION_FOOTER, AutoText.ALIGMENT_RIGHT)
		//.addField("objekti", Objekti.class)
		.setPrintBackgroundOnOddRows(true)
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("datumVreme", Timestamp.class)
				.setTitle("??????????")
				.setStyle(datum)
				.setWidth(25)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("objekatOznaka", String.class)
				.setTitle("??????????????")
				.setStyle(tekst)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("registracija", String.class)
				.setTitle("????????????????????????")
				.setStyle(tekst)
				.setWidth(25)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("marka", String.class)
				.setTitle("??????????")
				.setStyle(tekst)
				.setWidth(30)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("model", String.class)
				.setTitle("??????????")
				.setStyle(tekst)
				.setWidth(25)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("partnerNaziv", String.class)
				.setTitle("??????????????")
				.setStyle(tekst)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("tipServisaNaziv", String.class)
				.setTitle("?????? ????????????")
				.setStyle(tekst)
				.setWidth(30)
				.build())
		/*.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("brojRacuna", String.class)
				.setTitle("??????????")
				.setStyle(broj)
				.setWidth(20)
				.build())*/
		/*.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("kolicina", Float.class)
				.setTitle("??????")
				.setWidth(20)
				.setStyle(broj)
				.build())*/
		.addColumn(kolicina)
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("cena", Float.class)
				.setTitle("????????")
				.setWidth(20)
				.setStyle(broj)
				.build())
		.addColumn(ukupno)
		.addGlobalFooterVariable(kolicina, DJCalculation.SUM, broj)
		.addGlobalFooterVariable(ukupno, DJCalculation.SUM, broj);
		setItems(vratiListu(vozila, datumVremeOd, datumVremeDo, tipTroska));
	}
	
	public List<Troskovi> vratiListu(ArrayList<Objekti> vozila, Timestamp datumVremeOd, Timestamp datumVremeDo, Integer tipTroska){
		lista.clear();
		lista = Servis.trosakServis.nadjiSveTroskoveUkupno(vozila, datumVremeOd, datumVremeDo, tipTroska);
		return lista;
	}
	
	public SerializableSupplier<List<? extends Troskovi>> vratiSeriju(){
		SerializableSupplier<List<? extends Troskovi>> serija = () -> lista;
		return serija;
	}

}
