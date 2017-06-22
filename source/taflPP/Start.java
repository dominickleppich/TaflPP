package taflPP;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import taflPP.board.AsciiBoard;
import taflPP.gui.Gui;
import taflPP.gui.MatchPanel;
import taflPP.match.Match;
import taflPP.player.HumanPlayer;
import taflPP.player.IOMoveInterpreter;
import taflPP.player.ai.RandomAI;
import taflPP.preset.Player;
import taflPP.preset.Requestable;
import eu.nepster.toolkit.file.FileAccess;
import eu.nepster.toolkit.gfx.objects.Graphic;
import eu.nepster.toolkit.io.IO;
import eu.nepster.toolkit.io.output.FileLog;
import eu.nepster.toolkit.io.output.SystemOut;
import eu.nepster.toolkit.lang.Language;
import eu.nepster.toolkit.plugin.ObjectLoader;
import eu.nepster.toolkit.settings.ArgumentParser;
import eu.nepster.toolkit.settings.Settings;

/**
 * Startklasse f&uuml;r TaflPP
 * 
 * @author Dominick Leppich
 *
 */
public class Start {

	public static void main(String[] args) {
		/* Lade Einstellungen */
		Settings.CFG.load();
		Settings.CFG.setSetNotFoundAction(Settings.CREATE_SETTING);
		Settings.CFG.set("debug", false);
		Settings.CFG.set("log", false);
		Settings.CFG.set("save", false);
		Settings.CFG.set("gui", true);
		Settings.CFG.set("games", 1);

		/* Lade Argumente */
		ArgumentParser.parse(args, Settings.CFG);

		int size = Settings.CFG.getInt("size");

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
		MatchPanel mp = null;
		Requestable rq = null;
		if (Settings.CFG.is("gui")) {
			mp = new MatchPanel();
			rq = mp;
		} else
			rq = new IOMoveInterpreter(size);

		/* Player Loader */
		ObjectLoader<Player> playerLoader = new ObjectLoader<Player>();

		/* Erzeuge Spieler */
		Player red = null, blue = null;
		if (Settings.CFG.getString("redtype").equals("HUMAN"))
			red = new HumanPlayer(rq, size);
		else if (Settings.CFG.getString("redtype").equals("RANDOM"))
			red = new RandomAI(size);
		else
			red = playerLoader.loadClass(Settings.CFG.getString("redtype"), new Object[] { new Integer(size) });

		if (Settings.CFG.getString("bluetype").equals("HUMAN"))
			blue = new HumanPlayer(rq, size);
		else if (Settings.CFG.getString("bluetype").equals("RANDOM"))
			blue = new RandomAI(size);
		else
			blue = playerLoader.loadClass(Settings.CFG.getString("bluetype"), new Object[] { new Integer(size) });

		if (mp != null) {
			/* Erzeuge GUI */
			Gui g = new Gui(1);
			g.addPanel(mp);
			g.startRefresh();
		}

		for (int i = 0; i < Settings.CFG.getInt("games"); i++) {
			String matchName = null;
			if (i % 2 == 0)
				matchName = Settings.CFG.getString("red") + " vs. " + Settings.CFG.getString("blue") + " (Game " + (i + 1) + ")";
			else
				matchName = Settings.CFG.getString("blue") + " vs. " + Settings.CFG.getString("red") + " (Game " + (i + 1) + ")";

			/* Erzeuge Match */
			Match m = new Match((i % 2 == 0 ? red : blue), (i % 2 == 0 ? blue : red), Settings.CFG.getString(i % 2 == 0 ? "red" : "blue"),
					Settings.CFG.getString(i % 2 == 0 ? "blue" : "red"), (mp != null ? mp : new AsciiBoard()), size, null);

			m.startMatch();
			m.waitMatchEnd();

			if (Settings.CFG.is("save"))
				m.saveGame(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_" + matchName, matchName);

			/* 3 Sekunden Timeout bis zum naechsten Spiel */
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (Settings.CFG.is("log")) {
			/* Speichere Logdatei */
			l.save(FileAccess.createFile("log/" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt"));
		}

		/* Speichere Einstellungen */
		Settings.CFG.save();
	}

}
