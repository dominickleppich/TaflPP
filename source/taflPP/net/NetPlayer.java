package taflPP.net;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import taflPP.preset.Move;
import taflPP.preset.Player;
import taflPP.preset.Status;

/**
 * Spieler der mit dem Netzwerk kommunizieren kann.
 * 
 * @author Dominick Leppich
 *
 */
public class NetPlayer extends UnicastRemoteObject implements Player {
	private static final long serialVersionUID = 1L;
	private Player player;

	// ------------------------------------------------------------

	/**
	 * Erzeuge NetPlayer mit einem vorhandenen PlayerObjekt
	 * 
	 * @param player
	 *          Player
	 * @throws RemoteException
	 *           Verbindungsfehler
	 */
	public NetPlayer(Player player) throws RemoteException {
		this.player = player;
	}

	/**
	 * Fordere einen Zug an
	 * 
	 * @return Zug
	 */
	@Override public Move request() throws Exception, RemoteException {
		return player.request();
	}

	/**
	 * Best&auml;tige einen gemachten Zug vom Spiel
	 * 
	 * @param boardStatus
	 *          Status des Zuges
	 */
	@Override public void confirm(Status boardStatus) throws Exception, RemoteException {
		player.confirm(boardStatus);
	}

	/**
	 * Informiere den Spieler &uuml;ber den gemachten Zug des Gegners
	 * 
	 * @param opponentMove
	 *          Zug des Gegners
	 * @param boardStatus
	 *          Status des gegnerischen Zugs
	 */
	@Override public void update(Move opponentMove, Status boardStatus) throws Exception, RemoteException {
		player.update(opponentMove, boardStatus);
	}

	/**
	 * Setze den Spieler zur&uuml;ck
	 * 
	 * @param man
	 *          Farbe des Spielers
	 */
	@Override public void reset(int man) throws Exception, RemoteException {
		player.reset(man);
	}
}
