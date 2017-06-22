package taflPP.test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import taflPP.gui.Gui;
import taflPP.gui.MatchPanel;
import taflPP.match.Match;
import taflPP.player.HumanPlayer;
import taflPP.player.ai.RandomAI;
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
public class StartQuadraPanel {

	public static void main(String[] args) {
		int size = 9;
		/* Lade Einstellungen */
		Settings.CFG.load();
		Settings.CFG.setSetNotFoundAction(Settings.CREATE_SETTING);
		Settings.CFG.set("debug", false);
		Settings.CFG.set("log", false);
		Settings.CFG.set("save", false);

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
		MatchPanel mp1 = new MatchPanel();
		MatchPanel mp2 = new MatchPanel();
		MatchPanel mp3 = new MatchPanel();
		MatchPanel mp4 = new MatchPanel();

		/* Erzeuge Spieler */
		// Match m = new Match(new HumanPlayer(mp, size), new RandomAI(size), mp, size);
		HumanPlayer h1 = new HumanPlayer(mp1, size);
		HumanPlayer h2 = new HumanPlayer(mp2, size);
		HumanPlayer h3 = new HumanPlayer(mp3, size);
		
		RandomAI r1 = new RandomAI(size);
		RandomAI r2 = new RandomAI(size);
		RandomAI r3 = new RandomAI(size);
		
		Match m1 = new Match(h1, h2, "Human I", "Human II", mp1, size, null);
		Match m2 = new Match(h3, r1, "Human III", "Random I", mp3, size, null);
		Match m3 = new Match(r2, r3, "Random II", "Random III", mp4, size, null);
		mp2.setViewer(h2.viewer());
		h2.addBoardObserver(mp2);

		/* Erzeuge GUI */
		Gui g = new Gui(4);
		g.addPanel(mp1);
		g.addPanel(mp2);
		g.addPanel(mp3);
		g.addPanel(mp4);
		g.startRefresh();

		m1.startMatch();
		m2.startMatch();
		m3.startMatch();
		m1.waitMatchEnd();
		m2.waitMatchEnd();
		m3.waitMatchEnd();
		if (Settings.CFG.is("save")) {
			m1.saveGame(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()), "Human I vs. Human II (Spiel 1)");
			m2.saveGame(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()), "Human III vs. Random I (Spiel 1)");
			m3.saveGame(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()), "Random II vs. Random III (Spiel 1)");
		}

		if (Settings.CFG.is("log")) {
			/* Speichere Logdatei */
			l.save(FileAccess.createFile("log/" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt"));
		}

		/* Speichere Einstellungen */
		Settings.CFG.save();
	}

}
