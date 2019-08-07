package rs.atekom.prati.view.vozilo.nalozi;

import com.vaadin.server.Page;

import pratiBaza.tabele.VozilaNalozi;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class VozilaNaloziLogika implements LogikaInterface{
 
	public VozilaNaloziView view;
	
	public VozilaNaloziLogika(VozilaNaloziView voziloNalogView) {
		view = voziloNalogView;
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
				VozilaNalozi nalog = Servis.nalogServis.nadjiVoziloNalog(id);
				view.izaberiRed(nalog);
			}catch (Exception e) {
				
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		VozilaNalozi nalog = (VozilaNalozi)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(nalog.getId() != null) {
			Servis.nalogServis.izmeniVoziloNalog(nalog);
			view.pokaziPorukuUspesno("подаци за налог измењени");
		}else {
			try {
				Servis.nalogServis.unesiVoziloNalog(nalog);
				view.pokaziPorukuUspesno("подаци за налог сачувани");
			}catch (Exception e) {
				view.pokaziPorukuGreska("грешка, контактирајте администратора");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		VozilaNalozi nalog = (VozilaNalozi)podatak;
		if(nalog == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(nalog.getId() + "");
		}
		view.izmeniPodatak(nalog);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new VozilaNalozi());
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
		view.izmeniPodatak((VozilaNalozi)podatak);
	}

}
