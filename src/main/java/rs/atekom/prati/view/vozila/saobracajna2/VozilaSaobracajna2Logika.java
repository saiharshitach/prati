package rs.atekom.prati.view.vozila.saobracajna2;

import com.vaadin.server.Page;

import rs.atekom.prati.Prati;
import rs.atekom.prati.view.LogikaInterface;

public class VozilaSaobracajna2Logika implements LogikaInterface{

	public VozilaSaobracajna2View view;
	
	public VozilaSaobracajna2Logika(VozilaSaobracajna2View saobracajna2View) {
		view = saobracajna2View;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noviPodatak() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ukloniPodatak() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void redIzabran(Object podatak) {
		// TODO Auto-generated method stub
		
	}

}
