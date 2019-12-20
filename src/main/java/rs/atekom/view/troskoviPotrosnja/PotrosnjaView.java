package rs.atekom.view.troskoviPotrosnja;

import com.github.appreciated.app.layout.annotations.MenuCaption;
import com.github.appreciated.app.layout.annotations.MenuIcon;
import com.github.appreciated.app.layout.annotations.NavigatorViewName;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

@NavigatorViewName("potrosnja")
@MenuCaption("Потрошња")
@MenuIcon(VaadinIcons.DROP)
public class PotrosnjaView extends PotrosnjaOpstiView implements View{

	private static final long serialVersionUID = 1L;
	
	public PotrosnjaView() {
		super(true, null);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		super.viewLogika.enter(event.getParameters());
	}
}
