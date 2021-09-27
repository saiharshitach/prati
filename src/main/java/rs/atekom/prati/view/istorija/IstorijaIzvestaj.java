package rs.atekom.prati.view.istorija;

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
import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.Obd;
import pratiBaza.tabele.Objekti;
import rs.atekom.prati.server.Servis;

public class IstorijaIzvestaj extends PrintPreviewReport<Javljanja>{

	private static final long serialVersionUID = 1L;
	private static final String DATUMVREME = "dd/MM/yyyy HH:mm:ss";

	public IstorijaIzvestaj(Objekti objekat, Timestamp datumVremeOd, Timestamp datumVremeDo) {
		//setImageServletPathPattern("izvestaj-istorija?image={0}");
		setSizeUndefined();
		String strVremeOd = "од: ";
		String strVremeDo = "до: ";
		int brojBrzina = 0;
		int prosBrzina = 0;
		int maxBrzina = 0;
		float predjeniPutGPS = 0.0f;
		float ukPotrosnja = 0.0f;
		int satPocetak = 0;
		int satKraj = 0;
		int satUkupno = 0;
		float prosPotrosUk = 0.0f;
		int brojProsPotr = 1;
		float prosPotros = 0.0f;

		List<Javljanja> javljanja = Servis.javljanjeServis.vratiJavljanjaObjektaOdDo(objekat, datumVremeOd, datumVremeDo);
		ArrayList<Obd> obd = Servis.obdServis.nadjiObdPoObjektuOdDo(objekat, datumVremeOd, datumVremeDo);

		Date vremeMaxBrzine = new Date();
		for(Javljanja javljanje : javljanja) {
			if(javljanje.getBrzina() > 5) {
				prosBrzina += javljanje.getBrzina();
				brojBrzina++;
			}
			if(javljanje.getBrzina() > maxBrzina) {
				maxBrzina = javljanje.getBrzina();
				vremeMaxBrzine = javljanje.getDatumVreme();
			}
		}
		
		if(brojBrzina == 0) {
			brojBrzina = 1;
		}
		
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		
		strVremeOd += outputFormat.format(javljanja.get(0).getDatumVreme());
		strVremeDo += outputFormat.format(javljanja.get(javljanja.size() - 1).getDatumVreme());
		
		if(javljanja != null &&!javljanja.isEmpty()) {
			predjeniPutGPS = javljanja.get(javljanja.size()-1).getVirtualOdo() - javljanja.get(0).getVirtualOdo();
		}

		if(obd != null && !obd.isEmpty()) {
			ukPotrosnja = obd.get(obd.size() - 1).getUkupnoGorivo() - obd.get(0).getUkupnoGorivo();
			satPocetak = obd.get(0).getUkupnoKm();
			satKraj = obd.get(obd.size() - 1).getUkupnoKm();
			satUkupno = satKraj - satPocetak;
			
			for(Obd obdZapis : obd) {
				if(obdZapis.getProsecnaPotrosnja() > 0.0f) {
					prosPotrosUk += obdZapis.getProsecnaPotrosnja();
					brojProsPotr++;
				}
			}
			prosPotros = prosPotrosUk/brojProsPotr;
		}else {
			satUkupno = 1;
		}
		//setFont(new Font(Font.MEDIUM, "Arial Unicode MS", false));
		//Font slova = new Font(10, Font._FONT_ARIAL, Font._FONT_ARIAL, Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing, true);
		
		SimpleDateFormat datumVreme = new SimpleDateFormat(DATUMVREME);
		Style headerStyle = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM).build();
		headerStyle.setHorizontalAlign(HorizontalAlign.LEFT);
		
		Style datum = new StyleBuilder(true).setPattern(DATUMVREME).setFont(Font.ARIAL_MEDIUM).build();
		datum.setHorizontalAlign(HorizontalAlign.LEFT);
		
		Style footerStyle = new StyleBuilder(true).setFont(Font.ARIAL_MEDIUM_BOLD).build();
		
		getReportBuilder()
		//.setProperty("net.sf.jasperreports.default.pdf.encoding", "Identity-H")
		.setPageSizeAndOrientation(Page.Page_A4_Landscape())
		.setMargins(20, 20, 40, 40)
		.setDefaultEncoding(Font.PDF_ENCODING_Identity_H_Unicode_with_horizontal_writing)
		.setTitle("Преглед историје кретања")
		//.setGrandTotalLegend("ukupno")
		//.addGlobalFooterVariable("osnova", DJCalculation.SUM, headerStyle);
		//.setUseFullPageWidth(true)
		.addAutoText("Преглед података за објекат: " + objekat.getOznaka() + "    " + strVremeOd + "    " + strVremeDo, AutoText.POSITION_HEADER, AutoText.ALIGMENT_LEFT, 450, headerStyle)
		.addAutoText("извештај урађен: " + datumVreme.format(new Date()), AutoText.POSITION_HEADER, AutoText.ALIGNMENT_RIGHT, 300, datum)
		.addAutoText("пређени пут по гпс:  " + String.format("%.2f", predjeniPutGPS) + "км", AutoText.POSITION_HEADER, AutoText.ALIGMENT_LEFT, 800, headerStyle)
		.addAutoText("просечна брзина: " + prosBrzina/brojBrzina + "км/ч;      " + "макс брзина: " + maxBrzina + "км/ч      остварена: " + outputFormat.format(vremeMaxBrzine), 
				AutoText.POSITION_HEADER, AutoText.ALIGMENT_LEFT, 800, headerStyle)
		.addAutoText("одометар - почетак: " + String.format("%,d", satPocetak) + "км;      крај: " + String.format("%,d", satKraj) + "км;      разлика: " + String.format("%,d", satUkupno) + "км", 
				AutoText.POSITION_HEADER, AutoText.ALIGMENT_LEFT, 800, headerStyle)
		.addAutoText("ук. пот. горива: " + ukPotrosnja + " лит;      прос. потр. по гпс: " + String.format("%.2f", ukPotrosnja/(predjeniPutGPS/100)) + "лит/100км;      прос. потр. по одометру: "
				+ String.format("%.2f", ukPotrosnja/((float)satUkupno/100)) + "лит/100км;      потрошња по радном сату: " + String.format("%.2f", prosPotros) + "лит", AutoText.POSITION_HEADER, AutoText.ALIGMENT_LEFT, 800, headerStyle)
		.addAutoText(" ", AutoText.POSITION_HEADER, AutoText.ALIGNMENT_LEFT, 300, datum)
		.addAutoText("Атеком доо               www.atekom.rs                    info@atekom.rs ", AutoText.POSITION_FOOTER, AutoText.ALIGMENT_CENTER, 800, footerStyle)
		.addAutoText(AutoText.AUTOTEXT_PAGE_X, AutoText.POSITION_FOOTER, AutoText.ALIGMENT_RIGHT)
		.setPrintBackgroundOnOddRows(true)
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("datumVreme", Date.class)
				.setTitle("датум и време")
				.setWidth(15)
				.setStyle(datum)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("nazivAlarma", String.class)
				.setTitle("догађај")
				.setWidth(20)
				.setStyle(datum)
				.build())
		.addColumn(ColumnBuilder.getNew()
				.setColumnProperty("eventData", String.class)
				.setTitle("опис")
				.setStyle(datum)
				.build());
		setItems(vratiListu(objekat, datumVremeOd, datumVremeDo));
	}
	
	public List<Javljanja> vratiListu(Objekti objekat, Timestamp datumVremeOd, Timestamp datumVremeDo){
		return Servis.javljanjeServis.vratiJavljanjaObjektaOdDoSaAlarmima(objekat, datumVremeOd, datumVremeDo);
	}
	
	public SerializableSupplier<List<? extends Javljanja>> vratiSeriju(Objekti objekat, Timestamp datumVremeOd, Timestamp datumVremeDo){
		SerializableSupplier<List<? extends Javljanja>> serija = () -> Servis.javljanjeServis.vratiJavljanjaObjektaOdDoSaAlarmima(objekat, datumVremeOd, datumVremeDo);
		return serija;
	}

}
