package rs.atekom.prati.izvestaji;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.servlet.annotation.WebServlet;
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
import net.sf.jasperreports.j2ee.servlets.ImageServlet;
import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.SistemAlarmi;
import rs.atekom.prati.server.Servis;


public class ZoneIzvestaj extends PrintPreviewReport<Javljanja>{

	@WebServlet("/izvestaj-zone")
    public static class ReportsImageServlet extends ImageServlet {
		private static final long serialVersionUID = 1L;
	}
	private static final long serialVersionUID = 1L;
	private String decimalFormat = "###,###,###.##";
	private static final String DATUMVREME = "dd/MM/yyyy HH:mm:ss";

	public ZoneIzvestaj(ArrayList<Objekti> objekti, Timestamp datumVremeOd, Timestamp datumVremeDo, ArrayList<SistemAlarmi> alarmi) {
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
		
		getReportBuilder()
		.setPageSizeAndOrientation(Page.Page_A4_Landscape())
		.setMargins(20, 20, 40, 40)
		.setDefaultEncoding(Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing)
		.setTitle("Преглед улаза/излаза из зона")
		.addAutoText("Преглед података за период: " + outputFormat.format(datumVremeOd) + " - " + outputFormat.format(datumVremeDo), AutoText.POSITION_HEADER, AutoText.ALIGMENT_LEFT, 450, headerStyle)
		.addAutoText("извештај урађен: " + datumVreme.format(new Date()), AutoText.POSITION_HEADER, AutoText.ALIGNMENT_RIGHT, 300, datum)
		.addAutoText("Атеком доо               www.atekom.rs                    info@atekom.rs ", AutoText.POSITION_FOOTER, AutoText.ALIGMENT_CENTER, 800, footerStyle)
		.addAutoText(AutoText.AUTOTEXT_PAGE_X, AutoText.POSITION_FOOTER, AutoText.ALIGMENT_RIGHT)
		.setPrintBackgroundOnOddRows(true)
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("nazivObjekta", String.class)
				.setTitle("објекат")
				.setWidth(20)
				.setStyle(datum)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("nazivAlarma", String.class)
				.setTitle("догађај")
				.setWidth(20)
				.setStyle(datum)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("nazivZone", String.class)
				.setTitle("зона")
				.setWidth(20)
				.setStyle(datum)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("datumVreme", Date.class)
				.setTitle("датум и време")
				.setWidth(20)
				.setStyle(datum)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("eventData", String.class)
				.setTitle("опис")
				.setStyle(datum)
				.build());
		setItems(obracun(objekti, datumVremeOd, datumVremeDo, alarmi));
	}
	
	public List<Javljanja> vratiListu(ArrayList<Objekti> objekti, Timestamp datumVremeOd, Timestamp datumVremeDo, ArrayList<SistemAlarmi> alarmi){
		return obracun(objekti, datumVremeOd, datumVremeDo, alarmi);
	}
	
	public SerializableSupplier<List<? extends Javljanja>> vratiSeriju(ArrayList<Objekti> objekti, Timestamp datumVremeOd, Timestamp datumVremeDo, ArrayList<SistemAlarmi> alarmi){
		SerializableSupplier<List<? extends Javljanja>> serija = () -> obracun(objekti, datumVremeOd, datumVremeDo, alarmi);;
		return serija;
	}
	
	private List<Javljanja> obracun(ArrayList<Objekti> objekti, Timestamp datumVremeOd, Timestamp datumVremeDo, ArrayList<SistemAlarmi> alarmi){
		List<Javljanja> lista = new ArrayList<Javljanja>();
		for(Objekti objekat : objekti) {
			ArrayList<Javljanja> javljanja = Servis.javljanjeServis.vratiJavljanjaObjektaOdDoSaAlarmimaZona(objekat, datumVremeOd, datumVremeDo, alarmi);
			Collections.sort(javljanja, (o1, o2) -> o1.getDatumVreme().compareTo(o2.getDatumVreme()));
			lista.addAll(javljanja);
		}
		System.out.println("broj objekata " + objekti.size() + " javljanja " + lista.size());
		return lista;
	}
	
}
