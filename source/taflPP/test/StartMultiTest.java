package taflPP.test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import taflPP.gui.Gui;
import taflPP.gui.MatchPanel;
import taflPP.match.Match;
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
public class StartMultiTest {

	public static void main(String[] args) {
		int size = 17;
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
		Language.load();

		/* Lade Grafiken */
		Graphic.GFX.showLoading(true);
		Graphic.GFX.loadSynchron(new File("gfx"), true, "");

		/* Erzeuge Spielpanel */
		int multi = 9;
		MatchPanel[] mp = new MatchPanel[multi];
		for (int i = 0; i < multi; i++)
			mp[i] = new MatchPanel();
		
		Match[] m = new Match[multi];

		
		/* Erzeuge Spieler */
		for (int i = 0; i < multi; i++) {
			RandomAI r1 = new RandomAI(size);
			RandomAI r2 = new RandomAI(size);
			m[i] = new Match(r1, r2, "PC", "PC", mp[i], size, null);
			
		}

		/* Erzeuge GUI */
		Gui g = new Gui(multi);
		for (int i = 0; i < multi; i++)
			g.addPanel(mp[i]);
		g.startRefresh();

		for (int i = 0; i < multi; i++)
			m[i].startMatch();
		
		for (int i = 0; i < multi; i++)
			m[i].waitMatchEnd();

		if (Settings.CFG.is("log")) {
			/* Speichere Logdatei */
			l.save(FileAccess.createFile("log/" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt"));
		}

		/* Speichere Einstellungen */
		Settings.CFG.save();
	}

}
