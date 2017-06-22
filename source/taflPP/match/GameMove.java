package taflPP.match;

import taflPP.preset.Move;
import taflPP.preset.Status;

/**
 * Spielzug wird in dieser Datenstruktur abgelegt
 * 
 * @author Dominick Leppich
 *
 */
public class GameMove {
	private Move move;
	private long time;
	private Status status;

	// ------------------------------------------------------------

	public GameMove(Move move, long time, Status status) {
		this.move = move;
		this.time = time;
		this.status = status;
	}

	// ------------------------------------------------------------

	/**
	 * Gib Zug zur&uuml;ck
	 * 
	 * @return Zug
	 */
	public Move getMove() {
		return move;
	}

	/**
	 * Gib Zeit zur&uuml;ck
	 * 
	 * @return Zeit
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Gib Status zur&uuml;ck
	 * 
	 * @return Status
	 */
	public Status getStatus() {
		return status;
	}
}