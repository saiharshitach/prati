package rs.cybertrade.prati;

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
import rs.cybertrade.prati.meni.PratiEvent.KorisnikLoggedOutEvent;
import rs.cybertrade.prati.meni.PratiEventBus;
import rs.cybertrade.prati.view.IstorijaView;
import rs.cybertrade.prati.view.PocetnaView;
import rs.cybertrade.prati.view.PracenjeView;
import rs.cybertrade.prati.view.alarmi.AlarmiView;
import rs.cybertrade.prati.view.gorivo.GorivoView;
import rs.cybertrade.prati.view.grupe.GrupeObjektiView;
import rs.cybertrade.prati.view.grupe.GrupeView;
import rs.cybertrade.prati.view.korisnici.KorisniciView;
import rs.cybertrade.prati.view.objekti.ObjektiView;
import rs.cybertrade.prati.view.organizacije.OrganizacijeView;
import rs.cybertrade.prati.view.pretplatnici.PretplatniciView;
import rs.cybertrade.prati.view.proizvodjaci.UredjajiProizvodjaciView;
import rs.cybertrade.prati.view.sim.SimView;
import rs.cybertrade.prati.view.simSistemOperateri.SimOperateriView;
import rs.cybertrade.prati.view.sistem.SistemView;
import rs.cybertrade.prati.view.uredjaji.UredjajiView;
import rs.cybertrade.prati.view.uredjajiModeli.UredjajiModeliView;
import rs.cybertrade.prati.view.zone.ZoneView;

public class PratiMeniKorisnik{
	
	private static DefaultNotificationHolder notifications;
	private static DefaultBadgeHolder badge;
	
	public PratiMeniKorisnik() {
		badge = new DefaultBadgeHolder();
		notifications = new DefaultNotificationHolder();
	}
	
	public AppLayoutComponent vratiMeniKorisnik(String korisnik) {
		return AppLayout.getDefaultBuilder(Behaviour.LEFT_RESPONSIVE_OVERLAY_NO_APP_BAR)
        .withTitle("Праћење")
        .addToAppBar(new AppBarNotificationButton(notifications))
        .withNavigationElementInfoProducer(new DefaultNavigationElementInfoProducer())
        .withDesign(AppLayoutDesign.MATERIAL)
        .add(new MenuHeader("Version 1.0.3", null), HEADER)
        .add(badge, PocetnaView.class)
        .add(PracenjeView.class)
        .add(SubmenuBuilder.get("Извештаји", VaadinIcons.BAR_CHART)
                .add(IstorijaView.class)
                .build())
        /*.add(SubmenuBuilder.get("Подаци", VaadinIcons.COG)
                .add(ObjektiView.class)
                .build())**/
        .addClickable("Одјава", VaadinIcons.SIGN_OUT, clickEvent -> PratiEventBus.post(new KorisnikLoggedOutEvent()), FOOTER)
        //.add(View6.class)
        .build();
	}
	
	public AppLayoutComponent vratiMeniAdministrator(String korisnik) {
		return AppLayout.getDefaultBuilder(Behaviour.LEFT_RESPONSIVE_OVERLAY_NO_APP_BAR)
        .withTitle("Праћење")
        .addToAppBar(new AppBarNotificationButton(notifications))
        .withNavigationElementInfoProducer(new DefaultNavigationElementInfoProducer())
        .withDesign(AppLayoutDesign.MATERIAL)
        .add(new MenuHeader(korisnik, null), HEADER)//.add(new MenuHeader("Version 1.0.3", new ThemeResource("logo.png")), HEADER)
        .add(badge, PocetnaView.class)
        .add(PracenjeView.class)
        .add(SubmenuBuilder.get("Извештаји", VaadinIcons.BAR_CHART)
                .add(IstorijaView.class)
                .build())
        .add(SubmenuBuilder.get("Подаци", VaadinIcons.COG)
        		.add(GrupeView.class)
        		.add(GrupeObjektiView.class)
        		.add(KorisniciView.class)
                .add(ObjektiView.class)
                .add(OrganizacijeView.class)
                .add(SimView.class)
                .add(UredjajiView.class)
                .add(ZoneView.class)
                .build())
        //.add(View6.class)
        .addClickable("Одјава", VaadinIcons.SIGN_OUT, clickEvent -> PratiEventBus.post(new KorisnikLoggedOutEvent()), FOOTER)
        .build();
	
	}
	
	public AppLayoutComponent vratiMeniSistem(String korisnik) {
		return AppLayout.getDefaultBuilder(Behaviour.LEFT_RESPONSIVE_OVERLAY_NO_APP_BAR)
		        .withTitle("Праћење")
		        .addToAppBar(new AppBarNotificationButton(notifications))
		        .withNavigationElementInfoProducer(new DefaultNavigationElementInfoProducer())
		        .withDesign(AppLayoutDesign.MATERIAL)
		        .add(new MenuHeader(korisnik, null), HEADER)//.add(new MenuHeader("Version 1.0.3", new ThemeResource("logo.png")), HEADER)
		        .add(badge, PocetnaView.class)
		        .add(PracenjeView.class)
		        .add(SubmenuBuilder.get("Извештаји", VaadinIcons.BAR_CHART)
		                .add(IstorijaView.class)
		                .build())
		        .add(SubmenuBuilder.get("Подаци", VaadinIcons.COG)
		        		.add(GrupeView.class)
		        		.add(GrupeObjektiView.class)
		        		.add(KorisniciView.class)
		                .add(ObjektiView.class)
		                .add(OrganizacijeView.class)
		                .add(SimView.class)
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
