package rs.atekom.prati.meni;

import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;


public class GlavniView extends HorizontalLayout{
	
	private static final long serialVersionUID = 1L;
	
	public GlavniView() {
		setSizeFull();
		setStyleName("main-screen");
    	setSpacing(false);
    	ComponentContainer content = new CssLayout();
    	content.addStyleName("valo-content");
        content.setSizeFull();
        
        //addStyleName("mainview");
        addComponent(new PratiMeni());
        addComponent(content);
        setExpandRatio(content, 1);
        new PratiNavigator(content);
	}
}
