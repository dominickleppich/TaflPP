package taflPP.net;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import taflPP.preset.Player;
import eu.nepster.toolkit.io.IO;

/**
 * Diese Klasse regelt die Netzwerkaktivit&auml;ten des Spiels. Es kann eine RMI Registry starten, Player anmelden oder angemeldete Player Referenzen
 * holen
 * 
 * @author Dominick Leppich
 *
 */
public class MultiplayerSetup {
	/**
	 * Starte RMI-Registry an einem bestimmten Port
	 * 
	 * @param port
	 *          Port
	 */
	public static void startRMIRegistry(int port) {
		try {
			LocateRegistry.createRegistry(port);
		} catch (RemoteException e) {
			IO.errorln("Unable to start RMI registry on port " + port + " @ MultiplayerSetup.startRMIRegistry");
			e.printStackTrace();
		}
	}

	// ------------------------------------------------------------

	/**
	 * Melde einen Player an der RMI-Registry an
	 * 
	 * @param host
	 *          Host
	 * @param port
	 *          Port
	 * @param name
	 *          Name, unter dem der Spieler angeboten werden soll
	 * @param player
	 *          Netzwerkspieler
	 * @return Anmelden erfolgreich
	 */
	public static boolean offer(String host, int port, String name, Player player) {
		try {
			Naming.rebind("rmi://" + host + ":" + port + "/" + name, player);
			IO.debugln("Player " + name + " offered at host " + host + " @MultiplayerSetup.offer");
			return true;
		} catch (MalformedURLException e) {
			IO.errorln("Cannot offer player " + name + " at host " + host + ", bad url @ MultiplayerSetup.offer");
			e.printStackTrace();
		} catch (RemoteException e) {
			IO.errorln("Cannot offer player " + name + " at host " + host + ", RemoteException @ MultiplayerSetup.offer");
			e.printStackTrace();
		} catch (Exception e) {
			IO.errorln("Cannot offer player " + name + " at host " + host + ", Exception @ MultiplayerSetup.offer");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Finde einen Player im Netzwerk
	 * 
	 * @param host
	 *          Host
	 * @param port
	 *          Port
	 * @param name
	 *          Name, unter dem der Spieler angeboten wurde
	 * @return Player
	 */
	public static Player find(String host, int port, String name) {
		Player p = null;
		try {
			p = (Player) Naming.lookup("rmi://" + host + ":" + port + "/" + name);
			IO.debugln("Found player " + name + " on host " + host + " @MultiplayerSetup.find");
		} catch (NotBoundException e) {
			IO.errorln("Player " + name + " not bound at server " + host + " @ MultiplayerSetup.find");
			e.printStackTrace();
		} catch (Exception e) {
			IO.errorln("Error finding player @ MultiplayerSetup.find");
			e.printStackTrace();
		}
		return p;
	}

	/**
	 * Gib eine Liste aller angebotenen Spieler zur&uuml;ck
	 * 
	 * @param host
	 *          Host
	 * @param port
	 *          Port
	 * @return Array von angebotenen Spielern
	 */
	public static String[] list(String host, int port) {
		try {
			return Naming.list("rmi://" + host + ":" + port);
		} catch (RemoteException e) {
			IO.errorln("Cannot list registered players at host " + host + ", RemoteException @ MultiplayerSetup.list");
			e.printStackTrace();
		} catch (MalformedURLException e) {
			IO.errorln("Cannot list registered players at host " + host + ", bad url @ MultiplayerSetup.list");
			e.printStackTrace();
		}
		return null;
	}
}
