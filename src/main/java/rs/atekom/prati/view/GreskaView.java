package rs.atekom.prati.view;

import com.github.appreciated.app.layout.annotations.MenuCaption;
import com.github.appreciated.app.layout.annotations.MenuIcon;
import com.github.appreciated.app.layout.annotations.NavigatorViewName;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Label;

@NavigatorViewName("greska") // an empty view name will also be the default view
@MenuCaption("Грешка")
@MenuIcon(VaadinIcons.ERASER)
public class GreskaView extends OpstiView{

	private static final long serialVersionUID = 1L;

	public GreskaView() {
		Label obavestenje = new Label("Појавила се грешка, обратите се администратору!");
		addComponent(obavestenje);
	}
}
