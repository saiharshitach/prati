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

public class StajanjeLayout extends VerticalLayout{

	private static final long serialVersionUID = 1L;
	private HorizontalLayout hLayout;
	private static final String DATUMVREME = "dd/MM/yyyy HH:mm:ss";

	public StajanjeLayout(ArrayList<Objekti> objekti, Timestamp vremeOd, Timestamp vremeDo, int duzina) {
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
		StajanjeIzvestaj izvestaj = new StajanjeIzvestaj(objekti, vremeOd, vremeDo, duzina);
		izvestaj.downloadPdfOnClick(pdf, "stajanje_" + datumVreme.format(new Date()) + ".pdf", izvestaj.vratiSeriju(objekti, vremeOd, vremeDo, duzina));
		//izvestaj.downloadDocxOnClick(doc, "predjeni_put_"" + datumVreme.format(new Date()) + ".doc", izvestaj.vratiSeriju(objekat, datumVremeOd, datumVremeDo));
		//izvestaj.downloadXlsOnClick(xls, "predjeni_put_"" + datumVreme.format(new Date()) + ".xlsx", izvestaj.vratiSeriju(objekat, datumVremeOd, datumVremeDo));
		panel.setContent(izvestaj);
		addComponentsAndExpand(panel);
	}
	
	public HorizontalLayout vratiPreuzimanje() {
		return hLayout;
	}
}
