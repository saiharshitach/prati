package rs.atekom.prati.view.vozila.saobracajna2;

import com.vaadin.server.Page;

import pratiBaza.tabele.VozilaSaobracajne;
import pratiBaza.tabele.VozilaSaobracajne2;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
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
		if(objectId != null && !objectId.isEmpty()) {
			if(objectId.equals("new")) {
				noviPodatak();
			}
		}else {
			try {
				int id = Integer.parseInt(objectId);
				VozilaSaobracajne2 saobracajna2 = Servis.saobracajna2Servis.nadjiSaobracajnu2PoId(id);
				view.izaberiRed(saobracajna2);
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
		VozilaSaobracajne2 saobracajna2 = (VozilaSaobracajne2)podatak;
		VozilaSaobracajne saobracajna = saobracajna2.getSaobracajna();
		if(saobracajna2.getId() != null) {
			Servis.saobracajna2Servis.izmeniSaobracajnu2(saobracajna2);
			view.pokaziPorukuUspesno("подаци саобраћајне2 измењени");
		}else {
			try {
				Servis.saobracajna2Servis.unesiSaobracajnu2(saobracajna2);
				if(saobracajna != null) {
					saobracajna.setSaobracajna2(saobracajna2);
					Servis.saobracajnaServis.izmeniSaobracajnu(saobracajna);
				}
				view.pokaziPorukuUspesno("подаци саобраћајне2 сачувани");
			}catch (Exception e) {
				view.pokaziPorukuGreska("подаци саобраћајне2 због грешке нису сачувани!");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		VozilaSaobracajne2 saobracajna2 = (VozilaSaobracajne2)podatak;
		if(saobracajna2 == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(saobracajna2.getId() + "");
		}
		view.izmeniPodatak(saobracajna2);
	}

	@Override
	public void noviPodatak() {
		setFragmentParametar("new");
		view.ocistiIzbor();
		view.izmeniPodatak(new VozilaSaobracajne2());
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
		view.izmeniPodatak((VozilaSaobracajne2)podatak);
	}

}
