package rs.atekom.prati.view.izvestaji;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import com.ibm.icu.text.SimpleDateFormat;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import pratiBaza.tabele.Objekti;
import pratiBaza.tabele.SistemAlarmi;


public class ZoneLayout extends VerticalLayout{

	private static final long serialVersionUID = 1L;
	private HorizontalLayout hLayout;
	private static final String DATUMVREME = "dd/MM/yyyy HH:mm:ss";
	
	public ZoneLayout(ArrayList<Objekti> objekti, Timestamp datumVremeOd, Timestamp datumVremeDo, ArrayList<SistemAlarmi> alarmi) {
		setSizeFull();
		setMargin(new MarginInfo(false, false, true, false));
		SimpleDateFormat datumVreme = new SimpleDateFormat(DATUMVREME);
		Button pdf = new Button("PDF");
		//Button doc = new Button("doc");
		//Button xls = new Button("xls");
		//Label prazno = new Label("");
		hLayout = new HorizontalLayout();
		hLayout.setSpacing(true);
		hLayout.setMargin(new MarginInfo(false, false, false, false));
		//hLayout.addComponentsAndExpand(prazno);
		hLayout.addComponent(pdf);
		//hLayout.addComponent(doc);
		//hLayout.addComponent(xls);
		Panel panel = new Panel();
		panel.setHeight("100%");
		ZoneIzvestaj izvestaj = new ZoneIzvestaj(objekti, datumVremeOd, datumVremeDo, alarmi);
		izvestaj.downloadPdfOnClick(pdf, "zone_" + datumVreme.format(new Date()) + ".pdf", izvestaj.vratiSeriju(objekti, datumVremeOd, datumVremeDo, alarmi));
		//izvestaj.downloadDocxOnClick(doc, "predjeni_put_"" + datumVreme.format(new Date()) + ".doc", izvestaj.vratiSeriju(objekat, datumVremeOd, datumVremeDo));
		//izvestaj.downloadXlsOnClick(xls, "predjeni_put_"" + datumVreme.format(new Date()) + ".xlsx", izvestaj.vratiSeriju(objekat, datumVremeOd, datumVremeDo));
		panel.setContent(izvestaj);
		addComponentsAndExpand(panel);
	}
	
	public HorizontalLayout vratiPreuzimanje() {
		return hLayout;
	}
}
