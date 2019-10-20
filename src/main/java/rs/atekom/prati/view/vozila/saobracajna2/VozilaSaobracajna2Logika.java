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
				VozilaSaobracajne2 saobracajna = Servis.saobracajna2Servis.nadjiSaobracajnu2PoId(id);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		VozilaSaobracajne2 saobracajna = (VozilaSaobracajne2)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(saobracajna.getId() != null) {
			
			view.pokaziPorukuUspesno("подаци саобраћајне 2 измењени");
		}else {
			
		}
		view.updateTable();
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new VozilaSaobracajne());
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
		view.izmeniPodatak((VozilaSaobracajne2)podatak);
	}

}
