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
import pratiBaza.pomocne.KontrolaGoriva;
import pratiBaza.tabele.Objekti;
import rs.atekom.prati.server.Servis;

@SuppressWarnings("deprecation")
public class KontrolaGorivaIzvestaj extends PrintPreviewReport<KontrolaGoriva>{

	private static final long serialVersionUID = 1L;
	private String decimalFormat = "###,###,###.##";
	private static final String DATUMVREME = "dd/MM/yyyy HH:mm:ss";
	private List<KontrolaGoriva> lista = new ArrayList<KontrolaGoriva>();
	
	public KontrolaGorivaIzvestaj(ArrayList<Objekti> objekti, Timestamp datumVremeOd, Timestamp datumVremeDo) {
		setSizeUndefined();
		SimpleDateFormat datumVreme = new SimpleDateFormat(DATUMVREME);
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		int sirina = 30;
		
		Style headerStyle = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM).build();
		headerStyle.setHorizontalAlign(HorizontalAlign.LEFT);
		
		Style broj = new StyleBuilder(true).setPattern(decimalFormat).setFont(Font.ARIAL_MEDIUM).build();
		broj.setHorizontalAlign(HorizontalAlign.RIGHT);
		
		Style datum = new StyleBuilder(true).setPattern(DATUMVREME).setFont(Font.ARIAL_MEDIUM).build();
		datum.setHorizontalAlign(HorizontalAlign.LEFT);
		
		Style footerStyle = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM_BOLD).build();
		
		getReportBuilder()
		.setPageSizeAndOrientation(Page.Page_A4_Landscape())
		.setMargins(20, 20, 40, 40)
		.setDefaultEncoding(Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing)
		.setTitle("Контрола горива")
		.addAutoText("Преглед података за период: " + outputFormat.format(datumVremeOd) + " - " + outputFormat.format(datumVremeDo), AutoText.POSITION_HEADER, AutoText.ALIGMENT_LEFT, 450, headerStyle)
		.addAutoText("извештај урађен: " + datumVreme.format(new Date()), AutoText.POSITION_HEADER, AutoText.ALIGNMENT_RIGHT, 300, datum)
		.addAutoText("Атеком доо               www.atekom.rs                    info@atekom.rs ", AutoText.POSITION_FOOTER, AutoText.ALIGMENT_CENTER, 800, footerStyle)
		.addAutoText(AutoText.AUTOTEXT_PAGE_X, AutoText.POSITION_FOOTER, AutoText.ALIGMENT_RIGHT)
		.setPrintBackgroundOnOddRows(true)
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("objekatNaziv", String.class)
				.setTitle("Објекат")
				.setStyle(headerStyle)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("registracija", String.class)
				.setTitle("Регистрација")
				.setStyle(headerStyle)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("virtualOdo", Float.class)
				.setTitle("Пут гпс")
				.setWidth(sirina)
				.setStyle(broj)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("ukupnoKm", Float.class)
				.setTitle("Пут одометар")
				.setWidth(sirina)
				.setStyle(broj)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("ukupnoGorivo", Float.class)
				.setTitle("Потр. горива")
				.setWidth(sirina)
				.setStyle(broj)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("prosPotGps", Float.class)
				.setTitle("Потр./гпс")
				.setWidth(sirina)
				.setStyle(broj)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("prosPotr", Float.class)
				.setTitle("Прос потрошња")
				.setWidth(sirina)
				.setStyle(broj)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("potrosnja", Float.class)
				.setTitle("Деф. потрошња")
				.setWidth(sirina)
				.setStyle(broj)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("kolicina", Float.class)
				.setTitle("Купљена кол.")
				.setWidth(sirina)
				.setStyle(broj)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("gpsKolicina", Float.class)
				.setTitle("Кол./гпс")
				.setWidth(sirina)
				.setStyle(broj)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("odoKolicina", Float.class)
				.setTitle("Кол./обд")
				.setWidth(sirina)
				.setStyle(broj)
				.build());
		setItems(vratiListu(objekti, datumVremeOd, datumVremeDo));
	}

	public List<KontrolaGoriva> vratiListu(ArrayList<Objekti> objekti, Timestamp datumVremeOd, Timestamp datumVremeDo){
		lista.clear();
		lista = Servis.javljanjeServis.vratiKontroluGoriva(objekti, datumVremeOd, datumVremeDo);
		return lista;
	}
	
	public SerializableSupplier<List<? extends KontrolaGoriva>> vratiSeriju(){
		SerializableSupplier<List<? extends KontrolaGoriva>> serija = () -> lista;
		return serija;
	}
}
