package taflPP.player;

import java.rmi.RemoteException;

import taflPP.board.Board;
import taflPP.board.BoardObserver;
import taflPP.board.Field;
import taflPP.preset.Move;
import taflPP.preset.Player;
import taflPP.preset.Square;
import taflPP.preset.Status;
import taflPP.preset.Viewer;
import eu.nepster.toolkit.io.IO;

/**
 * Abstrakte Spielerklasse in welcher die Grundfunktionalit&auml;ten implementiert sind.
 * 
 * @author Dominick Leppich
 *
 */
public abstract class AbstractPlayer implements Player {
	public static final int NONE = -1;
	public static final int REQUEST = 0;
	public static final int CONFIRM = 1;
	public static final int UPDATE = 2;

	protected Board board;
	protected int size;
	protected int man;

	private int expectedCall;

	private Move lastMove;

	// ------------------------------------------------------------

	/**
	 * Erzeugt einen Spieler und &uuml;bergibt ihm die Spielfeldgr&ouml;sse
	 * 
	 * @param size
	 *          Spielfeldgr&ouml;sse
	 */
	public AbstractPlayer(Integer size) {
		setBoard(new Board(size));
		this.size = size;
	}

	/**
	 * Erzeuge einen Spieler und &uuml;bergib ihm das Spielbrett
	 * 
	 * @param board
	 *          Spielbrett
	 */
	public AbstractPlayer(Board board) {
		setBoard(board);
		this.size = board.getSize();
	}

	// ------------------------------------------------------------

	/**
	 * Setze das Spielerboard. Ist n&ouml;tig um dem Spieler nach dem Laden ein ver&auml;ndertes Spielerboard zu geben
	 * 
	 * @param board
	 *          Spielbrett
	 */
	public void setBoard(Board board) {
		this.board = board;
	}

	/**
	 * F&uuml;gt dem Player-Board einen Observer hinzu
	 * 
	 * @param observer
	 *          Board-Observer
	 */
	public void addBoardObserver(BoardObserver observer) {
		board.addObserver(observer);
	}

	/**
	 * Gibt einen Viewer auf das Player-Board zur&uuml;ck
	 * 
	 * @return Viewer
	 */
	public Viewer<Field> viewer() {
		return board.viewer();
	}

	/**
	 * Setze die Spielerfarbe
	 * 
	 * @param man
	 *          Farbe des Spielers
	 */
	public void setMan(int man) {
		this.man = man;

		if (man == Square.RED)
			expectedCall = REQUEST;
		else
			expectedCall = UPDATE;
	}

	// ------------------------------------------------------------

	/**
	 * Fordert einen Zug an, dieser wird von <code>deliver</code> geliefert
	 * 
	 * @return Zug
	 */
	@Override public Move request() throws Exception, RemoteException {
		IO.debugln("Player " + this + " REQUEST @ AbstractPlayer.request");
		if (expectedCall == NONE)
			throw new PlayerException("Spieler muss zuerst resetted werden!");
		if (expectedCall != REQUEST)
			throw new PlayerException("Falsche Aufrufreihenfolge der Player-Funktionen, erwartet: REQUEST");

		lastMove = deliver();
		IO.debugln("Got move " + lastMove + " from player @ AbstractPlayer.request");

		/* Erwarte als naechstes den folgenden Aufruf */
		expectedCall = (expectedCall + 1) % 3;
		return lastMove;
	}

	/**
	 * Fordert den Zug vom Spieler an, muss &uuml;berschrieben werden
	 * 
	 * @return Zug
	 * @throws Exception
	 *           Fehler
	 */
	public abstract Move deliver() throws Exception;

	/**
	 * Best&auml;tigt einen gemachten Zug vom Spiel, dieser wird dann auf dem eigenen Brett ausgef&uuml;hrt
	 * 
	 * @param boardStatus
	 *          des Zuges vom Spiel
	 */
	@Override public void confirm(Status boardStatus) throws Exception, RemoteException {
		IO.debugln("Player " + this + " CONFIRM @ AbstractPlayer.confirm");
		if (expectedCall == NONE)
			throw new PlayerException("Spieler muss zuerst resetted werden!");
		if (expectedCall != CONFIRM)
			throw new PlayerException("Falsche Aufrufreihenfolge der Player-Funktionen, erwartet: CONFIRM");

		/* Fuehre letzten eigenen Zug aus */
		board.makeMove(lastMove);

		/* Vergleiche den Status */
		if ( !board.getStatus().equals(boardStatus))
			throw new PlayerException("Boardstatus des Spiels " + boardStatus + " und Status des eigenen Player-Boards " + board.getStatus()
					+ " stimmen nicht ueberein");

		/* Erwarte als naechstes den folgenden Aufruf */
		expectedCall = (expectedCall + 1) % 3;
	}

	/**
	 * Liefert den Zug des Gegners und den Status seines Zuges
	 * 
	 * @param opponentMove
	 *          Gegnerischer Zug
	 * @param boardStatus
	 *          Status dieses Zuges
	 */
	@Override public void update(Move opponentMove, Status boardStatus) throws Exception, RemoteException {
		IO.debugln("Player " + this + " UPDATE @ AbstractPlayer.update");
		if (expectedCall == NONE)
			throw new PlayerException("Spieler muss zuerst resetted werden!");
		if (expectedCall != UPDATE)
			throw new PlayerException("Falsche Aufrufreihenfolge der Player-Funktionen, erwartet: UPDATE");

		/* Fuehre gegnerischen Zug aus */
		board.makeMove(opponentMove);

		/* Vergleiche den Status */
		if ( !board.getStatus().equals(boardStatus))
			throw new PlayerException("Boardstatus des Spiels " + boardStatus + " und Status des eigenen Player-Boards " + board.getStatus()
					+ " stimmen nicht ueberein");

		/* Erwarte als naechstes den folgenden Aufruf */
		expectedCall = (expectedCall + 1) % 3;
	}

	/**
	 * Setze den Spieler zur&uuml;ck und lasse ihn mit einer neuen Farbe spielen
	 * 
	 * @param man
	 *          neue Spielerfarbe
	 */
	@Override public void reset(int man) throws Exception, RemoteException {
		setMan(man);

		board.reset();

		IO.debugln("Resetting player " + this + " @ AbstractPlayer.reset");
	}
}
