package rs.cybertrade.prati.izvestaji;

import java.sql.Timestamp;
import java.util.ArrayList;
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
import pratiBaza.pomocne.PredjeniPut;
import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.Obd;
import pratiBaza.tabele.Objekti;
import rs.cybertrade.prati.server.Servis;

public class PredjeniPutIzvestaj extends PrintPreviewReport<PredjeniPut>{
	
	@WebServlet("/izvestaj-predjeniPut")
    public static class ReportsImageServlet extends ImageServlet {
		private static final long serialVersionUID = 1L;
	}
	private static final long serialVersionUID = 1L;
	private String decimalFormat = "###,###,###.##";
	private static final String DATUMVREME = "dd/MM/yyyy HH:mm:ss";

	public PredjeniPutIzvestaj(ArrayList<Objekti> objekti, Timestamp datumVremeOd, Timestamp datumVremeDo) {
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
		.setTitle("Преглед пређеног пута")
		.addAutoText("Преглед података за период: " + outputFormat.format(datumVremeOd) + " - " + outputFormat.format(datumVremeDo), AutoText.POSITION_HEADER, AutoText.ALIGMENT_LEFT, 450, headerStyle)
		.addAutoText("извештај урађен: " + datumVreme.format(new Date()), AutoText.POSITION_HEADER, AutoText.ALIGNMENT_RIGHT, 300, datum)
		.addAutoText("Атеком доо               www.atekom.rs                    info@atekom.rs ", AutoText.POSITION_FOOTER, AutoText.ALIGMENT_CENTER, 800, footerStyle)
		.addAutoText(AutoText.AUTOTEXT_PAGE_X, AutoText.POSITION_FOOTER, AutoText.ALIGMENT_RIGHT)
		.setPrintBackgroundOnOddRows(true)
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("objekatNaziv", String.class)
				.setTitle("објекат")
				.setStyle(headerStyle)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("virtualOdo", Float.class)
				.setTitle("пређени пут гпс")
				.setWidth(20)
				.setStyle(broj)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("ukupnoKm", Float.class)
				.setTitle("пређени пут одометар")
				.setWidth(20)
				.setStyle(broj)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("ukupnoGorivo", Float.class)
				.setTitle("ук. потр. горива")
				.setWidth(20)
				.setStyle(broj)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("prosPotGps", Float.class)
				.setTitle("прос. пот. по гпс")
				.setWidth(20)
				.setStyle(broj)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("prosPotr", Float.class)
				.setTitle("прос потрошња")
				.setWidth(20)
				.setStyle(broj)
				.build());
		setItems(vratilistu(objekti, datumVremeOd, datumVremeDo));
	}
	
	public List<PredjeniPut> vratilistu(ArrayList<Objekti> objekti, Timestamp datumVremeOd, Timestamp datumVremeDo){
		return obracun(objekti, datumVremeOd, datumVremeDo);
	}
	
	public SerializableSupplier<List<? extends PredjeniPut>> vratiSeriju(ArrayList<Objekti> objekti, Timestamp datumVremeOd, Timestamp datumVremeDo){
		SerializableSupplier<List<? extends PredjeniPut>> serija = () -> obracun(objekti, datumVremeOd, datumVremeDo);
		return serija;
	}
	
	private List<PredjeniPut> obracun(ArrayList<Objekti> objekti, Timestamp datumVremeOd, Timestamp datumVremeDo){
		List<PredjeniPut> lista = new ArrayList<PredjeniPut>();
		for(Objekti objekat : objekti) {
			
			ArrayList<Javljanja> javljanja = Servis.javljanjeServis.vratiJavljanjaObjektaOdDoPrvoPoslednje(objekat, datumVremeOd, datumVremeDo);
			ArrayList<Obd> obdLista = Servis.obdServis.nadjiObdPoObjektuOdDoPrvoPoslednje(objekat, datumVremeOd, datumVremeDo);

			PredjeniPut predjeniPut = new PredjeniPut(objekat.getOznaka(), 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
			
			if(javljanja != null && !javljanja.isEmpty()) {
				predjeniPut.setVirtualOdo(javljanja.get(1).getVirtualOdo() - javljanja.get(0).getVirtualOdo());
				if(obdLista != null && !obdLista.isEmpty()) {
					predjeniPut.setUkupnoKm(obdLista.get(1).getUkupnoKm() - obdLista.get(0).getUkupnoKm());
					predjeniPut.setUkupnoGorivo(obdLista.get(1).getUkupnoGorivo() - obdLista.get(0).getUkupnoGorivo());
					predjeniPut.setProsPotGps(predjeniPut.getUkupnoGorivo()/(predjeniPut.getVirtualOdo()/100));
					if(predjeniPut.getUkupnoKm() != 0.0f) {
						predjeniPut.setProsPotr(predjeniPut.getUkupnoGorivo()/(predjeniPut.getUkupnoKm()/100));
					}
				}
			}
			lista.add(predjeniPut);
		}
		return lista;
	}
}
