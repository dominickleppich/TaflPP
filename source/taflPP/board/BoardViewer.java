package taflPP.board;

import taflPP.preset.Status;
import taflPP.preset.Viewer;

/**
 * Viewer des Spielbretts erm&ouml;glicht anderen Klassen alle f&uuml;r das Anzeigen erforderliche Informationen vom Board abzufragen. Dazu
 * geh&ouml;ren:
 * <ul>
 * <li>Spielbrettgr&ouml;&szlig;e</li>
 * <li>Spieler der aktuell an der Reihe ist</li>
 * <li>Informationen &uuml;ber jedes Feld des Bretts (Spielstein und Typ)</li>
 * <li>Status des Spielbretts</li>
 * </ul>
 * 
 * @author Dominick Leppich
 *
 */
public class BoardViewer implements Viewer<Field> {
	private Board board;

	// ------------------------------------------------------------

	/**
	 * Erzeugt einen BoardViewer mit Grundlage eines Spielbretts
	 * 
	 * @param board
	 *          Spielbrett
	 */
	public BoardViewer(Board board) {
		this.board = board;
	}

	// ------------------------------------------------------------

	/**
	 * Ist ROT an der Reihe?
	 * 
	 * @return ROT an der Reihe?
	 */
	@Override public boolean isRed() {
		return board.isRed();
	}

	/**
	 * Gibt die Spielbrettgr&ouml;&szlig;e zur&uuml;ck
	 * 
	 * @return Spielbrettgr&ouml;&szlig;e
	 */
	@Override public int getSize() {
		return board.getSize();
	}

	/**
	 * Gibt ein bestimmtes Feld zur&uuml;ck, aber nur eine Kopie des Feldes, damit keine Zugriffe auf das echte Feld des Boards m&ouml;glich sind.
	 * 
	 * @param letter
	 *          Buchstabe
	 * @param number
	 *          Nummer
	 * @return Feld
	 */
	@Override public Field getSquare(int letter, int number) {
		return new Field(board.getField(letter, number));
	}

	/**
	 * Gibt den Status des Spielbretts zur&uuml;ck
	 * 
	 * @return Status
	 */
	@Override public Status getStatus() {
		return board.getStatus();
	}

}
