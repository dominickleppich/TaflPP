package taflPP;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import taflPP.gui.Gui;
import taflPP.gui.MatchPanel;
import taflPP.match.Match;
import taflPP.net.MultiplayerSetup;
import taflPP.player.HumanPlayer;
import taflPP.preset.Player;
import eu.nepster.toolkit.file.FileAccess;
import eu.nepster.toolkit.gfx.objects.Graphic;
import eu.nepster.toolkit.io.IO;
import eu.nepster.toolkit.io.output.FileLog;
import eu.nepster.toolkit.io.output.SystemOut;
import eu.nepster.toolkit.lang.Language;
import eu.nepster.toolkit.settings.ArgumentParser;
import eu.nepster.toolkit.settings.Settings;

/**
 * Startklasse f&uuml;r TaflPP
 * 
 * @author Dominick Leppich
 *
 */
public class StartNetworkHost {

	public static void main(String[] args) {
		int size = 9;
		/* Lade Einstellungen */
		Settings.CFG.load();
		Settings.CFG.setSetNotFoundAction(Settings.CREATE_SETTING);
		Settings.CFG.set("debug", false);

		/* Lade Argumente */
		ArgumentParser.parse(args, Settings.CFG);

		/* Lege Logger an */
		FileLog l = new FileLog();

		if (Settings.CFG.is("log")) {
			/* Initialisiere Eingabe-Ausgabe-Klasse */
			IO.register(l, IO.LEVEL_ALL_OUTPUT, false, true, false);
		}

		if (Settings.CFG.is("debug"))
			IO.register(new SystemOut(), IO.LEVEL_DEBUG, false, true, false);
		IO.register(new SystemOut(), IO.LEVEL_NORMAL, false, false, false);

		/* Lade Sprachen */
		Language.load(new File("cfg/lang"));

		/* Lade Grafiken */
		Graphic.GFX.showLoading(true);
		Graphic.GFX.loadSynchron(new File("gfx"), true, "");

		/* Erzeuge Spielpanel */
		MatchPanel mp = new MatchPanel();

		HumanPlayer red = new HumanPlayer(mp, size);

		MultiplayerSetup.startRMIRegistry(1337);
		try {
			System.out.println(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		Player blue = null;
		while (blue == null) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			blue = MultiplayerSetup.find("192.168.178.39", 1337, "net");
		}
		
		for (int i = 0; i < 500; i++) {
			/* Erzeuge ein Match */
			Match m = new Match((i % 2 == 0 ? red : blue), (i % 2 == 0 ? blue : red), "Host", "Client", mp, size, null);
			if (i == 0) {
				/* Erzeuge GUI */
				Gui g = new Gui(1);
				g.setTitle("Host");
				g.addPanel(mp);
				g.startRefresh();
			}

			m.startMatch();
			m.waitMatchEnd();
			if (Settings.CFG.is("save"))
				m.saveGame("NETGAME_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()), "Host vs. Client (Spiel " + (i + 1) + ")");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mp.stopRefresh();

		if (Settings.CFG.is("log")) {
			/* Speichere Logdatei */
			l.save(FileAccess.createFile("log/" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt"));
		}

		/* Speichere Einstellungen */
		Settings.CFG.save();
	}

}
