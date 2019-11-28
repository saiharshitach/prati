package rs.atekom.prati.view.vozila.izvestaji;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.vaadin.reports.PrintPreviewReport;

import com.vaadin.server.SerializableSupplier;

import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Page;
import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.Vozila;
import rs.atekom.prati.server.Servis;

public class DoVelikogServisaIzvestaj extends PrintPreviewReport<Vozila>{

	private static final long serialVersionUID = 1L;
	private String decimalFormat = "###,###,###.##";
	private static final String DANFORMAT2 = "dd/MM/yyyy";
	private static final String DATUMVREME = "dd/MM/yyyy HH:mm:ss";
	private List<Vozila> lista;

	public DoVelikogServisaIzvestaj(ArrayList<Objekti> objekti, int tipServisa, int doServisa) {
		lista = new ArrayList<Vozila>();
		setSizeUndefined();
		SimpleDateFormat datumVreme = new SimpleDateFormat(DATUMVREME);
		//SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
		
		Style headerStyle = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM).build();
		headerStyle.setHorizontalAlign(HorizontalAlign.LEFT);
		
		Style broj = new StyleBuilder(true).setPattern(decimalFormat).setFont(Font.ARIAL_MEDIUM).build();
		broj.setHorizontalAlign(HorizontalAlign.RIGHT);
		
		Style tekst = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM).build();

		broj.setHorizontalAlign(HorizontalAlign.RIGHT);
		
		Style datum = new StyleBuilder(true).setPattern(DANFORMAT2).setFont(Font.ARIAL_MEDIUM).build();
		datum.setHorizontalAlign(HorizontalAlign.LEFT);
		
		Style footerStyle = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM_BOLD).build();
		
		getReportBuilder()
		.setPageSizeAndOrientation(Page.Page_A4_Landscape())
		.setMargins(20, 20, 40, 40)
		.setDefaultEncoding(Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing)
		.setTitle("Преглед возила за велики сервис")
		.setGrandTotalLegend("укупно")
		.addAutoText("Преглед возила за велики сервис за мање од " + doServisa + "км" , AutoText.POSITION_HEADER, AutoText.ALIGMENT_LEFT, 450, headerStyle)
		.addAutoText("извештај урађен: " + datumVreme.format(new Date()), AutoText.POSITION_HEADER, AutoText.ALIGNMENT_RIGHT, 300, datum)
		.addAutoText("Атеком доо               www.atekom.rs                    info@atekom.rs ", AutoText.POSITION_FOOTER, AutoText.ALIGMENT_CENTER, 800, footerStyle)
		.addAutoText(AutoText.AUTOTEXT_PAGE_X, AutoText.POSITION_FOOTER, AutoText.ALIGMENT_RIGHT)
		//.addField("objekti", Objekti.class)
		.setPrintBackgroundOnOddRows(true)
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("registracija", String.class)
				.setTitle("регистрација")
				.setStyle(tekst)
				.setWidth(30)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("model", String.class)
				.setTitle("модел")
				.setStyle(tekst)
				.setWidth(25)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("marka", String.class)
				.setTitle("марка")
				.setStyle(tekst)
				.setWidth(25)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("velikiPoslednjiDatum", Date.class)
				.setTitle("датум")
				.setStyle(datum)
				.setWidth(25)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("danaOdVs", Integer.class)
				.setTitle("дана")
				.setStyle(broj)
				.setWidth(12)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("velikiPoslednjiGPSkm", Float.class)
				.setTitle("урађен на ГПС км")
				.setStyle(broj)
				//.setWidth(30)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("kmOdGpsVs", Float.class)
				.setTitle("ГПС км од сервиса")
				.setStyle(broj)
				//.setWidth(20)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("velikiPoslednjiOBDkm", Integer.class)
				.setTitle("урађен на ОБД км")
				//.setWidth(10)
				.setStyle(broj)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("kmOdObdVs", Integer.class)
				.setTitle("ОБД км од сервиса")
				//.setWidth(10)
				.setStyle(broj)
				.build());
		setItems(vratiListu(objekti, tipServisa, doServisa));
	}
	
	
	public List<Vozila> vratiListu(ArrayList<Objekti> objekti, int tipServisa, int doServisa){
		return obracun(objekti, tipServisa, doServisa);
	}
	
	public SerializableSupplier<List<? extends Vozila>> vratiSeriju(ArrayList<Objekti> objekti, int tipServisa, int doServisa){
		SerializableSupplier<List<? extends Vozila>> serija = () ->lista;
		return serija;
	}
	
	private List<Vozila> obracun(ArrayList<Objekti> objekti, int tipServisa, int doServisa){
		lista.clear();
		lista = Servis.javljanjeServis.vratiVozilaZaServise(objekti, tipServisa, doServisa);
		return lista;
	}
}
