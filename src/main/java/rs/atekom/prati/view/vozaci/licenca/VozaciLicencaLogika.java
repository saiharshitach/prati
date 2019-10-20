package rs.atekom.prati.view.vozaci.licenca;

import java.util.ArrayList;

import com.vaadin.server.Page;
import pratiBaza.tabele.VozaciLicence;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.LogikaInterface;

public class VozaciLicencaLogika implements LogikaInterface{

	public VozaciLicencaView view;
	
	public VozaciLicencaLogika(VozaciLicencaView vozaciLicencaView) {
		view = vozaciLicencaView;
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
				VozaciLicence licenca = Servis.licencaServis.nadjiVozacLicencaPoId(id);
				view.izaberiRed(licenca);
			}catch (Exception e) {
				
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		VozaciLicence licenca = (VozaciLicence)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(licenca.getId() != null) {
			Servis.licencaServis.izmeniVozacLicenca(licenca);
			view.pokaziPorukuUspesno("подаци за лиценцу измењени");
		}else {
			try {
				ArrayList<VozaciLicence> lista = Servis.licencaServis.nadjiSveVozacLicencaPoVozacu(licenca.getVozaci());
				if(lista.isEmpty() || lista == null) {
					Servis.licencaServis.unesiVozacLicenca(licenca);
					view.pokaziPorukuUspesno("подаци за лиценцу сачувани");
				}else {
					boolean unet = false;
					for(VozaciLicence lic: lista) {
						if((lic.getBroj() != null && lic.getBroj().equals(licenca.getBroj())) || lic.getVaziDo() == null || lic.getVaziDo().after(licenca.getIzdato())){
							unet = true;
						}
					}
					if(!unet) {
						Servis.licencaServis.unesiVozacLicenca(licenca);
						view.pokaziPorukuUspesno("подаци за лиценцу сачувани");
					}else {
						view.pokaziPorukuGreska("подаци за о лиценци већ унети, проверите унос броја и датума");
					}
				}
			}catch (Exception e) {
				view.pokaziPorukuGreska("грешка, контактирајте администратора");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		VozaciLicence licenca = (VozaciLicence)podatak;
		if(licenca == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(licenca.getId() + "");
		}
		view.izmeniPodatak(licenca);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new VozaciLicence());
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
		view.izmeniPodatak((VozaciLicence)podatak);
	}

}
