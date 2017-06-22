package taflPP.board;

import taflPP.preset.Move;
import taflPP.preset.Square;
import taflPP.preset.Viewer;
import eu.nepster.toolkit.io.IO;

/**
 * Klasse, die eine Ascii-Repr&auml;sentation des Spielbretts liefert. Das Spielbrett wird wie folgt repr&auml;sentiert:
 * 
 * <ul>
 * <li>Roter Stein: +</li>
 * <li>Blauer Stein: -</li>
 * <li>K&ouml;nig: K</li>
 * <li>Burg: #</li>
 * <li>Thron: T</li>
 * </ul>
 * 
 * @author Dominick Leppich
 *
 */
public class AsciiBoard implements BoardObserver {
	/* Viewer des anzuzeigenden Spielbretts */
	private Viewer<Field> viewer;

	// ------------------------------------------------------------

	@Override public void setViewer(Viewer<Field> viewer) {
		this.viewer = viewer;
	}
	
	/**
	 * Schreibe Spielbrett als Ascii-Grafik auf die Konsole
	 * 
	 * @param move
	 *          gemachter Zug
	 */
	@Override public void update(Move move) {
		IO.println(getAsciiBoard(viewer));
	}

	// ------------------------------------------------------------

	/**
	 * Liefert die Ascii-Repr&auml;sentation des Spielbretts mit Beschriftung &uuml;ber den Viewer
	 * 
	 * @param v
	 *          Viewer
	 * @return Ascii-Repr&auml;sentation
	 */
	public static String getAsciiBoard(Viewer<Field> v) {
		return getAsciiBoard(v, true);
	}

	/**
	 * Liefert die Ascii-Repr&auml;sentation des Spielbretts &uuml;ber den Viewer. Wahlweise mit Beschriftung oder ohne.
	 * 
	 * @param v
	 *          Viewer
	 * @param captions
	 *          Beschriftung an/aus
	 * @return Ascii-Repr&auml;sentation
	 */
	public static String getAsciiBoard(Viewer<Field> v, boolean captions) {
		String s = "\n";

		/* Zeige das Spielbrett als ASCII-Grafik an */
		s += (captions ? "  " + (v.getSize() > 9 ? " " : "") : "");
		for (int number = 0; number < v.getSize(); number++)
			s += "--";
		s += "-\n";

		for (int number = v.getSize() - 1; number >= 0; number--) {
			if (captions)
				s += (v.getSize() > 9 ? (number < 9 ? " " : "") : "") + (number + 1) + " ";
			s += "|";
			for (int letter = 0; letter < v.getSize(); letter++) {
				char symbol = '\0';
				switch (v.getSquare(letter, number).getLabel()) {
					case Square.CASTLE:
						symbol = '#';
						break;
					case Square.THRONE:
						symbol = 'T';
						break;
					default:
						symbol = ' ';
				}
				switch (v.getSquare(letter, number).getMan()) {
					case Square.RED:
						symbol = '+';
						break;
					case Square.BLUE:
						symbol = '-';
						break;
					case Square.KING:
						symbol = 'K';
						break;
					default:
						if (symbol == '\0')
							symbol = ' ';
				}

				s += symbol;

				if (letter != v.getSize() - 1)
					s += " ";
			}
			s += "|\n";
		}

		s += (captions ? "  " + (v.getSize() > 9 ? " " : "") : "");
		for (int number = 0; number < v.getSize(); number++)
			s += "--";
		s += "-\n";

		if (captions) {
			s += (captions ? "  " + (v.getSize() > 9 ? " " : "") : "") + " ";
			for (int letter = 0; letter < v.getSize(); letter++)
				s += (char) ('A' + letter) + " ";
			s += "\n";
		}

		return s;
	}
}
