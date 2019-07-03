package rs.atekom.prati;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

import rs.atekom.prati.meni.PratiEventBus;
import rs.atekom.prati.meni.PratiEvent.KorisnikLoginRequestedEvent;

public class PrijavaView extends VerticalLayout{

	private static final long serialVersionUID = 1L;

	public PrijavaView() {
		setSizeFull();
		Component prijavaForma = buildPrijavaForma();
		addComponent(prijavaForma);
		setComponentAlignment(prijavaForma, Alignment.MIDDLE_CENTER);
	}
	
	private Component buildPrijavaForma(){
		final VerticalLayout prijavaPanel = new VerticalLayout();
		prijavaPanel.setSizeUndefined();
		prijavaPanel.setSpacing(true);
		Responsive.makeResponsive(prijavaPanel);
		prijavaPanel.addStyleName("login-panel");
		
		prijavaPanel.addComponent(buildLabele());
		prijavaPanel.addComponent(buildFields());
		return prijavaPanel;
	}
	
	private Component buildFields(){
		HorizontalLayout polja = new HorizontalLayout();
		polja.setSpacing(true);
		polja.addStyleName("fields");
		
		final TextField korisnickoIme = new TextField("Корисничко име");
		korisnickoIme.setMaxLength(30);
		korisnickoIme.setIcon(VaadinIcons.USER);
		korisnickoIme.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
		korisnickoIme.focus();
		
		final PasswordField lozinka = new PasswordField("Лозинка");
		lozinka.setMaxLength(30);
		lozinka.setIcon(VaadinIcons.LOCK);
		lozinka.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
		
		final Button prijava = new Button("Пријава");
		prijava.addStyleName(ValoTheme.BUTTON_PRIMARY);
		prijava.setClickShortcut(KeyCode.ENTER);
		
		polja.addComponents(korisnickoIme, lozinka, prijava);
		polja.setComponentAlignment(prijava, Alignment.BOTTOM_LEFT);
		
		prijava.addClickListener(new ClickListener(){
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				if(!(korisnickoIme.getValue() == null) && !(lozinka.getValue() == null ))
					PratiEventBus.post(new KorisnikLoginRequestedEvent(korisnickoIme.getValue(), lozinka.getValue()));	
				}
			});
		return polja;
	}
	
	private Component buildLabele(){
		CssLayout labele = new CssLayout();
		labele.addStyleName("labels");
		
		Label dobroDosli = new Label("Добро дошли!");
		dobroDosli.setSizeUndefined();
		dobroDosli.addStyleName(ValoTheme.LABEL_H4);
		dobroDosli.addStyleName(ValoTheme.LABEL_COLORED);
		labele.addComponent(dobroDosli);
		
		Label naslov = new Label("Праћење");
		naslov.setSizeUndefined();
		naslov.addStyleName(ValoTheme.LABEL_H3);
		naslov.addStyleName(ValoTheme.LABEL_LIGHT);
		labele.addComponent(naslov);
		return labele;
	}
}
