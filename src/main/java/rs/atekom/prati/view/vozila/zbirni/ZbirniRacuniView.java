package rs.atekom.prati.view.vozila.zbirni;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import org.vaadin.dialogs.ConfirmDialog;
import com.github.appreciated.app.layout.annotations.MenuCaption;
import com.github.appreciated.app.layout.annotations.MenuIcon;
import com.github.appreciated.app.layout.annotations.NavigatorViewName;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.themes.ValoTheme;
import pratiBaza.tabele.Korisnici;
import pratiBaza.tabele.Racuni;
import pratiBaza.tabele.RacuniRaspodela;
import pratiBaza.tabele.SistemPretplatnici;
import pratiBaza.tabele.Troskovi;
import rs.atekom.prati.Prati;
import rs.atekom.prati.server.Servis;
import rs.atekom.prati.view.komponente.Celobrojni;
import rs.atekom.prati.view.komponente.ComboGorivo;
import rs.atekom.prati.view.komponente.ComboObjektiSaVozilima;
import rs.atekom.prati.view.komponente.ComboOrganizacije;
import rs.atekom.prati.view.komponente.ComboPartneri;
import rs.atekom.prati.view.komponente.ComboPretplatnici;
import rs.atekom.prati.view.komponente.Datum;
import rs.atekom.prati.view.komponente.Decimalni;
import rs.atekom.prati.view.komponente.Filter;
import rs.atekom.prati.view.komponente.Horizontalni;
import rs.atekom.prati.view.komponente.Paneli;
import rs.atekom.prati.view.komponente.Tekst;

@NavigatorViewName("zbirni")
@MenuCaption("Збирни")
@MenuIcon(VaadinIcons.WALLET)
public class ZbirniRacuniView extends Panel implements View, Serializable{

	private static final long serialVersionUID = 1L;
	private static final String DANFORMAT = "%1$td/%1$tm/%1$tY";
	private static final String DANSATFORMAT = "%1$td/%1$tm/%1$tY %1$tH:%1$tM:%1$tS";
	private Korisnici korisnik;
	private Racuni izabraniRacun;
	private Paneli racuniPanel, stavkePanel, raspodelaPanel;
	private Horizontalni racuniHorizontal, stavkeHorizontal, raspodelaHorizontal;
	private Filter racuniFilter, stavkeFilter;
	private Grid<Racuni> racuni;
	private Grid<Troskovi> troskovi;
	private Grid<RacuniRaspodela> raspodela;
	private Button dodajRacun, dodajStavku, dodajRaspodelu, izbrisiRacun, izbrisiStavku, izbrisiRaspodelu, sacuvajRacun, sacuvajStavku, sacuvajRaspodelu, upload;
	private VerticalLayout root, ucitavanjeSadrzaj;
	private ArrayList<Racuni> pocetnoRacuni, listaRacuni;
	private ArrayList<Troskovi> pocetnoStavke, listaStavke;
	private ArrayList<RacuniRaspodela> pocetnoRaspodela, listaRaspodela;
	private ListDataProvider<Racuni> dataProviderRacuni;
	private SerializablePredicate<Racuni> filterPredicateRacuni;
	private ListDataProvider<Troskovi> dataProviderTroskovi;
	private SerializablePredicate<Troskovi> filterPredicateTroskovi;
	private ComboPretplatnici pretplatnici;
	private ComboOrganizacije organizacije;
	private ComboPartneri partneri, partneriRaspodela;
	private Datum datum;
	private Tekst brojRacuna, opis;
	private ComboObjektiSaVozilima objekti;
	private ComboGorivo gorivo;
	private Decimalni cena, pdvIznos, ukupno, kolicina, iznosRaspodele;
	private Celobrojni pdvProcenat;
	private Troskovi izabranaStavka;
	private RacuniRaspodela izabranaRaspodela;
	private Window ucitavanje;
	private ComboBox<String> vrsteUcitavanja;
	private Upload up;

	public ZbirniRacuniView() {
		setSizeFull();
		addStyleName("crud-view");
		addStyleName(ValoTheme.PANEL_BORDERLESS);
        setWidth("100%");
        Prati.getCurrent().pracenjeView = null;
        korisnik = (Korisnici) VaadinSession.getCurrent().getAttribute(Korisnici.class.getName());

        postaviPanele();
        postaviTabeluRacuna();
        postaviTabeluTroskova();
        postaviTabeluRaspodela();
        
        root = new VerticalLayout();
        root.addComponent(racuniPanel);
        root.addComponent(racuni);
        root.addComponent(stavkePanel);
        root.addComponent(troskovi);
        root.addComponent(raspodelaPanel);
        root.addComponent(raspodela);
        
        setContent(root);
	}
	
	private void postaviPanele() {
		postaviElemente();
		racuniPanel = new Paneli();
		racuniPanel.setContent(racuniHorizontal);
		
		stavkePanel = new Paneli();
		stavkePanel.setContent(stavkeHorizontal);
		
		raspodelaPanel = new Paneli();
		raspodelaPanel.setContent(raspodelaHorizontal);
	}
	
	private void postaviElemente() {
		racuniHorizontal = new Horizontalni();
		racuniFilter = new Filter("рачуни...");
		pretplatnici = new ComboPretplatnici(null, false, false);
		pretplatnici.setValue(korisnik.getSistemPretplatnici());
		pretplatnici.addValueChangeListener(new ValueChangeListener<SistemPretplatnici>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent<SistemPretplatnici> event) {
				organizacije.setItems(Servis.organizacijaServis.nadjiSveOrganizacije(event.getValue(), true));
				partneri.setItems(Servis.partnerServis.nadjiSvePartnerePoPretplatniku(event.getValue(), true));
				updateTableRacuni();
			}
		});
		organizacije = new ComboOrganizacije(pretplatnici.getValue(), null, true, false);
		if(korisnik.getOrganizacija() != null) {
        	organizacije.setValue(korisnik.getOrganizacija());
        }
		partneri = new ComboPartneri(korisnik, null, true, false);
		datum = new Datum(null, false);
		datum.setPlaceholder("датум...");
		brojRacuna = new Tekst(null, false);
		brojRacuna.setPlaceholder("број рачуна...");
		opis = new Tekst(null, false);
		opis.setPlaceholder("опис...");
		
		dodajRacun = new Button();
		dodajRacun.addStyleName(ValoTheme.BUTTON_PRIMARY);
		dodajRacun.setIcon(VaadinIcons.PLUS_CIRCLE);
		dodajRacun.setDescription("додај нови рачун");
		dodajRacun.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				izabraniRacun = null;
				ocistiRacun();
				racuni.deselectAll();
			}
		});
		sacuvajRacun = new Button();
		sacuvajRacun.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		sacuvajRacun.setIcon(VaadinIcons.CHECK_CIRCLE);
		sacuvajRacun.setDescription("сачувај рачун");
		sacuvajRacun.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				if(partneri.getValue() == null || datum.getValue() == null || brojRacuna.getValue() == null || brojRacuna.getValue() == "" || brojRacuna.isEmpty()) {
					pokaziPorukuGreska("поља партнер, датум и број рачуна морају бити попуњена!");
				}else {
					ConfirmDialog.show(getUI(), "Провера", "Сачувај унете податке?", "да", "не", new ConfirmDialog.Listener() {
					private static final long serialVersionUID = 1L;
					@Override
					public void onClose(ConfirmDialog dialog) {
						if(dialog.isConfirmed()) {
							if(izabraniRacun != null) {
								izabraniRacun.setDatum(dateDatum(datum.getValue()));
								izabraniRacun.setPartner(partneri.getValue());
								izabraniRacun.setBrojRacuna(brojRacuna.getValue());
								izabraniRacun.setOpis(opis.getValue());
								izabraniRacun.setUradio(korisnik);
								Servis.racunServis.izmeniRacun(izabraniRacun);
								izabraniRacun = null;
								pokaziPorukuUspesno("подаци измењени");
								}else {
									Racuni racun = new Racuni();
									racun.setSistemPretplatnici(pretplatnici.getValue());
									racun.setOrganizacija(organizacije.getValue());
									racun.setDatum(dateDatum(datum.getValue()));
									racun.setPartner(partneri.getValue());
									racun.setBrojRacuna(brojRacuna.getValue());
									racun.setOpis(opis.getValue());
									racun.setUradio(korisnik);
									Servis.racunServis.unesiRacun(racun);
									pokaziPorukuUspesno("подаци сачувани");
								}
							}
						updateTableRacuni();
						ocistiRacun();
						racuni.deselectAll();
						}
					});
				}
			}
		});
		izbrisiRacun = new Button();
		izbrisiRacun.addStyleName(ValoTheme.BUTTON_DANGER);
		izbrisiRacun.setIcon(VaadinIcons.MINUS_CIRCLE);
		izbrisiRacun.setDescription("избриши изабрани рачун");
		izbrisiRacun.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(getUI(), "Провера", "Избриши изабрани рачун?", "да", "не", new ConfirmDialog.Listener() {
					private static final long serialVersionUID = 1L;
					@Override
					public void onClose(ConfirmDialog dialog) {
						if(dialog.isConfirmed()) {
							Servis.racunServis.izbrisiRacun(izabraniRacun);
							updateTableRacuni();
							ocistiRacun();
							racuni.deselectAll();
						}
					}
				});
			}
		});
		
		upload = new Button();
		upload.setIcon(VaadinIcons.CLOUD_UPLOAD_O);
		ucitavanje = new Window("учитавање");
		ucitavanje.setWidth(300.0f, Unit.PIXELS);
		final FormLayout content = new FormLayout();
        content.setMargin(true);
        ucitavanje.setContent(content);
        ucitavanje.setResizable(false);
        ucitavanje.setModal(true);
        ucitavanje.center();
        vrsteUcitavanja = new ComboBox<>();
        vrsteUcitavanja.setSizeFull();
        up = new Upload();
        up.setSizeFull();
        //up.setImmediateMode(false);
        
        ucitavanjeSadrzaj = new VerticalLayout();
        ucitavanjeSadrzaj.addComponent(vrsteUcitavanja);
        ucitavanjeSadrzaj.addComponent(up);
        ucitavanje.setContent(ucitavanjeSadrzaj);
        
		upload.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				if(izabraniRacun != null) {
					getUI().getUI().addWindow(ucitavanje);
				}else {
					pokaziPorukuGreska("морате изабрати рачун!");
				}
			}
		});
		
		racuniHorizontal.addComponent(racuniFilter);
		if(isSistem()) {
			racuniHorizontal.addComponent(pretplatnici);
		}
		if(korisnik.isAdmin() && korisnik.getOrganizacija() == null) {
			racuniHorizontal.addComponent(organizacije);
		}
		racuniHorizontal.addComponent(partneri);
		racuniHorizontal.addComponent(datum);
		racuniHorizontal.addComponent(brojRacuna);
		racuniHorizontal.addComponent(opis);
		
		racuniHorizontal.addComponent(dodajRacun);
		racuniHorizontal.addComponent(sacuvajRacun);
		racuniHorizontal.addComponent(izbrisiRacun);
		racuniHorizontal.setComponentAlignment(racuniFilter, Alignment.MIDDLE_LEFT);
		
		
		stavkeHorizontal = new Horizontalni();
		stavkeFilter = new Filter("ставке...");
		stavkeHorizontal.addComponent(stavkeFilter);
		stavkeHorizontal.setComponentAlignment(stavkeFilter, Alignment.MIDDLE_LEFT);
		objekti = new ComboObjektiSaVozilima(pretplatnici.getValue(), organizacije.getValue(), null, true, false);
		gorivo = new ComboGorivo(null, true, false);
		kolicina = new Decimalni(null, false);
		kolicina.setPlaceholder("количина...");
		kolicina.setDescription("количина");
		cena = new Decimalni(null, false);
		cena.setPlaceholder("цена");
		cena.setDescription("цена");
		pdvProcenat = new Celobrojni(null, false);
		pdvProcenat.setDescription("пдв %");
		pdvIznos = new Decimalni(null, false);
		pdvIznos.setEnabled(false);
		pdvIznos.setDescription("пдв износ");
		ukupno = new Decimalni(null, false);
		ukupno.setEnabled(false);
		ukupno.setDescription("ставка укупно");
		
		kolicina.setValue("0");
		cena.setValue("0");
		pdvProcenat.setValue("0");
		pdvIznos.setValue("0");
		ukupno.setValue("0");
		
		dodajStavku = new Button();
		dodajStavku.addStyleName(ValoTheme.BUTTON_PRIMARY);
		dodajStavku.setIcon(VaadinIcons.PLUS_CIRCLE);
		dodajStavku.setDescription("додај нови рачун");
		dodajStavku.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				izabranaStavka = null;
				ocistiStavku();;
				troskovi.deselectAll();
			}
		});
		sacuvajStavku = new Button();
		sacuvajStavku.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		sacuvajStavku.setIcon(VaadinIcons.CHECK_CIRCLE);
		sacuvajStavku.setDescription("сачувај ставку");
		sacuvajStavku.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				if(izabraniRacun != null) {
					if(objekti.getValue() == null || gorivo.getValue() == null || 
							kolicina.getValue() == null || kolicina.getValue().isEmpty() || kolicina.getValue().equals("") ||
							cena.getValue() == null || cena.getValue().isEmpty() || cena.getValue().equals("") || 
							pdvProcenat.getValue() == null || pdvProcenat.isEmpty() || pdvProcenat.equals("")) {
						pokaziPorukuGreska("поља објекат, гориво, количина и цена морају бити попуњена!");
						}else {
							ConfirmDialog.show(getUI(), "Провера", "Сачувај унете податке?", "да", "не", new ConfirmDialog.Listener() {
								private static final long serialVersionUID = 1L;
								@Override
								public void onClose(ConfirmDialog dialog) {
									if(dialog.isConfirmed()) {
										if(izabranaStavka != null) {
											izabranaStavka.setSistemPretplatnici(izabraniRacun.getSistemPretplatnici());
											izabranaStavka.setOrganizacija(izabraniRacun.getOrganizacija());
											izabranaStavka.setRacun(izabraniRacun);
											izabranaStavka.setPartner(izabraniRacun.getPartner());
											izabranaStavka.setDatumVreme(new Timestamp(izabraniRacun.getDatum().getTime()));
											izabranaStavka.setObjekti(objekti.getValue());
											izabranaStavka.setSistemGoriva(gorivo.getValue());
											izabranaStavka.setTipServisa(0);
											izabranaStavka.setKolicina(Float.parseFloat(kolicina.getValue()));
											izabranaStavka.setCena(Float.parseFloat(cena.vratiIznos()));
											izabranaStavka.setPdvProcenat(Integer.parseInt(pdvProcenat.getValue()));
											izabranaStavka.setPdvIznos((izabranaStavka.getKolicina() * izabranaStavka.getCena() * izabranaStavka.getPdvProcenat())/100);
											izabranaStavka.setUkupno(izabranaStavka.getKolicina() * izabranaStavka.getCena() + izabranaStavka.getPdvIznos());
											izabranaStavka.setOpis("");
											izabranaStavka.setIzbrisan(false);
											Servis.trosakServis.izmeniTrosak(izabranaStavka);
											izabranaStavka = null;
											pokaziPorukuUspesno("подаци измењени");
											}else {
												Troskovi trosak = new Troskovi();
												trosak.setSistemPretplatnici(izabraniRacun.getSistemPretplatnici());
												trosak.setOrganizacija(izabraniRacun.getOrganizacija());
												trosak.setRacun(izabraniRacun);
												trosak.setBrojRacuna(izabraniRacun.getBrojRacuna());
												trosak.setPartner(izabraniRacun.getPartner());
												trosak.setDatumVreme(new Timestamp(izabraniRacun.getDatum().getTime()));
												trosak.setObjekti(objekti.getValue());
												trosak.setTipServisa(0);
												trosak.setSistemGoriva(gorivo.getValue());
												trosak.setKolicina(Float.parseFloat(kolicina.getValue()));
												trosak.setCena(Float.parseFloat(cena.getValue()));
												trosak.setPdvProcenat(Integer.parseInt(pdvProcenat.getValue()));
												trosak.setPdvIznos((trosak.getKolicina() * trosak.getCena() * trosak.getPdvProcenat())/100);
												trosak.setUkupno(trosak.getKolicina() * trosak.getCena() + trosak.getPdvIznos());
												trosak.setOpis("");
												trosak.setIzbrisan(false);
												Servis.trosakServis.unesiTrosak(trosak);
												pokaziPorukuUspesno("подаци сачувани");
												}
										}
									updateTableTroskovi();
									ocistiStavku();
									troskovi.deselectAll();
									}
								});
							}
					}else {
						pokaziPorukuGreska("морате изабрати рачун!");
					}
				}
			});
		izbrisiStavku = new Button();
		izbrisiStavku.addStyleName(ValoTheme.BUTTON_DANGER);
		izbrisiStavku.setIcon(VaadinIcons.MINUS_CIRCLE);
		izbrisiStavku.setDescription("избриши изабрани рачун");
		izbrisiStavku.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(getUI(), "Провера", "Избриши изабрани ставку?", "да", "не", new ConfirmDialog.Listener() {
					private static final long serialVersionUID = 1L;
					@Override
					public void onClose(ConfirmDialog dialog) {
						if(dialog.isConfirmed()) {
							Servis.trosakServis.izbrisiTrosak(izabranaStavka);
							updateTableTroskovi();
							ocistiStavku();
							troskovi.deselectAll();
						}
					}
				});
			}
		});
		stavkeHorizontal.addComponent(objekti);
		stavkeHorizontal.addComponent(gorivo);
		stavkeHorizontal.addComponent(kolicina);
		stavkeHorizontal.addComponent(cena);
		stavkeHorizontal.addComponent(pdvProcenat);
		stavkeHorizontal.addComponent(pdvIznos);
		stavkeHorizontal.addComponent(dodajStavku);
		stavkeHorizontal.addComponent(sacuvajStavku);
		stavkeHorizontal.addComponent(izbrisiStavku);
		stavkeHorizontal.addComponent(upload);
		
		raspodelaHorizontal = new Horizontalni();
		partneriRaspodela = new ComboPartneri(korisnik, null, true, false);
		iznosRaspodele = new Decimalni(null, false);
		iznosRaspodele.setDescription("износ расподеле");
		iznosRaspodele.setValue(String.valueOf(0));
		dodajRaspodelu = new Button();
		dodajRaspodelu.addStyleName(ValoTheme.BUTTON_PRIMARY);
		dodajRaspodelu.setIcon(VaadinIcons.PLUS_CIRCLE);
		dodajRaspodelu.setDescription("додај нови рачун");
		dodajRaspodelu.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				izabranaRaspodela = null;
				ocistiRaspodelu();
				raspodela.deselectAll();
			}
		});
		sacuvajRaspodelu = new Button();
		sacuvajRaspodelu.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		sacuvajRaspodelu.setIcon(VaadinIcons.CHECK_CIRCLE);
		sacuvajRaspodelu.setDescription("сачувај расподелу");
		sacuvajRaspodelu.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				if(izabraniRacun != null) {
					float unetiIznos = Float.parseFloat(iznosRaspodele.vratiIznos());
					if(partneriRaspodela.getValue() == null || iznosRaspodele.getValue() == null || iznosRaspodele.isEmpty() || unetiIznos == 0.0f) {
						pokaziPorukuGreska("морате унети партнера и исправан износ!");
					}else {
						float ukupanIznos = 0;
						for(Troskovi trosak : listaStavke) {
							ukupanIznos += trosak.getUkupno();
							}
						float ukupnoRaspodela = 0;
						for(RacuniRaspodela raspodela : listaRaspodela) {
							if(izabranaRaspodela == null || !izabranaRaspodela.getId().equals(raspodela.getId())) {
								
							}else {
								ukupnoRaspodela += raspodela.getIznos();
								}
							}
						ukupnoRaspodela += Float.parseFloat(iznosRaspodele.vratiIznos());
						if(ukupanIznos >= ukupnoRaspodela) {
							ConfirmDialog.show(getUI(), "Провера", "Сачувај унете податке?", "да", "не", new ConfirmDialog.Listener() {
								private static final long serialVersionUID = 1L;
								@Override
								public void onClose(ConfirmDialog dialog) {
									if(dialog.isConfirmed()) {
										if(izabranaRaspodela != null) {
											izabranaRaspodela.setSistemPretplatnici(izabraniRacun.getSistemPretplatnici());
											izabranaRaspodela.setOrganizacija(izabraniRacun.getOrganizacija());
											izabranaRaspodela.setPartner(partneriRaspodela.getValue());
											izabranaRaspodela.setRacun(izabraniRacun);
											izabranaRaspodela.setIznos(Float.parseFloat(iznosRaspodele.getValue()));
											Servis.racunRaspodelaServis.izmeniRacunRaspodelu(izabranaRaspodela);
											pokaziPorukuUspesno("подаци измењени");
											}else {
												RacuniRaspodela raspodela = new RacuniRaspodela();
												raspodela.setSistemPretplatnici(izabraniRacun.getSistemPretplatnici());
												raspodela.setOrganizacija(izabraniRacun.getOrganizacija());
												raspodela.setPartner(partneriRaspodela.getValue());
												raspodela.setRacun(izabraniRacun);
												raspodela.setIznos(Float.parseFloat(iznosRaspodele.vratiIznos()));
												Servis.racunRaspodelaServis.unesiRacunRaspodelu(raspodela);
												pokaziPorukuUspesno("подаци сачувани");
												}
										updateTableRaspodela();
										ocistiRaspodelu();
										raspodela.deselectAll();
									}
								}
							});
							}else {
								pokaziPorukuGreska("укупан износ расподеле не може бити већи од укупног износа ставки!");
								}
						}
					}else {
						pokaziPorukuGreska("морате изабрати рачун!");
						}	
				}
			});
		izbrisiRaspodelu = new Button();
		izbrisiRaspodelu.addStyleName(ValoTheme.BUTTON_DANGER);
		izbrisiRaspodelu.setIcon(VaadinIcons.MINUS_CIRCLE);
		izbrisiRaspodelu.setDescription("избриши изабрани рачун");
		izbrisiRaspodelu.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(getUI(), "Провера", "Избриши изабрани ставку?", "да", "не", new ConfirmDialog.Listener() {
					private static final long serialVersionUID = 1L;
					@Override
					public void onClose(ConfirmDialog dialog) {
						if(dialog.isConfirmed()) {
							Servis.racunRaspodelaServis.izbrisiRacunRaspodelu(izabranaRaspodela);
							updateTableRaspodela();
							ocistiRaspodelu();
							raspodela.deselectAll();
						}
					}
				});
			}
		});
		raspodelaHorizontal.addComponent(partneriRaspodela);
		raspodelaHorizontal.addComponent(iznosRaspodele);
		raspodelaHorizontal.addComponent(dodajRaspodelu);
		raspodelaHorizontal.addComponent(sacuvajRaspodelu);
		raspodelaHorizontal.addComponent(izbrisiRaspodelu);
	}
	
	private void postaviTabeluRacuna() {
		racuni = new Grid<Racuni>();
		pocetnoRacuni = new ArrayList<Racuni>();
		updateTableRacuni();
		racuni.setSizeFull();
		racuni.setStyleName("list");
		racuni.setSelectionMode(SelectionMode.SINGLE);
		
		if(isSistem()) {
			racuni.addColumn(racuni -> racuni.getSistemPretplatnici() == null ? "" : racuni.getSistemPretplatnici().getNaziv()).setCaption("претплатник");
		}
		racuni.addColumn(Racuni::getDatum, new DateRenderer(DANFORMAT)).setCaption("датум").setStyleGenerator(objekti -> "v-align-right");
		racuni.addColumn(racuni -> racuni.getPartner() == null ? "" : racuni.getPartner().getNaziv()).setCaption("партнер");
		racuni.addColumn(Racuni::getBrojRacuna).setCaption("број рачуна");
		racuni.addColumn(racuni -> racuni.getOpis() == null ? "" : racuni.getOpis()).setCaption("опис");
		racuni.addColumn(racuni -> racuni.getUradio() == null ? "" : racuni.getUradio().toString()).setCaption("урадио");
		if(isSistem() || (korisnik.isAdmin() && korisnik.getOrganizacija() == null)) {
			racuni.addColumn(racuni -> racuni.getOrganizacija() == null ? "" : racuni.getOrganizacija() == null ? "" : racuni.getOrganizacija().getNaziv()).setCaption("организација");
		}
		if(isSistem()) {
			racuni.addComponentColumn(racuni -> {CheckBox chb = new CheckBox(); if(racuni.isIzbrisan()) {chb.setValue(true);} return chb;}).setCaption("избрисан").setStyleGenerator(objekti -> "v-align-right");
		}
		racuni.addColumn(Racuni::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		racuni.addColumn(Racuni::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
		
		racuni.addSelectionListener(new SelectionListener<Racuni>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<Racuni> event) {
				if(event.getFirstSelectedItem().isPresent()) {
					izabraniRacun = event.getFirstSelectedItem().get();
					datum.setValue(localDatum(izabraniRacun.getDatum()));
					partneri.setValue(izabraniRacun.getPartner());
					brojRacuna.setValue(izabraniRacun.getBrojRacuna());
					if(izabraniRacun.getOpis() != null) {
						opis.setValue(izabraniRacun.getOpis());
					}else {
						opis.setValue("");
					}
					updateTableTroskovi();
					updateTableRaspodela();
				}
			}
		});
	}
	
	private void postaviTabeluTroskova() {
		troskovi = new Grid<Troskovi>();
		pocetnoStavke = new ArrayList<Troskovi>();
		troskovi.setSizeFull();
		troskovi.setStyleName("list");
		troskovi.setSelectionMode(SelectionMode.SINGLE);
		troskovi.addColumn(troskovi -> troskovi.getObjekti() == null ? "" : troskovi.getObjekti().getOznaka()).setCaption("објекат");
		troskovi.addColumn(troskovi -> troskovi.getSistemGoriva() == null ? "" : troskovi.getSistemGoriva().getNaziv()).setCaption("врста горива").setStyleGenerator(objekti -> "v-align-right");
		troskovi.addColumn(Troskovi::getKolicina).setCaption("количина").setStyleGenerator(objekti -> "v-align-right");
		troskovi.addColumn(Troskovi::getCena).setCaption("цена").setStyleGenerator(objekti -> "v-align-right");
		troskovi.addColumn(Troskovi::getPdvProcenat).setCaption("ПДВ %").setStyleGenerator(objekti -> "v-align-right");
		troskovi.addColumn(Troskovi::getPdvIznos).setCaption("ПДВ").setStyleGenerator(objekti -> "v-align-right");
		troskovi.addColumn(Troskovi::getUkupno).setCaption("укупно").setStyleGenerator(objekti -> "v-align-right");
		troskovi.addColumn(Troskovi::getOpis).setCaption("опис").setStyleGenerator(objekti -> "v-align-right");
		troskovi.addColumn(Troskovi::getIzmenjeno, new DateRenderer(DANSATFORMAT)).setCaption("измењено").setStyleGenerator(objekti -> "v-align-right");
		troskovi.addColumn(Troskovi::getKreirano, new DateRenderer(DANSATFORMAT)).setCaption("креирано").setStyleGenerator(objekti -> "v-align-right");
		troskovi.addSelectionListener(new SelectionListener<Troskovi>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<Troskovi> event) {
				if(event.getFirstSelectedItem().isPresent()) {
					izabranaStavka = event.getFirstSelectedItem().get();
					objekti.setValue(izabranaStavka.getObjekti());
					gorivo.setValue(izabranaStavka.getSistemGoriva());
					try {
						kolicina.setValue(String.valueOf(izabranaStavka.getKolicina()));
					}catch (Exception e) {
						kolicina.setValue(String.valueOf(0));
					}
					try {
						cena.setValue(String.valueOf(izabranaStavka.getCena()));
					}catch (Exception e) {
						cena.setValue(String.valueOf(0));
					}
					try {
						pdvProcenat.setValue(String.valueOf(izabranaStavka.getPdvProcenat()));
					}catch (Exception e) {
						pdvProcenat.setValue(String.valueOf(0));
					}
					try {
						pdvIznos.setValue(String.valueOf(izabranaStavka.getPdvIznos()));
					}catch (Exception e) {
						pdvIznos.setValue(String.valueOf(0));
					}
					try {
						ukupno.setValue(String.valueOf(izabranaStavka.getUkupno()));
					}catch (Exception e) {
						ukupno.setValue(String.valueOf(0));
					}
				}
				
			}
		});
	}
	
	public void postaviTabeluRaspodela() {
		raspodela = new Grid<RacuniRaspodela>();
		pocetnoRaspodela = new ArrayList<RacuniRaspodela>();
		raspodela.setSizeFull();
		raspodela.setStyleName("list");
		raspodela.setSelectionMode(SelectionMode.SINGLE);
		raspodela.addColumn(raspodela -> raspodela.getPartner() == null ? "" : raspodela.getPartner().getNaziv());
		raspodela.addColumn(RacuniRaspodela::getIznos).setCaption("износ").setStyleGenerator(objekti -> "v-align-right");
		raspodela.addSelectionListener(new SelectionListener<RacuniRaspodela>() {
			private static final long serialVersionUID = 1L;
			@Override
			public void selectionChange(SelectionEvent<RacuniRaspodela> event) {
				if(event.getFirstSelectedItem().isPresent()) {
					izabranaRaspodela = event.getFirstSelectedItem().get();
					partneriRaspodela.setValue(izabranaRaspodela.getPartner());
					iznosRaspodele.setValue(String.valueOf(izabranaRaspodela.getIznos()));
				}
			}
		});
	}
	
	public void updateTableRacuni() {
		racuniFilter.clear();
		listaRacuni = Servis.racunServis.nadjiRacunePoPretplatniku(korisnik.getSistemPretplatnici(), korisnik.getOrganizacija(), true, null, null, null);
		if(listaRacuni != null) {
			racuni.setItems(listaRacuni);
		}else {
			racuni.setItems(pocetnoRacuni);
		}
		dodajFilterRacuni();
	}

	public void osveziFilterRacuni() {
		dataProviderRacuni.setFilter(filterPredicateRacuni);
		dataProviderRacuni.refreshAll();
	}
	
	@SuppressWarnings("unchecked")
	public void dodajFilterRacuni() {
		dataProviderRacuni = (ListDataProvider<Racuni>)racuni.getDataProvider();
		filterPredicateRacuni = new SerializablePredicate<Racuni>() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean test(Racuni t) {
				return ((t.getSistemPretplatnici() == null ? "" : t.getSistemPretplatnici().getNaziv()).toLowerCase().contains(racuniFilter.getValue().toLowerCase()) ||
						(t.getBrojRacuna() == null ? "" : t.getBrojRacuna()).toLowerCase().contains(racuniFilter.getValue().toLowerCase()) ||
						(t.getPartner() == null ? "" : t.getPartner().getNaziv()).toLowerCase().contains(racuniFilter.getValue().toLowerCase()));
			}
		};
		racuniFilter.addValueChangeListener(e -> {osveziFilterRacuni();});
	}
	
	private void ocistiRacun() {
		datum.clear();
		partneri.clear();
		brojRacuna.clear();
		opis.clear();
	}
	
	private void updateTableTroskovi() {
		stavkeFilter.clear();
		listaStavke = Servis.trosakServis.nadjiSvuPotrosnjuPoRacunu(izabraniRacun);
		if(listaStavke != null) {
			troskovi.setItems(listaStavke);
		}else {
			troskovi.setItems(pocetnoStavke);
		}
		dodajFilterStavke();
	}
	
	public void osveziFilterStavke() {
		dataProviderTroskovi.setFilter(filterPredicateTroskovi);
		dataProviderTroskovi.refreshAll();
	}
	
	@SuppressWarnings("unchecked")
	public void dodajFilterStavke() {
		dataProviderTroskovi = (ListDataProvider<Troskovi>)troskovi.getDataProvider();
		filterPredicateTroskovi = new SerializablePredicate<Troskovi>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean test(Troskovi t) {
				return ((t.getObjekti() == null ? "" : t.getObjekti().getOznaka()).toLowerCase().contains(racuniFilter.getValue().toLowerCase()));
			}
		};
		stavkeFilter.addValueChangeListener(e -> {osveziFilterStavke();});
	}

	private void ocistiStavku() {
		objekti.clear();
		gorivo.clear();
		kolicina.setValue("0");
		cena.setValue("0");
		pdvProcenat.setValue("0");
		pdvIznos.setValue("0");
		ukupno.setValue("0");
	}
	
	private void updateTableRaspodela() {
		listaRaspodela = Servis.racunRaspodelaServis.nadjiRacuneRaspodelePoRacunu(izabraniRacun);
		if(listaRaspodela != null) {
			raspodela.setItems(listaRaspodela);
		}else {
			raspodela.setItems(pocetnoRaspodela);
		}
	}
	
	private void ocistiRaspodelu() {
		partneriRaspodela.clear();
		iznosRaspodele.setValue(String.valueOf(0));
	}

	public LocalDate localDatum(Date datum) {
		return (new Date(datum.getTime())).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	public Date dateDatum(LocalDate datum) {
		return Date.from(datum.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	
	public boolean isSistem() {
		return (korisnik.isSistem() && korisnik.getSistemPretplatnici().isSistem());
	}
	
	public void pokaziPorukuGreska(String msg) {
		Notification.show(msg, Type.ERROR_MESSAGE);
	}

	public void pokaziPorukuUspesno(String msg) {
		Notification.show(msg, Type.TRAY_NOTIFICATION);
	}
	
}
