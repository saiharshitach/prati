package rs.atekom.prati.view;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.VerticalLayout;

import pratiBaza.tabele.Korisnici;
import rs.atekom.prati.Prati;
import rs.atekom.prati.view.komponente.Celobrojni;
import rs.atekom.prati.view.komponente.Decimalni;
import rs.atekom.prati.view.komponente.Tekst;
import rs.atekom.prati.view.komponente.combo.ComboOrganizacije;
import rs.atekom.prati.view.komponente.combo.ComboPretplatnici;

public class OpstaForma extends CssLayout{

	private static final long serialVersionUID = 1L;
	public DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
	public DecimalFormat decimalFormat;
	public VerticalLayout layout;
	public CssLayout expander;
	public Button sacuvaj, otkazi, izbrisi, dodajLokaciju;
	public Celobrojni ceo;
	public Decimalni dec;
	public Tekst tekst;
	public DateField datum;
	public Date dat;
	public Korisnici korisnik;
	public ComboPretplatnici pretplatnici;
	public ComboOrganizacije organizacije;
	
	public OpstaForma() {
		pretplatnici = new ComboPretplatnici("претплатник", true, true);
		organizacije = new ComboOrganizacije(pretplatnici.getValue(), "организација", true, false);
		korisnik = (Korisnici) VaadinSession.getCurrent().getAttribute(Korisnici.class.getName());
		
		addStyleName("product-form");
		addStyleName("product-form-wrapper");
		decimalFormatSymbols = new DecimalFormatSymbols();
		
		decimalFormatSymbols.setDecimalSeparator(',');
		decimalFormatSymbols.setGroupingSeparator('.');
		decimalFormat = new DecimalFormat("#,##0.00", decimalFormatSymbols);
		
		layout = new VerticalLayout();
		layout.setSizeUndefined();
		layout.setHeight("100%");
		if(!Prati.getCurrent().sirina()) {
			layout.setWidth("100%");
		}
		layout.setSpacing(true);
		
		expander = new CssLayout();
		expander.addStyleName("expander");
		
		if(isSistem()) {
			layout.addComponent(pretplatnici);
		}
		
		if(isSistem() || isAdmin()) {
			layout.addComponent(organizacije);
		}
		
		sacuvaj = new Button("сачувај");
		sacuvaj.addStyleName("primary");
		
		otkazi = new Button("откажи");
		otkazi.addStyleName("cancel");
		otkazi.setClickShortcut(KeyCode.ESCAPE);
		
		izbrisi = new Button("избриши");
		izbrisi.addStyleName("danger");
	}
	
	public boolean isSistem() {
		return (korisnik.isSistem() && korisnik.getSistemPretplatnici().isSistem());
	}
	
	public boolean isAdmin() {
		return (korisnik.isAdmin() && korisnik.getOrganizacija() == null);
	}
	
	public void ukloniCombo() {
		layout.removeComponent(pretplatnici);
		layout.removeComponent(organizacije);
	}
	
	public void dodajExpanderButton() {
		layout.addComponentsAndExpand(expander);
		layout.addComponent(sacuvaj);
		layout.addComponent(otkazi);
		layout.addComponent(izbrisi);
	}
	
	public Double parsirajDecimalni(String vrednost) {
		String iznosStr1 = vrednost.replace(".", "");
		String iznosStr2 = iznosStr1.replace(",", ".");
		return Double.parseDouble(iznosStr2);
	}
	
	public Date dateDatum(LocalDate datum) {
		return Date.from(datum.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	
	public Date dateTimeDatum(LocalDateTime datumVreme) {
	    return java.util.Date.from(datumVreme.atZone(ZoneId.systemDefault()).toInstant());
	}
	
	public LocalDate localDatum(Date datum) {
		return (new Date(datum.getTime())).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	public LocalDateTime localTimeDatum(Date datum) {
		return datum.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
}
