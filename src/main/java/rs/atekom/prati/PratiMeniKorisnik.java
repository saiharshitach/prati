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

import rs.atekom.prati.izvestaji.IzvestajiView;
import rs.atekom.prati.meni.PratiEventBus;
import rs.atekom.prati.meni.PratiEvent.KorisnikLoggedOutEvent;
import rs.atekom.prati.view.IstorijaView;
import rs.atekom.prati.view.PocetnaView;
import rs.atekom.prati.view.PracenjeView;
import rs.atekom.prati.view.alarmi.AlarmiView;
import rs.atekom.prati.view.alarmiKorisnik.AlarmKorisnikView;
import rs.atekom.prati.view.gorivo.GorivoView;
import rs.atekom.prati.view.grupe.GrupeObjektiView;
import rs.atekom.prati.view.grupe.GrupeView;
import rs.atekom.prati.view.korisnici.KorisniciView;
import rs.atekom.prati.view.objekatZone.ObjekatZoneView;
import rs.atekom.prati.view.objekti.ObjektiView;
import rs.atekom.prati.view.objektiDetalji.ObjektiDetaljiView;
import rs.atekom.prati.view.organizacije.OrganizacijeView;
import rs.atekom.prati.view.pretplatnici.PretplatniciView;
import rs.atekom.prati.view.proizvodjaci.UredjajiProizvodjaciView;
import rs.atekom.prati.view.sim.SimView;
import rs.atekom.prati.view.simSistemOperateri.SimOperateriView;
import rs.atekom.prati.view.sistem.SistemView;
import rs.atekom.prati.view.sistemSesije.SistemSesijeView;
import rs.atekom.prati.view.uredjaji.UredjajiView;
import rs.atekom.prati.view.uredjajiModeli.UredjajiModeliView;
import rs.atekom.prati.view.zone.ZoneView;

public class PratiMeniKorisnik{
	
	private static DefaultNotificationHolder notifications;
	private static DefaultBadgeHolder badge;
	
	public PratiMeniKorisnik() {
		badge = new DefaultBadgeHolder();
		notifications = new DefaultNotificationHolder();
	}
	
	public AppLayoutComponent vratiMeniKorisnik(String korisnik) {
		return AppLayout.getDefaultBuilder(Behaviour.LEFT_RESPONSIVE_HYBRID_OVERLAY_NO_APP_BAR)
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
        .addClickable("Одјава", VaadinIcons.SIGN_OUT, clickEvent -> PratiEventBus.post(new KorisnikLoggedOutEvent()), FOOTER)
        //.add(View6.class)
        .build();
	}
	
	public AppLayoutComponent vratiMeniAdministrator(String korisnik) {
		return AppLayout.getDefaultBuilder(Behaviour.LEFT_RESPONSIVE_HYBRID_OVERLAY_NO_APP_BAR)
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
        .add(SubmenuBuilder.get("Подаци", VaadinIcons.COG)
        		.add(GrupeView.class)
        		.add(GrupeObjektiView.class)
        		.add(KorisniciView.class)
                .add(ObjektiView.class)
                .add(ObjekatZoneView.class)
                .add(ObjektiDetaljiView.class)
                .add(OrganizacijeView.class)
                .add(SimView.class)
                .add(SistemSesijeView.class)
                .add(UredjajiView.class)
                .add(ZoneView.class)
                .build())
        //.add(View6.class)
        .addClickable("Одјава", VaadinIcons.SIGN_OUT, clickEvent -> PratiEventBus.post(new KorisnikLoggedOutEvent()), FOOTER)
        .build();
	
	}
	
	public AppLayoutComponent vratiMeniSistem(String korisnik) {
		return AppLayout.getDefaultBuilder(Behaviour.LEFT_RESPONSIVE_HYBRID_OVERLAY_NO_APP_BAR)
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
		        .add(SubmenuBuilder.get("Подаци", VaadinIcons.COG)
		        		.add(GrupeView.class)
		        		.add(GrupeObjektiView.class)
		        		.add(KorisniciView.class)
		                .add(ObjektiView.class)
		                .add(ObjekatZoneView.class)
		                .add(ObjektiDetaljiView.class)
		                .add(OrganizacijeView.class)
		                .add(SimView.class)
		                .add(SistemSesijeView.class)
		                .add(UredjajiView.class)
		                .add(ZoneView.class)
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
