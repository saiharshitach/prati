package rs.atekom.prati.view.alarmiKorisnik;

import com.vaadin.server.Page;
import pratiBaza.tabele.AlarmiKorisnik;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class AlarmKorisnikLogika implements LogikaInterface{

	public AlarmKorisnikView view;
	
	public AlarmKorisnikLogika(AlarmKorisnikView alarmKorsnikView) {
		view = alarmKorsnikView;
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
				AlarmiKorisnik alarmKorisnik = Servis.alarmKorisnikServis.nadjiAlarmKorisnikPoId(id);
				view.izaberiRed(alarmKorisnik);
			}catch (Exception e) {
				
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		AlarmiKorisnik alarmKorisnik = (AlarmiKorisnik)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(alarmKorisnik.getId() != null) {
			Servis.alarmKorisnikServis.azurirajAlarmiKorisnik(alarmKorisnik);
			view.pokaziPorukuUspesno("аларм корисника измењен");
		}else {
			try {
				if(Servis.alarmKorisnikServis.nadjiAlarmePoKorisnikObjekatAlarm(alarmKorisnik.getKorisnik(), alarmKorisnik.getObjekti(), alarmKorisnik.getSistemAlarmi()) == null) {
					Servis.alarmKorisnikServis.unesiAlarmiKorisnik(alarmKorisnik);
					view.pokaziPorukuUspesno("корисник сачуван");
				}else {
					view.pokaziPorukuGreska("комбинација корисника, објекта и аларма већ постоји");
				}
			}catch (Exception e) {
				view.pokaziPorukuGreska("проблем са уносом!");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		AlarmiKorisnik alarmKorisnik = (AlarmiKorisnik)podatak;
		if(alarmKorisnik == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(alarmKorisnik.getId() + "");
		}
		view.izmeniPodatak(alarmKorisnik);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new AlarmiKorisnik());
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
		view.izmeniPodatak((AlarmiKorisnik)podatak);
	}

}
