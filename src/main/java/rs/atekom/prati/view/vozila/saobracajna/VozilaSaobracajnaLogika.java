package rs.atekom.prati.view.vozila.saobracajna;

import com.vaadin.server.Page;
import pratiBaza.tabele.Vozila;
import pratiBaza.tabele.VozilaSaobracajne;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class VozilaSaobracajnaLogika implements LogikaInterface{

	public VozilaSaobracajnaView view;
	
	public VozilaSaobracajnaLogika(VozilaSaobracajnaView saobracajnaView) {
		view = saobracajnaView;
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
				VozilaSaobracajne saobracajna = Servis.saobracajnaServis.nadjiSaobracajnuPoId(id);
				view.izaberiRed(saobracajna);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		setFragmentParametar("");
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		VozilaSaobracajne saobracajna = (VozilaSaobracajne)podatak;
		Vozila vozilo = saobracajna.getVozilo();
		if(saobracajna.getId() != null) {
			Servis.saobracajnaServis.izmeniSaobracajnu(saobracajna);
			view.pokaziPorukuUspesno("подаци саобраћајне измењени");
		}else{
			try {				
				Servis.saobracajnaServis.unesiSaobracajnu(saobracajna);
				if(vozilo != null) {
					vozilo.setSaobracajna(saobracajna);
					Servis.voziloServis.azurirajVozilo(vozilo);
					}
				view.pokaziPorukuUspesno("подаци саобраћајне сачувани");
			}catch (Exception e) {
				view.pokaziPorukuGreska("подаци саобраћајне због грешке нису сачувани!");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		VozilaSaobracajne saobracajna = (VozilaSaobracajne)podatak;
		if(saobracajna == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(saobracajna.getId() + "");
		}
		view.izmeniPodatak(saobracajna);
	}

	@Override
	public void noviPodatak() {
		setFragmentParametar("new");
		view.ocistiIzbor();
		view.izmeniPodatak(new VozilaSaobracajne());
	}

	@Override
	public void ukloniPodatak() {
		setFragmentParametar("");
		view.ukloniPodatak();
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		view.updateTable();
	}

	@Override
	public void redIzabran(Object podatak) {
		view.izmeniPodatak((VozilaSaobracajne)podatak);
	}

}
