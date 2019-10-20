package rs.atekom.prati.view.vozila.saobracajna;

import java.util.ArrayList;

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
		VozilaSaobracajne saobracajna = (VozilaSaobracajne)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(saobracajna.getId() != null) {
			Vozila vozilo = Servis.voziloServis.vratiVoziloPoSaobracajnoj(saobracajna);
			if(vozilo != null) {
				if(vozilo.equals(saobracajna.getVozilo())) {
					Servis.saobracajnaServis.izmeniSaobracajnu(saobracajna);
				}else {
					vozilo.setSaobracajna(null);
					Servis.voziloServis.azurirajVozilo(vozilo);
					Servis.saobracajnaServis.izmeniSaobracajnu(saobracajna);
					saobracajna.getVozilo().setSaobracajna(saobracajna);
					Servis.voziloServis.azurirajVozilo(saobracajna.getVozilo());
				}
			}else {
				Servis.saobracajnaServis.izmeniSaobracajnu(saobracajna);
				saobracajna.getVozilo().setSaobracajna(saobracajna);
				Servis.voziloServis.azurirajVozilo(saobracajna.getVozilo());
			}
			view.pokaziPorukuUspesno("подаци саобраћајне измењени");
		}else{
			try {
				ArrayList<VozilaSaobracajne> lista = new ArrayList<VozilaSaobracajne>();
				if(lista.isEmpty() || lista == null) {
					Servis.saobracajnaServis.unesiSaobracajnu(saobracajna);
					saobracajna.getVozilo().setSaobracajna(saobracajna);
					Servis.voziloServis.azurirajVozilo(saobracajna.getVozilo());
					view.pokaziPorukuUspesno("подаци саобраћајне сачувани");
				}else {
					boolean unet = false;
					for(VozilaSaobracajne saob: lista) {
						if(saob.getDatumIzdavanja().after(saobracajna.getDatumIzdavanja())) {
							unet = true;
						}
					}
					if(!unet) {
						Servis.saobracajnaServis.unesiSaobracajnu(saobracajna);
						saobracajna.getVozilo().setSaobracajna(saobracajna);
						Servis.voziloServis.azurirajVozilo(saobracajna.getVozilo());
						view.pokaziPorukuUspesno("подаци саобраћајне сачувани");
					}else {
						view.pokaziPorukuGreska("подаци за о саобраћајнон већ унети, проверите унос броја и датума");
					}
				}
			}catch (Exception e) {
				view.pokaziPorukuGreska("подаци saobraćajne због грешке нису сачувани!");
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
		view.izmeniPodatak((VozilaSaobracajne)podatak);
	}

}
