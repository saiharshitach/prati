package rs.atekom.prati.view.izvestaji;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.vaadin.reports.PrintPreviewReport;

import com.ibm.icu.text.SimpleDateFormat;
import com.vaadin.server.SerializableSupplier;

import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Page;
import pratiBaza.pomocne.StajanjeMirovanje;
import pratiBaza.tabele.Objekti;
import rs.atekom.prati.server.Servis;

@SuppressWarnings("deprecation")
public class StajanjeIzvestaj extends PrintPreviewReport<StajanjeMirovanje>{

	private static final long serialVersionUID = 1L;
	private String decimalFormat = "###,###,###.##";
	private static final String DATUMVREME = "dd/MM/yyyy HH:mm:ss";
	private List<StajanjeMirovanje> lista;

	public StajanjeIzvestaj(ArrayList<Objekti> objekti, Timestamp vremeOd, Timestamp vremeDo, int duzina) {
		setSizeUndefined();
		SimpleDateFormat datumVreme = new SimpleDateFormat(DATUMVREME);
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Style headerStyle = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM).build();
		headerStyle.setHorizontalAlign(HorizontalAlign.LEFT);
		
		Style broj = new StyleBuilder(true).setPattern(decimalFormat).setFont(Font.ARIAL_MEDIUM).build();
		broj.setHorizontalAlign(HorizontalAlign.RIGHT);
		
		Style tekstLevo = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM).build();
		tekstLevo.setHorizontalAlign(HorizontalAlign.LEFT);
		
		Style tekstDesno= new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM).build();
		tekstDesno.setHorizontalAlign(HorizontalAlign.RIGHT);
		
		Style datumLevo = new StyleBuilder(true).setPattern(DATUMVREME).setFont(Font.ARIAL_MEDIUM).build();
		datumLevo.setHorizontalAlign(HorizontalAlign.LEFT);
		
		Style datumDesno = new StyleBuilder(true).setPattern(DATUMVREME).setFont(Font.ARIAL_MEDIUM).build();
		datumLevo.setHorizontalAlign(HorizontalAlign.RIGHT);
		
		Style vremeCentar = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM).build();
		vremeCentar.setHorizontalAlign(HorizontalAlign.CENTER);
		
		Style footerStyle = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM_BOLD).build();
		getReportBuilder()
		.setPageSizeAndOrientation(Page.Page_A4_Landscape())
		.setMargins(20, 20, 40, 40)
		.setDefaultEncoding(Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing)
		.setTitle("Преглед стајања и мировања")
		.addAutoText("Преглед података за период: " + outputFormat.format(vremeOd) + " - " + outputFormat.format(vremeDo), AutoText.POSITION_HEADER, AutoText.ALIGMENT_LEFT, 450, headerStyle)
		.addAutoText("извештај урађен: " + datumVreme.format(new Date()), AutoText.POSITION_HEADER, AutoText.ALIGNMENT_RIGHT, 300, datumLevo)
		.addAutoText("Атеком доо               www.atekom.rs                    info@atekom.rs ", AutoText.POSITION_FOOTER, AutoText.ALIGMENT_CENTER, 800, footerStyle)
		.addAutoText(AutoText.AUTOTEXT_PAGE_X, AutoText.POSITION_FOOTER, AutoText.ALIGMENT_RIGHT)
		.setPrintBackgroundOnOddRows(true)
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("objekat", String.class)
				.setTitle("објекат")
				.setWidth(20)
				.setStyle(tekstLevo)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("pocetak", Date.class)
				.setTitle("почетак")
				.setWidth(30)
				.setStyle(datumLevo)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("kraj", Date.class)
				.setTitle("крај")
				.setWidth(30)
				.setStyle(datumDesno)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("vremeStajanja", String.class)
				.setTitle("време стајања")
				.setWidth(10)
				.setStyle(vremeCentar)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("vremeMirovanja", String.class)
				.setTitle("време мировања")
				.setWidth(10)
				.setStyle(vremeCentar)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("opis", String.class)
				.setTitle("опис")
				.setStyle(tekstDesno)
				.build());
		setItems(vratiListu(objekti, vremeOd, vremeDo, duzina));
	}
	
	public List<StajanjeMirovanje> vratiListu(ArrayList<Objekti> objekti, Timestamp vremeOd, Timestamp vremeDo, int duzina){
		lista = Servis.javljanjeServis.vratiStajanjaMirovanja(objekti, vremeOd, vremeDo, duzina);
		return lista;
	}
	
	public SerializableSupplier<List<? extends StajanjeMirovanje>> vratiSeriju(){
		SerializableSupplier<List<? extends StajanjeMirovanje>> serija = () -> lista; 
		//Servis.javljanjeServis.vratiStajanjaMirovanja(objekti, vremeOd, vremeDo, duzina);
		return serija;
	}
}
