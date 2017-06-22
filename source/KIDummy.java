import java.util.Random;

import taflPP.player.AbstractPlayer;
import taflPP.preset.Move;
import taflPP.preset.Player;

/**
 * <p>
 * Das ist der Prototyp f&uuml;r deine KI. Du solltest nicht von dieser Klasse erben sondern sie einfach anpassen und nacher umbenennen. Das fertige
 * Projekt wird den KIDummy n&auml;mhlich nicht enthalten.
 * </p>
 * 
 * <p>
 * Den Zug musst du als Move Objekt zur&uuml;ckgeben. Ein Move besteht aus einer Start- und Endposition. Eine Position wiederum aus einem Buchstaben
 * und einer Nummer. So k&ouml;nntest du einen Move erzeugen:
 * <ul>
 * <li>Position start = new Position(0, 3); // A == 0, 3 == 4 (Die Positionen beginnen bei 0 zu z&auml;hlen! Das ist so von Brosenne vorgegeben)</li>
 * <li>Position end = new Positiong(0, 5);</li>
 * <li>Move move = new Move(start, end);</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Die Variable <code>board</code> hat eine Referenz auf dein Board (also das Board der KI, nicht das Board des Spiels). Das kannst du verwenden um
 * deinen Zug zu ermitteln. Allerdings sollst du in deliver() nur den Zug zur&uuml;ckliefern, diesen aber NICHT ausf&uuml;hren. Das Ausf&uuml;hren der
 * korrekten Z&uuml;ge &uuml;bernimmt die Klasse AbstractPlayer, sobald der Zug vom Spiel best&auml;tigt wurde.
 * </p>
 * 
 * <p>
 * Daher musst du nur die deliver-Methode neu schreiben, kannst aber gerne Hilfsfunktionen anlegen. Diese Funktionen des Boards k&ouml;nnten von
 * Nutzen f&uuml;r dich sein:
 * <ul>
 * <li>board.clone() : Erzeugt eine Kopie des Spielbretts. Auf dieser kannst du dann zum Beispiel Z&uuml;ge des Gegners simulieren oder
 * &auml;hnliches. Auf keinen Fall aber auf dem eigenen Board Z&uuml;ge machen</li>
 * <li>board.getField(letter, number) : Gibt ein bestimmtes Feld zur&uuml;ck (vom Typ Field). Das Field kann mittels getMan() oder getLabel() anzeigen
 * welcher Stein darauf steht (Square.NONE, Square.RED, Square.BLUE, Square.KING) oder was f&uuml;r einen Typen das Feld hat (Square.NONE,
 * Square.THRONE, Square.CASTLE)</li>
 * <li>board.getMen(man) : Gibt alle Positionen der M&auml;nner einer bestimmen Farbe zur&uuml;ck (Farben sind Square.RED, Square.BLUE und
 * Square.KING; KING ist in BLUE nicht enthalten)</li>
 * <li>board.getPossibleMoves() : Gibt alle m&ouml;glichen Z&uuml;ge zur&uuml;ck, die der aktuelle Spieler machen kann</li>
 * <li>board.getReachableFields(start) : Gibt alle Felder (als Positionen) zur&uuml;ck, die von dem start-Feld in dieser Spielsituation erreichbar
 * sind</li>
 * <li>board.getSize() : Gr&ouml;&szlig;e des Spielfelds</li>
 * <li>board.getStatus() : Status des Boards</li>
 * <li>board.isRed() : Ist Rot am Zug?</li>
 * <li>board.isValidMove(move) : Ist ein &uuml;bergebener Zug g&uuml;ltig?</li>
 * <li>board.makeMove(move) : F&uuml;hre Zug auf dem Brett aus (nur auf Kopie machen!)</li>
 * </ul>
 * </p>
 * 
 * @author Dominick Leppich
 *
 */
public class KIDummy extends AbstractPlayer implements Player {
	/**
	 * Es muss der Konstruktor von AbstractPlayer aufgerufen werden, damit alle f&uuml;r den Spieler notwendigen Dinge erzeugt werden. Die Methode
	 * bleibt am besten so stehen!
	 * 
	 * @param size
	 *          Boardgr&ouml;&szlig;e
	 */
	public KIDummy(Integer size) {
		super(size);
		init();
	}

	// ------------------------------------------------------------

	/* Hier kommen die Variablen Deklarationen rein */
	// private int temp0; ...

	/**
	 * Alles was deine KI initialisieren muss, kommt hier rein. Also Objekte erzeugen oder dergleichen
	 */
	private void init() {

	}

	/**
	 * Fordert den Zug von der KI an. Du musst darauf achten einen g&uuml;ltigen Zug zu machen. Gibst du <code>null</code> zur&uuml;ck, gilt der Zug als
	 * Aufgabe des Spielers. Ist der Zug nicht g&uuml;ltig, ist das Spiel f&uuml;r die KI wegen illegalem Zug verloren.
	 * 
	 * @return Zug als Move Objekt
	 */
	@Override public Move deliver() throws Exception {
		System.out.println("Dummy Deliver");
		/* Testhalber wird der Zug mal ueber einen InputDialog erfragt */
//		return IOMoveInterpreter.parseMove(JOptionPane.showInputDialog(Language.get("console_make_move")), board.getSize());
		Move[] moves = board.getPossibleMoves();
		return moves[new Random().nextInt(moves.length)];
	}

}
