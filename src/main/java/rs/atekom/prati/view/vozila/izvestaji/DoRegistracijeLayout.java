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

public class DoRegistracijeLayout extends VerticalLayout{

	private static final long serialVersionUID = 1L;
	private HorizontalLayout hLayout;
	private static final String DATUMVREME = "dd/MM/yyyy HH:mm:ss";
	
	public DoRegistracijeLayout(ArrayList<Objekti> objekti, int tipServisa, int doServisa) {
		setSizeFull();
		setMargin(new MarginInfo(false, false, true, false));
		SimpleDateFormat datumVreme = new SimpleDateFormat(DATUMVREME);
		Button pdf = new Button("PDF");
		Button doc = new Button("doc");
		Button xls = new Button("xls");
		//Label prazno = new Label("");
		hLayout = new HorizontalLayout();
		hLayout.setSpacing(true);
		hLayout.setMargin(new MarginInfo(false, false, false, false));
		//hLayout.addComponentsAndExpand(prazno);
		hLayout.addComponent(pdf);
		hLayout.addComponent(doc);
		hLayout.addComponent(xls);
		Panel panel = new Panel();
		panel.setHeight("100%");
		DoRegistracijeIzvestaj izvestaj = new DoRegistracijeIzvestaj(objekti, tipServisa, doServisa);
		izvestaj.downloadPdfOnClick(pdf, "za_registraciju_" + datumVreme.format(new Date()) + ".pdf", izvestaj.vratiSeriju());
		izvestaj.downloadDocxOnClick(doc, "za_registraciju_" + datumVreme.format(new Date()) + ".doc", izvestaj.vratiSeriju());
		izvestaj.downloadXlsOnClick(xls, "za_registraciju_" + datumVreme.format(new Date()) + ".xlsx", izvestaj.vratiSeriju());
		panel.setContent(izvestaj);
		addComponentsAndExpand(panel);
	}
	
	public HorizontalLayout vratiPreuzimanje() {
		return hLayout;
	}
}
