package rs.cybertrade.prati;

import javax.servlet.annotation.WebServlet;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Viewport;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import pratiBaza.tabele.Javljanja;
import rs.cybertrade.prati.Broadcaster.BroadcastListener;

@Viewport("user-scalable=no,initial-scale=1.0")
@Theme("mytheme")
@PreserveOnRefresh // zadržava poziciju kada se uradi refresh strane
@Push
public class Prati extends UI implements BroadcastListener{

	private static final long serialVersionUID = 1L;

	@Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        
        final TextField name = new TextField();
        name.setCaption("Type your name here:");

        Button button = new Button("Click Me");
        button.addClickListener(e -> {
            layout.addComponent(new Label("Thanks " + name.getValue() 
                    + ", it works!"));
        });
        layout.addComponent(new Label(Servis.korisnikServis.nadjiKorisnikaPoKorisnickom("goran@cybertrade.rs", "mostar").getIme()));
        layout.addComponents(name, button);
        
        setContent(layout);
    }

    @WebServlet(urlPatterns = "/*", name = "PratiServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = Prati.class, productionMode = true)
    public static class PratiServlet extends VaadinServlet {
		private static final long serialVersionUID = 1L;
    }

	@Override
	public void receiveBroadcast(Javljanja message) {
		// TODO Auto-generated method stub
		
	}
}
