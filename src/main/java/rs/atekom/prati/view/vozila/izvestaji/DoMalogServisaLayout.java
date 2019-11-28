package rs.atekom.prati.view.vozila.izvestaji;

import java.util.ArrayList;
import java.util.Date;

import com.ibm.icu.text.SimpleDateFormat;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import pratiBaza.tabele.Objekti;

public class DoMalogServisaLayout extends VerticalLayout{

	private static final long serialVersionUID = 1L;
	private HorizontalLayout hLayout;
	private static final String DATUMVREME = "dd/MM/yyyy HH:mm:ss";
	
	public DoMalogServisaLayout(ArrayList<Objekti> objekti, int tipServisa, int doServisa) {
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
		DoMalogServisaIzvestaj izvestaj = new DoMalogServisaIzvestaj(objekti, tipServisa, doServisa);
		izvestaj.downloadPdfOnClick(pdf, "za_mali_servis_" + datumVreme.format(new Date()) + ".pdf", izvestaj.vratiSeriju(objekti, tipServisa, doServisa));
		//izvestaj.downloadDocxOnClick(doc, "predjeni_put_"" + datumVreme.format(new Date()) + ".doc", izvestaj.vratiSeriju(objekat, datumVremeOd, datumVremeDo));
		//izvestaj.downloadXlsOnClick(xls, "predjeni_put_"" + datumVreme.format(new Date()) + ".xlsx", izvestaj.vratiSeriju(objekat, datumVremeOd, datumVremeDo));
		panel.setContent(izvestaj);
		addComponentsAndExpand(panel);
	}
	
	public HorizontalLayout vratiPreuzimanje() {
		return hLayout;
	}
}
