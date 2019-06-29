package rs.cybertrade.prati.view.korisnici;

import java.util.ArrayList;
import java.util.Set;
import com.vaadin.server.Page;
import pratiBaza.tabele.Grupe;
import pratiBaza.tabele.GrupeKorisnici;
import pratiBaza.tabele.Korisnici;
import rs.cybertrade.prati.Prati;
import rs.cybertrade.prati.server.Servis;
import rs.cybertrade.prati.view.LogikaInterface;

public class KorisniciLogika implements LogikaInterface{

	public KorisniciView view;
	public Set<Grupe> grupe;
	
	public KorisniciLogika(KorisniciView korisniciView) {
		view = korisniciView;
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
				Korisnici korisnik = Servis.korisnikServis.nadjiKorisnikaPoId(id);
				view.izaberiRed(korisnik);
			}catch (Exception e) {
				
			}
		}
	}

	@Override
	public void sacuvajPodatak(Object podatak) {
		Korisnici korisnik = (Korisnici)podatak;
		view.ocistiIzbor();
		view.izmeniPodatak(null);
		setFragmentParametar("");
		if(korisnik.getId() != null) {
			Servis.korisnikServis.azurirajKorisnika(korisnik);
			sacuvajGrupeKorisnik(korisnik);
			view.pokaziPorukuUspesno("корисник измењен");
		}else {
			try {
				Servis.korisnikServis.unesiKorisnika(korisnik);
				sacuvajGrupeKorisnik(korisnik);
				view.pokaziPorukuUspesno("корисник сачуван");
			}catch (Exception e) {
				view.pokaziPorukuGreska("корисник са унетом адресом е-поште већ постоји!");
			}
		}
		view.updateTable();
	}

	@Override
	public void izmeniPodatak(Object podatak) {
		Korisnici korisnik = (Korisnici)podatak;
		if(korisnik == null) {
			setFragmentParametar("");
		}else {
			setFragmentParametar(korisnik.getId() + "");
		}
		view.izmeniPodatak(korisnik);
	}

	@Override
	public void noviPodatak() {
		view.ocistiIzbor();
		setFragmentParametar("new");
		view.izmeniPodatak(new Korisnici());
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
		view.izmeniPodatak((Korisnici)podatak);
	}
	
	private void sacuvajGrupeKorisnik(Korisnici korisnik) {
		ArrayList<GrupeKorisnici> grupeKorisnik = Servis.grupeKorisnikServis.vratiSveGrupeKorisnikPoKorisniku(korisnik);
		for(GrupeKorisnici grKorisnik: grupeKorisnik) {
			Servis.grupeKorisnikServis.izbrisiGrupaZaposleni(grKorisnik);
			}
		for(Grupe grupa : grupe) {
			GrupeKorisnici grKorisnik = new GrupeKorisnici();
			grKorisnik.setSistemPretplatnici(korisnik.getSistemPretplatnici());
			grKorisnik.setOrganizacija(korisnik.getOrganizacija());
			grKorisnik.setKorisnici(korisnik);
			grKorisnik.setGrupe(grupa);
			Servis.grupeKorisnikServis.unesiGrupaZaposleni(grKorisnik);
			}
		}

}
