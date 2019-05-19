package rs.cybertrade.prati.view.zone;

import com.vaadin.server.Page;
import pratiBaza.tabele.Zone;
import rs.cybertrade.prati.Prati;
import rs.cybertrade.prati.server.Servis;
import rs.cybertrade.prati.view.LogikaInterface;

public class ZoneLogika implements LogikaInterface{

	public ZoneView view;
	
	public ZoneLogika(ZoneView zoneView) {
		view = zoneView;
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
				Zone zona = Servis.zonaServis.nadjiZonuPoId(id);
				view.izaberiRed(zona);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		Zone zona = (Zone)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(zona.getId() != null) {
			Servis.zonaServis.izmeniZonu(zona);
			view.pokaziPorukuUspesno("зона измењена");
		}else {
			try {
				Servis.zonaServis.unesiZonu(zona);
				view.pokaziPorukuUspesno("зона сачувана");
			}catch (Exception e) {
				view.pokaziPorukuGreska("сим са унетим подацима већ постоји!");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		Zone zona = (Zone)podatak;
		if(zona == null){
			setFragmentParametar("");
		}else {
			setFragmentParametar(zona.getId() + "");
		}
		view.izmeniPodatak(zona);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new Zone());
	}

	@Override
	public void ukloniPodatak() {
		view.ukloniPodatak();
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		view.updateTable();
		setFragmentParametar("");
	}

	@Override
	public void redIzabran(Object podatak) {
		view.izmeniPodatak((Zone)podatak);
	}

}
