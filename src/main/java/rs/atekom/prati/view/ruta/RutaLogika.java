package rs.atekom.prati.view.ruta;

import com.vaadin.server.Page;
import rs.atekom.prati.Prati;
import rs.atekom.prati.view.LogikaInterface;

public class RutaLogika implements LogikaInterface{

	public RutaView view;
	
	public RutaLogika(RutaView rutaView) {
		view = rutaView;
	}
	@Override
	public void init() {
		izmeniPodatak(null);
		view.postaviNoviOmoguceno(true);
	}

	@Override
	public void otkaziPodatak() {
		setFragmentParametar("");
		view.ocistiIzbor();
		view.izmeniPodatak(null);
	}

	@Override
	public void setFragmentParametar(String objectId) {
		String fragmentParametar;
		if(objectId == null || objectId.isEmpty()) {
			fragmentParametar = "";
		}else {
			fragmentParametar = objectId;
		}
		Page page = Prati.getCurrent().getPage();
		page.setUriFragment("!" + view.VIEW_NAME + "/" + fragmentParametar, false);
	}

	@Override
	public void enter(String objectId) {
		if(objectId != null && !objectId.isEmpty()) {
			if(objectId.equals("new")) {
				noviPodatak();
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		setFragmentParametar("");
		setFragmentParametar("new");
		view.izmeniPodatak(null);
	}

	@Override
	public void noviPodatak() {
		view.mapa.removeAllComponents();
		setFragmentParametar("new");
		view.izmeniPodatak(new Object());
	}

	@Override
	public void ukloniPodatak() {
		view.ukloniPodatak();
		view.izmeniPodatak(null);
		setFragmentParametar("");
	}

	@Override
	public void redIzabran(Object podatak) {
		// TODO Auto-generated method stub
		
	}

}
