package rs.atekom.view.troskoviOdrzavanje;

import java.sql.Date;
import java.sql.Timestamp;

import com.vaadin.server.Page;

import pratiBaza.tabele.Javljanja;
import pratiBaza.tabele.Obd;
import pratiBaza.tabele.Troskovi;
import pratiBaza.tabele.Vozila;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class OdrzavanjaLogika implements LogikaInterface{

	public OdrzavanjaView view;
	
	public OdrzavanjaLogika(OdrzavanjaView odrzavanjeView) {
		view = odrzavanjeView;
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
				Troskovi trosak = Servis.trosakServis.nadjiTrosakPoId(id);
				view.izaberiRed(trosak);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		Troskovi trosak = (Troskovi)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(trosak.getId() != null) {
			Servis.trosakServis.izmeniTrosak(trosak);
			view.pokaziPorukuUspesno("подаци одржавања измењени");
		}else {
			try {
				Vozila vozilo = trosak.getObjekti().getVozilo();
				if(vozilo != null) {
					switch (trosak.getTipServisa()) {
					case 1: if(vozilo.getMaliPoslednjiDatum() == null || trosak.getDatumVreme().after(new Timestamp(vozilo.getMaliPoslednjiDatum().getTime()))) {
						//System.out.println("mali... gps start...");
						vozilo.setMaliPoslednjiDatum(new Date(trosak.getDatumVreme().getTime()));
						Javljanja gpsKm = Servis.javljanjeServis.vratiJavljanjeObjektaDoIliOd(vozilo.getObjekti(), trosak.getDatumVreme(), true);
						if(gpsKm != null) {
							vozilo.setMaliPoslednjiGPSkm(gpsKm.getVirtualOdo());
						}
						//System.out.println("mali... obd start...");
						Obd obdKm = Servis.obdServis.nadjiObdPoslednji(vozilo.getObjekti(), trosak.getDatumVreme());
						if(obdKm != null) {
							vozilo.setMaliPoslednjiOBDkm(obdKm.getUkupnoKm());
							}
						}
					//System.out.println("mali... kraj...");
					Servis.voziloServis.azurirajVozilo(vozilo);
						break;
						
					case 2: if(vozilo.getVelikiPoslednjiDatum() == null || trosak.getDatumVreme().after(new Timestamp(vozilo.getVelikiPoslednjiDatum().getTime()))) {
						vozilo.setVelikiPoslednjiDatum(new Date(trosak.getDatumVreme().getTime()));
						Javljanja gpsKm = Servis.javljanjeServis.vratiJavljanjeObjektaDoIliOd(vozilo.getObjekti(), trosak.getDatumVreme(), true);
						if(gpsKm != null) {
							vozilo.setVelikiPoslednjiGPSkm(gpsKm.getVirtualOdo());
						}
						Obd obdKm = Servis.obdServis.nadjiObdPoslednji(vozilo.getObjekti(), trosak.getDatumVreme());
						if(obdKm != null) {
							vozilo.setVelikiPoslednjiOBDkm(obdKm.getUkupnoKm());
							}
						}
					Servis.voziloServis.azurirajVozilo(vozilo);
						break;

					case 3: if(vozilo.getDatumPoslednjeRegistracije() == null || trosak.getDatumVreme().after(new Timestamp(vozilo.getDatumPoslednjeRegistracije().getTime()))) {
						vozilo.setDatumPoslednjeRegistracije(new Date(trosak.getDatumVreme().getTime()));
						Servis.voziloServis.azurirajVozilo(vozilo);
						}
					
					default:
						break;
					}
				}
				Servis.trosakServis.unesiTrosak(trosak);
				view.pokaziPorukuUspesno("подаци одржавања сачувани");
			}catch (Exception e) {
				view.pokaziPorukuGreska("подаци за одржавање због грешке нису сачувани!");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		Troskovi trosak = (Troskovi)podatak;
		if(trosak == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(trosak.getId() + "");
		}
		view.izmeniPodatak(trosak);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new Troskovi());
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
		view.izmeniPodatak((Troskovi)podatak);
	}

}
