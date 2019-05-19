package rs.cybertrade.prati.view;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.VerticalLayout;

import rs.cybertrade.prati.Prati;
import rs.cybertrade.prati.view.komponente.Celobrojni;
import rs.cybertrade.prati.view.komponente.Decimalni;
import rs.cybertrade.prati.view.komponente.Tekst;

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
	
	public OpstaForma() {
		addStyleName("product-form");
		addStyleName("product-form-wrapper");
		decimalFormatSymbols = new DecimalFormatSymbols();
		
		decimalFormatSymbols.setDecimalSeparator(',');
		decimalFormatSymbols.setGroupingSeparator('.');
		decimalFormat = new DecimalFormat("#,##0.00", decimalFormatSymbols);
		
		layout = new VerticalLayout();
		layout.setHeight("100%");
		if(!Prati.getCurrent().sirina()) {
			layout.setWidth("100%");
		}
		layout.setSpacing(true);
		
		expander = new CssLayout();
		expander.addStyleName("expander");
		
		sacuvaj = new Button("сачувај");
		sacuvaj.addStyleName("primary");
		
		otkazi = new Button("откажи");
		otkazi.addStyleName("cancel");
		otkazi.setClickShortcut(KeyCode.ESCAPE);
		
		izbrisi = new Button("избриши");
		izbrisi.addStyleName("danger");
	}
	
	public Double parsirajDecimalni(String vrednost) {
		String iznosStr1 = vrednost.replace(".", "");
		String iznosStr2 = iznosStr1.replace(",", ".");
		return Double.parseDouble(iznosStr2);
	}
	
	public Date dateDatum(LocalDate datum) {
		return Date.from(datum.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	
	public LocalDate localDatum(Date datum) {
		return (new Date(datum.getTime())).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
}
