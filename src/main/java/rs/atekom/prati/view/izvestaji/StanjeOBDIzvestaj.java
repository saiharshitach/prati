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
import pratiBaza.pomocne.StanjeOBD;
import pratiBaza.tabele.Obd;
import pratiBaza.tabele.Objekti;
import rs.atekom.prati.server.Servis;

public class StanjeOBDIzvestaj extends PrintPreviewReport<StanjeOBD>{

	private static final long serialVersionUID = 1L;
	private String decimalFormat = "###,###,###.##";
	private static final String DATUMVREME = "dd/MM/yyyy HH:mm:ss";
	private List<StanjeOBD> lista = new ArrayList<StanjeOBD>();
	
	public StanjeOBDIzvestaj(ArrayList<Objekti> objekti, Timestamp datumVremeDo) {
		setSizeUndefined();
		SimpleDateFormat datumVreme = new SimpleDateFormat(DATUMVREME);
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Style headerStyle = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM).build();
		headerStyle.setHorizontalAlign(HorizontalAlign.LEFT);
		
		Style broj = new StyleBuilder(true).setPattern(decimalFormat).setFont(Font.ARIAL_MEDIUM).build();
		broj.setHorizontalAlign(HorizontalAlign.RIGHT);
		
		Style datum = new StyleBuilder(true).setPattern(DATUMVREME).setFont(Font.ARIAL_MEDIUM).build();
		datum.setHorizontalAlign(HorizontalAlign.LEFT);
		
		Style footerStyle = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM_BOLD).build();
		/*
		CustomExpression naziv = new CustomExpression() {
			private static final long serialVersionUID = 1L;
			@Override
			public String getClassName() {
				return String.class.getName();
			}
			@Override
			public Object evaluate(Map fields, Map variables, Map parameters) {
				Objekti objekat = (Objekti) fields.get("objekti");
				return objekat.getOznaka();
			}
		};
		
		CustomExpression potrosnja = new CustomExpression() {
			private static final long serialVersionUID = 1L;
			@Override
			public String getClassName() {
				return String.class.getName();
			}
			@Override
			public Object evaluate(Map fields, Map variables, Map parameters) {
				float gorivo = (Float) fields.get("ukupnoGorivo");
				int km = (Integer) fields.get("ukupnoKm");
				float prosPotr = gorivo * 100 / km;
				return prosPotr;
			}
		};
		
		AbstractColumn objekat = ColumnBuilder.getNew()
				.setCustomExpression(naziv)
				.setTitle("објекат")
				.setStyle(headerStyle)
				.build();
		
		AbstractColumn prosPotr = ColumnBuilder.getNew()
				.setCustomExpression(potrosnja)
				.setTitle("потрошња")
				.setWidth(20)
				.setStyle(broj)
				.build();**/
		
		getReportBuilder()
		.setPageSizeAndOrientation(Page.Page_A4_Landscape())
		.setMargins(20, 20, 40, 40)
		.setDefaultEncoding(Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing)
		.setTitle("Преглед стања на возилима ")
		.addAutoText("Преглед података на дан: " + outputFormat.format(datumVremeDo) + " - " + outputFormat.format(datumVremeDo), AutoText.POSITION_HEADER, AutoText.ALIGMENT_LEFT, 450, headerStyle)
		.addAutoText("извештај урађен: " + datumVreme.format(new Date()), AutoText.POSITION_HEADER, AutoText.ALIGNMENT_RIGHT, 300, datum)
		.addAutoText("Атеком доо               www.atekom.rs                    info@atekom.rs ", AutoText.POSITION_FOOTER, AutoText.ALIGMENT_CENTER, 800, footerStyle)
		.addAutoText(AutoText.AUTOTEXT_PAGE_X, AutoText.POSITION_FOOTER, AutoText.ALIGMENT_RIGHT)
		//.addField("objekti", Objekti.class)
		//.addField("ukupnoGorivo", Float.class)
		//.addField("ukupnoKm", Integer.class)
		.setPrintBackgroundOnOddRows(true)
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("objekatNaziv", String.class)
				.setTitle("објекат")
				.setStyle(headerStyle)
				.build())
		//.addColumn(objekat)
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("ukupnoGorivo", Float.class)
				.setTitle("гориво укупно")
				.setWidth(20)
				.setStyle(broj)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("ukupnoVreme", Float.class)
				.setTitle("време укупно")
				.setWidth(20)
				.setStyle(broj)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("ukupnoKm", Integer.class)
				.setTitle("км укупно")
				.setWidth(20)
				.setStyle(broj)
				.build())
		//.addColumn(prosPotr)
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("potrosnja", Float.class)
				.setTitle("потрошња")
				.setWidth(20)
				.setStyle(broj)
				.build());
		setItems(vratiListu(objekti, datumVremeDo));
	}
	
	public List<StanjeOBD> vratiListu(ArrayList<Objekti> objekti, Timestamp datumVremeDo){
		return obracun(objekti, datumVremeDo);
	}
	
	public SerializableSupplier<List<? extends StanjeOBD>> vratiSeriju(ArrayList<Objekti> objekti, Timestamp datumVremeDo){
		SerializableSupplier<List<? extends StanjeOBD>> serija = () -> lista;
		return serija;
	}
	
	private List<StanjeOBD> obracun(ArrayList<Objekti> objekti, Timestamp datumVremeDo){
		lista.clear();
		ArrayList<Obd> obdLista = Servis.obdServis.nadjiObdPoslednji(objekti, datumVremeDo);
		for(Obd obd : obdLista) {
			StanjeOBD stanje = new StanjeOBD(obd.getObjekti().getOznaka(), obd.getUkupnoKm(), obd.getUkupnoGorivo(), obd.getUkupnoVreme());
			lista.add(stanje);
		}
		return lista;
	}
}
