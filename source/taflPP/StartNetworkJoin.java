package taflPP;

import java.io.File;
import java.rmi.RemoteException;

import javax.swing.JOptionPane;

import taflPP.gui.Gui;
import taflPP.gui.MatchPanel;
import taflPP.net.MultiplayerSetup;
import taflPP.net.NetPlayer;
import taflPP.player.HumanPlayer;
import eu.nepster.toolkit.gfx.objects.Graphic;
import eu.nepster.toolkit.io.IO;
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
public class StartNetworkJoin {

	public static void main(String[] args) {
		int size = 9;
		/* Lade Einstellungen */
		Settings.CFG.load();
		Settings.CFG.setSetNotFoundAction(Settings.CREATE_SETTING);
		Settings.CFG.set("debug", true);

		/* Lade Argumente */
		ArgumentParser.parse(args, Settings.CFG);

		if (Settings.CFG.is("debug"))
			IO.register(new SystemOut(), IO.LEVEL_DEBUG, false, true, false);
		IO.register(new SystemOut(), IO.LEVEL_NORMAL, false, false, false);

		/* Lade Sprachen */
		Language.load();

		/* Lade Grafiken */
		Graphic.GFX.showLoading(true);
		Graphic.GFX.loadSynchron(new File("gfx"), true, "");

		/* Erzeuge Spielpanel */
		MatchPanel mp = new MatchPanel();

		/* Erzeuge ein Match */
		HumanPlayer human = new HumanPlayer(mp, size);
		NetPlayer net = null;
		try {
			net = new NetPlayer(human);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		String host = JOptionPane.showInputDialog("Geben Sie die IP-Adresse des Servers ein: ");
		
		mp.setViewer(human.viewer());
		human.addBoardObserver(mp);
		/* Erzeuge GUI */
		Gui g = new Gui(1);
		g.setTitle("Client");
		g.addPanel(mp);
		g.startRefresh();

		while (!MultiplayerSetup.offer(host, 1337, "net", net)) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		/* Speichere Einstellungen */
		Settings.CFG.save();
	}

}
