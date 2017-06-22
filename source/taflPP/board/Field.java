package taflPP.board;

import taflPP.preset.Square;

/**
 * <h1>Feld des Spielbretts</h1> Es k&ouml;nnen Spielfiguren sowie Feldtyp gesetzt oder abgefragt werden.
 * 
 * <h2>Spielfiguren</h2> M&ouml;gliche Spielfiguren sind:
 * <ul>
 * <li>Square.NONE (Kein Spielstein)</li>
 * <li>Square.RED (Roter Spielstein)</li>
 * <li>Square.BLUE (Blauer Spielstein)</li>
 * <li>Square.KING (Blauer K&ouml;nig)</li>
 * </ul>
 * Spielfiguren werden &uuml;ber die Methoden <code>setMan(man)</code> und <code>getMan()</code> gesetzt oder erfragt.
 * 
 * <h2>Feldtyp</h2> M&ouml;gliche Feldtypen sind:
 * <ul>
 * <li>Square.NONE (Normales Feld)</li>
 * <li>Square.CASTLE (Burg)</li>
 * <li>Square.THRONE (Thron)</li>
 * </ul>
 * Feldtypen k&ouml;nnen &uuml;ber die Methoden <code>setLabel(label)</code> und <code>getLabel()</code> gesetzt oder erfragt werden. Feldtypen
 * &auml;ndern sich im normalen Spielverlauf jedoch nicht mehr!
 * 
 * @author Dominick Leppich
 *
 */
public class Field implements Square {

	private int man;
	private int label;

	// ------------------------------------------------------------

	/**
	 * Default-Konstruktor legt ein normales leeres Feld an
	 */
	public Field() {
		this(NONE, NONE);
	}

	/**
	 * Copy Konstruktor &uuml;bernimmt alle Werte des Feldes f
	 * 
	 * @param f
	 *          Feld
	 */
	public Field(Field f) {
		this(f.man, f.label);
	}

	/**
	 * Erzeuge ein Feld und setzt die Spielfigur und den Feldtyp
	 * 
	 * @param man
	 *          Spielfigur
	 * @param label
	 *          Feldtyp
	 */
	public Field(int man, int label) {
		this.man = man;
		this.label = label;
	}

	// ------------------------------------------------------------

	/**
	 * Gibt den Spielstein auf dem Feld zur&uuml;ck
	 * 
	 * @return Spielstein
	 */
	@Override public int getMan() {
		return man;
	}

	/**
	 * Setzt einen Spielstein auf das Feld
	 * 
	 * @param man
	 *          Spielstein
	 */
	@Override public void setMan(int man) {
		if (man != NONE && man != RED && man != BLUE && man != KING)
			throw new IllegalArgumentException("Keine gueltige Spielfigur!");

		this.man = man;
	}

	/**
	 * Gibt den Feldtyp zur&uuml;ck
	 * 
	 * @return Feldtyp
	 */
	@Override public int getLabel() {
		return label;
	}

	/**
	 * Setzt den Feldtyp
	 * 
	 * @param label Feldtyp
	 */
	@Override public void setLabel(int label) {
		if (label != NONE && label != THRONE && label != CASTLE)
			throw new IllegalArgumentException("Kein gueltiger Feldtyp!");

		this.label = label;
	}

	// ------------------------------------------------------------

	/**
	 * Klont das aktuelle Feld und gibt eine Kopie zur&uuml;ck
	 * 
	 * @return Kopie des Feldes
	 */
	public Field clone() {
		return new Field(this);
	}

	/**
	 * Pr&uuml;ft ob zwei Felder gleich sind (ob gleicher Wert und gleicher Typ)
	 * 
	 * @return Felder gleich
	 */
	@Override public boolean equals(Object o) {
		if ( !(o instanceof Field))
			return false;
		Field f = (Field) o;

		return man == f.man && label == f.label;
	}
}
