package rs.atekom.prati;

import com.github.appreciated.app.layout.AppLayout;
import com.github.appreciated.app.layout.behaviour.AppLayoutComponent;
import com.github.appreciated.app.layout.behaviour.Behaviour;
import com.github.appreciated.app.layout.builder.design.AppLayoutDesign;
import com.github.appreciated.app.layout.builder.elements.builders.SubmenuBuilder;
import com.github.appreciated.app.layout.builder.entities.DefaultBadgeHolder;
import com.github.appreciated.app.layout.builder.factories.DefaultNavigationElementInfoProducer;
import com.github.appreciated.app.layout.component.MenuHeader;
import com.github.appreciated.app.layout.component.button.AppBarNotificationButton;
import com.github.appreciated.app.layout.builder.entities.DefaultNotificationHolder;
import static com.github.appreciated.app.layout.builder.Section.HEADER;
import static com.github.appreciated.app.layout.builder.Section.FOOTER;
import com.vaadin.icons.VaadinIcons;
import rs.atekom.prati.meni.PratiEventBus;
import rs.atekom.prati.meni.PratiEvent.KorisnikLoggedOutEvent;
import rs.atekom.prati.view.PracenjeView;
import rs.atekom.prati.view.alarmi.AlarmiView;
import rs.atekom.prati.view.alarmiKorisnik.AlarmKorisnikView;
import rs.atekom.prati.view.gorivo.GorivoView;
import rs.atekom.prati.view.grupe.GrupeObjektiView;
import rs.atekom.prati.view.grupe.GrupeView;
import rs.atekom.prati.view.istorija.IstorijaView;
import rs.atekom.prati.view.izvestaji.IzvestajiView;
import rs.atekom.prati.view.kalendar.KalendarView;
import rs.atekom.prati.view.korisnici.KorisniciView;
import rs.atekom.prati.view.objekatZone.ObjekatZoneView;
import rs.atekom.prati.view.objekti.ObjektiView;
import rs.atekom.prati.view.organizacije.OrganizacijeView;
import rs.atekom.prati.view.partneri.PartneriView;
import rs.atekom.prati.view.pocetna.PocetnaView;
import rs.atekom.prati.view.pretplatnici.PretplatniciView;
import rs.atekom.prati.view.proizvodjaci.UredjajiProizvodjaciView;
import rs.atekom.prati.view.projekti.ProjektiView;
import rs.atekom.prati.view.ruta.RutaView;
import rs.atekom.prati.view.sifre.SifreView;
import rs.atekom.prati.view.sim.SimView;
import rs.atekom.prati.view.simSistemOperateri.SimOperateriView;
import rs.atekom.prati.view.sistem.SistemView;
import rs.atekom.prati.view.sistemSesije.SistemSesijeView;
import rs.atekom.prati.view.uredjaji.UredjajiView;
import rs.atekom.prati.view.uredjajiModeli.UredjajiModeliView;
import rs.atekom.prati.view.vozaci.VozaciView;
import rs.atekom.prati.view.vozaci.dozvole.VozaciDozvoleView;
import rs.atekom.prati.view.vozaci.lekarsko.VozaciLekarskoView;
import rs.atekom.prati.view.vozaci.licenca.VozaciLicencaView;
import rs.atekom.prati.view.vozaci.licna.VozaciLicnaView;
import rs.atekom.prati.view.vozaci.pasosi.VozaciPasosiView;
import rs.atekom.prati.view.vozila.VozilaView;
import rs.atekom.prati.view.vozila.izvestaji.IzvestajiVozilaView;
import rs.atekom.prati.view.vozila.saobracajna.VozilaSaobracajnaView;
import rs.atekom.prati.view.vozila.saobracajna2.VozilaSaobracajna2View;
import rs.atekom.prati.view.vozila.zbirni.ZbirniRacuniView;
import rs.atekom.prati.view.vozilo.nalozi.VozilaNaloziView;
import rs.atekom.prati.view.vozilo.oprema.VozilaOpremaView;
import rs.atekom.prati.view.vozilo.primoPredaje.VozilaPrimoPredajeView;
import rs.atekom.prati.view.zone.ZoneView;
import rs.atekom.view.troskoviOdrzavanje.OdrzavanjaView;
import rs.atekom.view.troskoviPotrosnja.PotrosnjaView;

public class PratiMeniKorisnik{
	
	private static DefaultNotificationHolder notifications;
	private static DefaultBadgeHolder badge;
	private static final Behaviour izgled = Behaviour.LEFT_RESPONSIVE_HYBRID_OVERLAY_NO_APP_BAR;
	
	public PratiMeniKorisnik() {
		badge = new DefaultBadgeHolder();
		notifications = new DefaultNotificationHolder();
	}
	
	//korisnik
	public AppLayoutComponent vratiMeniKorisnik(String korisnik) {
		return AppLayout.getDefaultBuilder(izgled)
        .withTitle("праћење")
        .addToAppBar(new AppBarNotificationButton(notifications))
        .withNavigationElementInfoProducer(new DefaultNavigationElementInfoProducer())
        .withDesign(AppLayoutDesign.DEFAULT)
        .add(new MenuHeader(korisnik, null), HEADER)//.add(new MenuHeader("Version 1.0.3", new ThemeResource("logo.png")), HEADER)
        .add(badge, PocetnaView.class)
        .add(PracenjeView.class)
        .add(IstorijaView.class)
        .add(IzvestajiView.class)
        .add(AlarmKorisnikView.class)
        //.add(SubmenuBuilder.get("Извештаји", VaadinIcons.BAR_CHART)
        		//.add(NivoGorivaView.class)
        		//.build())
        .addClickable("Одјава", VaadinIcons.SIGN_OUT, clickEvent -> PratiEventBus.post(new KorisnikLoggedOutEvent()), FOOTER)
        //.add(View6.class)
        .build();
	}
	
	//administrator
	public AppLayoutComponent vratiMeniAdministrator(String korisnik) {
		//AppLayoutComponent admin = vratiMeniKorisnik(korisnik);
		//AppLayoutDesign design = AppLayoutDesign.CUSTOM;
		return AppLayout.getDefaultBuilder(izgled)
        .withTitle("праћење")
        .addToAppBar(new AppBarNotificationButton(notifications))
        .withNavigationElementInfoProducer(new DefaultNavigationElementInfoProducer())
        .withDesign(AppLayoutDesign.DEFAULT)
        .add(new MenuHeader(korisnik, null), HEADER)//.add(new MenuHeader("Version 1.0.3", new ThemeResource("logo.png")), HEADER)
        .add(badge, PocetnaView.class)
        .add(PracenjeView.class)
        .add(IstorijaView.class)
        .add(IzvestajiView.class)
        .add(AlarmKorisnikView.class)
        //.add(SubmenuBuilder.get("Извештаји", VaadinIcons.BAR_CHART)
        		//.add(NivoGorivaView.class)
        		//.build())
        .add(SubmenuBuilder.get("Објекти", VaadinIcons.LOCATION_ARROW_CIRCLE)
        		.add(ObjektiView.class)
        		.add(UredjajiView.class)
                .add(SimView.class)
                .add(ObjekatZoneView.class)
        		.add(GrupeView.class)
        		.add(GrupeObjektiView.class)
                .build())
        .add(SubmenuBuilder.get("Администрација", VaadinIcons.COG)
        		.add(KorisniciView.class)
                .add(OrganizacijeView.class)
                .add(PartneriView.class)
                .add(RutaView.class)
                .add(SistemSesijeView.class)
                .add(ZoneView.class)
                .add(ProjektiView.class)
                .add(SifreView.class)
                .build())
        .add(SubmenuBuilder.get("Возила", VaadinIcons.CAR)
        		.add(PotrosnjaView.class)
        		.add(OdrzavanjaView.class)
        		.add(ZbirniRacuniView.class)
        		.add(IzvestajiVozilaView.class)
        		.add(VozilaNaloziView.class)
        		.add(KalendarView.class)
        		.add(VozilaPrimoPredajeView.class)
        		.add(VozilaView.class)
        		.add(VozilaSaobracajnaView.class)
        		.add(VozilaSaobracajna2View.class)
        		.add(VozilaOpremaView.class)
        		.build())
        .add(SubmenuBuilder.get("Возачи", VaadinIcons.USER_CHECK)
        		.add(VozaciView.class)
        		.add(VozaciDozvoleView.class)
        		.add(VozaciLekarskoView.class)
        		.add(VozaciLicencaView.class)
        		.add(VozaciLicnaView.class)
        		.add(VozaciPasosiView.class)
        		.build())
        //.add(View6.class)
        
        .addClickable("Одјава", VaadinIcons.SIGN_OUT, clickEvent -> PratiEventBus.post(new KorisnikLoggedOutEvent()), FOOTER)
        .build()
        /*.setDesign(design)**/;
	}
	
	//sistem
	public AppLayoutComponent vratiMeniSistem(String korisnik) {
		return AppLayout.getDefaultBuilder(izgled)
		        .withTitle("праћење")
		        .addToAppBar(new AppBarNotificationButton(notifications))
		        .withNavigationElementInfoProducer(new DefaultNavigationElementInfoProducer())
		        .withDesign(AppLayoutDesign.DEFAULT)
		        .add(new MenuHeader(korisnik, null), HEADER)//.add(new MenuHeader("Version 1.0.3", new ThemeResource("logo.png")), HEADER)
		        .add(badge, PocetnaView.class)
		        .add(PracenjeView.class)
		        .add(IstorijaView.class)
		        .add(IzvestajiView.class)
		        .add(AlarmKorisnikView.class)
		        //.add(SubmenuBuilder.get("Извештаји", VaadinIcons.BAR_CHART)
		        		//.add(NivoGorivaView.class)
		        		//.build())
		        .add(SubmenuBuilder.get("Објекти", VaadinIcons.LOCATION_ARROW_CIRCLE)
		        		.add(ObjektiView.class)
		        		.add(UredjajiView.class)
		                .add(SimView.class)
		                .add(ObjekatZoneView.class)
		        		.add(GrupeView.class)
		        		.add(GrupeObjektiView.class)
		                .build())
		        .add(SubmenuBuilder.get("Администрација", VaadinIcons.COG)
		        		.add(KorisniciView.class)
		                .add(OrganizacijeView.class)
		                .add(PartneriView.class)
		                .add(RutaView.class)
		                .add(SistemSesijeView.class)
		                .add(ZoneView.class)
		                .add(ProjektiView.class)
		                .add(SifreView.class)
		                .build())
		        .add(SubmenuBuilder.get("Возила", VaadinIcons.CAR)
		        		.add(PotrosnjaView.class)
		        		.add(OdrzavanjaView.class)
		        		.add(ZbirniRacuniView.class)
		        		.add(IzvestajiVozilaView.class)
		        		.add(VozilaNaloziView.class)
		        		.add(KalendarView.class)
		        		.add(VozilaPrimoPredajeView.class)
		        		.add(VozilaView.class)
		        		.add(VozilaSaobracajnaView.class)
		        		.add(VozilaSaobracajna2View.class)
		        		.add(VozilaOpremaView.class)
		        		.build())
                .add(SubmenuBuilder.get("Возачи", VaadinIcons.USER_CHECK)
                		.add(VozaciView.class)
                		.add(VozaciDozvoleView.class)
                		.add(VozaciLekarskoView.class)
                		.add(VozaciLicencaView.class)
                		.add(VozaciLicnaView.class)
                		.add(VozaciPasosiView.class)
                		.build())
		        .add(SubmenuBuilder.get("Систем", VaadinIcons.DOCTOR)
		        		.add(AlarmiView.class)
		        		.add(GorivoView.class)
		        		.add(SimOperateriView.class)
		        		.add(PretplatniciView.class)
		        		.add(SistemView.class)
		        		.add(UredjajiModeliView.class)
		        		.add(UredjajiProizvodjaciView.class)
		        		.build())
		        .addClickable("Одјава", VaadinIcons.SIGN_OUT, clickEvent -> PratiEventBus.post(new KorisnikLoggedOutEvent()), FOOTER)
		        .build();
	}
}
