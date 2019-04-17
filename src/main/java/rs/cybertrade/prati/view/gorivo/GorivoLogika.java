package rs.cybertrade.prati.view.gorivo;

import com.vaadin.server.Page;

import pratiBaza.tabele.SistemGoriva;
import rs.cybertrade.prati.Prati;
import rs.cybertrade.prati.Servis;
import rs.cybertrade.prati.view.LogikaInterface;

public class GorivoLogika implements LogikaInterface{

	public GorivoView view;
	
	public GorivoLogika(GorivoView gorivoView) {
		view = gorivoView;
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
		}else {
			try {
				int id = Integer.parseInt(objectId);
				SistemGoriva gorivo = Servis.sistemGorivoServis.nadjiGorivoPoId(id);
				view.izaberiRed(gorivo);
			}catch (NumberFormatException e) {

			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		SistemGoriva gorivo = (SistemGoriva)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(gorivo.getId() != null) {
			Servis.sistemGorivoServis.azurirajGorivo(gorivo);
			view.pokaziPorukuUspesno("врста горива измењена");
		}else {
			Servis.sistemGorivoServis.unesiGorivo(gorivo);
			view.pokaziPorukuUspesno("врста горива сачувана");
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		SistemGoriva gorivo = (SistemGoriva)podatak;
		if(gorivo == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(gorivo.getId() + "");
		}
		view.izmeniPodatak(gorivo);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new SistemGoriva());
	}

	@Override
	public void ukloniPodatak() {
		view.ukloniPodatak();
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
	}

	@Override
	public void redIzabran(Object podatak) {
		view.izmeniPodatak((SistemGoriva)podatak);
	}

}
