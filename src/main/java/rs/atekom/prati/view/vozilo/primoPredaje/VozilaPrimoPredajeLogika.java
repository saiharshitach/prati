package rs.atekom.prati.view.vozilo.primoPredaje;

import java.util.ArrayList;
import java.util.List;
import com.vaadin.server.Page;

import pratiBaza.pomocne.StavkaPrijema;
import pratiBaza.tabele.VozilaOpremaPrijem;
import pratiBaza.tabele.VozilaPrimoPredaje;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class VozilaPrimoPredajeLogika implements LogikaInterface{

	public VozilaPrimoPredajeView view;
	public List<StavkaPrijema> opremaStavke;
	
	public VozilaPrimoPredajeLogika(VozilaPrimoPredajeView voziloPrimoPredajaView) {
		view = voziloPrimoPredajaView;
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
				VozilaPrimoPredaje primoPredaja = Servis.primoPredajaServis.nadjiVoziloPrimoPredajaPoId(id);
				view.izaberiRed(primoPredaja);
			}catch (Exception e) {
				
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		VozilaPrimoPredaje primoPredaja = (VozilaPrimoPredaje)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(primoPredaja.getId() != null) {
			Servis.primoPredajaServis.izmeniVoziloPrimoPredaja(primoPredaja);
			Servis.opremaPrijemServis.izbrisiSvaVoziloOpremaPredaja(primoPredaja);
			ArrayList<VozilaOpremaPrijem> stavkePrijema = new ArrayList<VozilaOpremaPrijem>();
			for(StavkaPrijema prijem : opremaStavke){
				VozilaOpremaPrijem opremaPrijem = new VozilaOpremaPrijem();
				opremaPrijem.setSistemPretplatnici(primoPredaja.getSistemPretplatnici());
				opremaPrijem.setOrganizacija(primoPredaja.getOrganizacija());
				opremaPrijem.setPrimoPredaja(primoPredaja);
				opremaPrijem.setOprema(prijem.getOprema());
				opremaPrijem.setKolicina(prijem.getKolicina());
				stavkePrijema.add(opremaPrijem);
			}
			if(!stavkePrijema.isEmpty()) {
				Servis.opremaPrijemServis.unesiSvaVoziloOpremaPredaja(stavkePrijema);
			}
			view.pokaziPorukuUspesno("подаци за примопредају измењени");
		}else {
			try {
				Servis.primoPredajaServis.unesiVoziloPrimoPredaja(primoPredaja);
				ArrayList<VozilaOpremaPrijem> stavkePrijema = new ArrayList<VozilaOpremaPrijem>();
				for(StavkaPrijema prijem : opremaStavke){
					VozilaOpremaPrijem opremaPrijem = new VozilaOpremaPrijem();
					opremaPrijem.setSistemPretplatnici(primoPredaja.getSistemPretplatnici());
					opremaPrijem.setOrganizacija(primoPredaja.getOrganizacija());
					opremaPrijem.setPrimoPredaja(primoPredaja);
					opremaPrijem.setOprema(prijem.getOprema());
					opremaPrijem.setKolicina(prijem.getKolicina());
					stavkePrijema.add(opremaPrijem);
				}
				if(!stavkePrijema.isEmpty()) {
					Servis.opremaPrijemServis.unesiSvaVoziloOpremaPredaja(stavkePrijema);
				}
				view.pokaziPorukuUspesno("подаци за примопредају сачувани");
			}catch (Exception e) {
				view.pokaziPorukuGreska("грешка, контактирајте администратора");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		VozilaPrimoPredaje primoPredaja = (VozilaPrimoPredaje)podatak;
		if(primoPredaja == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(primoPredaja.getId() + "");
		}
		view.izmeniPodatak(primoPredaja);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new VozilaPrimoPredaje());
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
		view.izmeniPodatak((VozilaPrimoPredaje)podatak);
	}

}
