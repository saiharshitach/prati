package rs.cybertrade.prati.view.alarmi;

import com.vaadin.server.Page;
import pratiBaza.tabele.SistemAlarmi;
import rs.cybertrade.prati.Prati;
import rs.cybertrade.prati.server.Servis;
import rs.cybertrade.prati.view.LogikaInterface;

public class AlarmiLogika implements LogikaInterface{

	public AlarmiView view;
	
	public AlarmiLogika(AlarmiView alarmiView) {
		view = alarmiView;
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
				SistemAlarmi alarm = Servis.sistemAlarmServis.nadjiAlaramPoId(id);
				view.izaberiRed(alarm);
			}catch (NumberFormatException e) {

			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		SistemAlarmi alarm = (SistemAlarmi)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(alarm.getId() != null) {
			Servis.sistemAlarmServis.azurirajAlarme(alarm);
			view.pokaziPorukuUspesno("аларм измењен");
		}else {
			Servis.sistemAlarmServis.unesiAlarme(alarm);
			view.pokaziPorukuUspesno("аларм сачуван");
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		SistemAlarmi alarm = (SistemAlarmi)podatak;
		if(alarm == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(alarm.getId() + "");
		}
		view.izmeniPodatak(alarm);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new SistemAlarmi());
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
		view.izmeniPodatak((SistemAlarmi)podatak);
	}

}
