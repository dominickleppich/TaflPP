package taflPP.board;

import java.util.LinkedList;
import java.util.Vector;

import taflPP.preset.Move;
import taflPP.preset.Position;
import taflPP.preset.Square;
import taflPP.preset.Status;
import taflPP.preset.Viewable;
import taflPP.preset.Viewer;
import eu.nepster.toolkit.io.IO;

/**
 * <h1>Spielbrett f&uuml;r TaflPP</h1> Es ist m&ouml;glich ein Spielbrett der Gr&ouml;ssen von 7 bis 19 (nur ungerade Werte) zu erzeugen. Weiterhin
 * muss die Gr&ouml;&szlig;e so gew&auml;hlt werden, dass es eine nat&uuml;rliche Zahl <code>k</code> und ein <code>t = 0/1</code> gibt, sodass
 * <code>n = 2(k(k+1)/2-t)+7</code> gilt. Wird versucht ein Spielbrett mit einer Gr&ouml;&szlig;e zu erstellen die diese Anforderungen nicht
 * erf&uuml;llt, wird eine Exception geworfen.
 * 
 * @author Dominick Leppich
 *
 */
public class Board extends BoardObservable implements Viewable<Field> {
	public static final int MIN_SIZE = 7;
	public static final int MAX_SIZE = 19;

	/* Spielbrett, Referenzen auf Spielfelder */
	private Field[][] board;
	private int size;

	/* Welcher Spieler zieht als naechstes */
	private int currentPlayer;

	/* Status des Spielbretts */
	private int status;

	/* Liste aller gemachten Zuege */
	private LinkedList<Move> lastMoves;

	/* Zaehlt vor wie vielen Zuegen der letzte Stein geschlagen wurde */
	private int kickCount;

	// ------------------------------------------------------------

	/**
	 * Erzeugt ein neues Brett der Gr&ouml;&szlig;e <code>size</code>
	 * 
	 * @param size
	 *          Brettgr&ouml;&szlig;e
	 */
	public Board(int size) {
		super();

		IO.debugln("New Board created, size " + size + " @ Board.Board");

		if (size < MIN_SIZE || size > MAX_SIZE || (size % 2) != 1)
			throw new IllegalArgumentException("Dimension muss ungerade Zahl zwischen " + MIN_SIZE + " und " + MAX_SIZE + " sein!");

		this.size = size;

		reset();
	}

	/**
	 * Copy-Konstruktor erzeugt ein neues Board nach Vorgabe eines &uuml;bergebenen
	 * 
	 * @param b
	 *          Vorlage Brett
	 */
	public Board(Board b) {
		super();

		IO.debugln("New Copy Board created, size " + b.size + " @ Board.Board");

		this.size = b.size;
		if (size < MIN_SIZE || size > MAX_SIZE || (size % 2) != 1)
			throw new IllegalArgumentException("Dimension muss ungerade Zahl zwischen " + MIN_SIZE + " und " + MAX_SIZE + " sein!");

		this.currentPlayer = b.currentPlayer;
		this.kickCount = b.kickCount;
		this.lastMoves = new LinkedList<Move>();
		for (Move m : b.lastMoves)
			this.lastMoves.add(m);
		this.status = b.status;
		board = new Field[size][size];

		/* Kopiere Eintraege */
		for (int letter = 0; letter < size; letter++)
			for (int number = 0; number < size; number++)
				board[letter][number] = b.board[letter][number].clone();
	}

	// ------------------------------------------------------------

	/**
	 * Setzt das Board zur&uuml;ck. Hierzu werden alle Felder entfernt, neu erzeugt und mit der Startaufstellung initialisiert. Der Spieler wird wieder
	 * auf Rot gesetzt, der Status ist OK und die Liste der bereits gemachten Z&uuml;ge ist leer. Es wurden keine Steine geschlagen.
	 */
	public void reset() {
		board = new Field[size][size];

		/* Setze Felder auf Startposition */
		initStartPosition();

		currentPlayer = Square.RED;
		status = Status.OK;
		lastMoves = new LinkedList<Move>();
		kickCount = 0;
		
		changed();
	}

	/**
	 * Setzt die Startposition
	 */
	private void initStartPosition() {
		IO.debugln("Board fields resetted @ Board.resetFields");

		for (int letter = 0; letter < size; letter++)
			for (int number = 0; number < size; number++)
				board[letter][number] = new Field();

		/* Setze Burgen */
		board[0][0].setLabel(Square.CASTLE);
		board[0][size - 1].setLabel(Square.CASTLE);
		board[size - 1][0].setLabel(Square.CASTLE);
		board[size - 1][size - 1].setLabel(Square.CASTLE);

		/* Setze Thron in die Mitte */
		board[size / 2][size / 2].setLabel(Square.THRONE);

		/* Setze Koenig auf den Thron */
		board[size / 2][size / 2].setMan(Square.KING);

		/* Fuer n = 15 gibt es kein k und t */
		int n = size;

		int tmp = (n - 7) / 2;
		int t = -1, k = -1;
		for (int tempK = 1; tempK <= size; tempK++) {
			if (tmp == (tempK * (tempK + 1)) / 2 - 1) {
				t = 1;
				k = tempK;
			} else if (tmp == (tempK * (tempK + 1)) / 2) {
				t = 0;
				k = tempK;
			}
		}

		/* Wenn es kein k gibt, ist n nicht gueltig */
		if (k == -1)
			throw new IllegalArgumentException("Dieses n ist nicht gueltig!");

		// System.out.println("n = " + n + ", t = " + t + ", k = " + k);

		/* Setze Steine */
		for (int letter = 0; letter < k; letter++) {
			for (int number = 0; number < k; number++) {
				if (letter + number < k) {
					/* Blaue Steine */
					board[(n - 1) / 2 - letter - 1][(n - 1) / 2 - number].setMan(Square.BLUE);
					board[(n - 1) / 2 - letter][(n - 1) / 2 + number + 1].setMan(Square.BLUE);
					board[(n - 1) / 2 + letter + 1][(n - 1) / 2 + number].setMan(Square.BLUE);
					board[(n - 1) / 2 + letter][(n - 1) / 2 - number - 1].setMan(Square.BLUE);

					/* Rote Steine - Grosse Dreiecke */
					board[0 + letter][(n - 1) / 2 - number].setMan(Square.RED);
					board[(n - 1) / 2 - letter][size - 1 - number].setMan(Square.RED);
					board[size - 1 - letter][(n - 1) / 2 + number].setMan(Square.RED);
					board[(n - 1) / 2 + letter][0 + number].setMan(Square.RED);
				}
				/* Rote Steine - Kleine Dreiecke */
				if (letter + number < k - 1) {
					board[(n - 1) / 2 - letter - 1][0 + number].setMan(Square.RED);
					board[0 + letter][(n - 1) / 2 + number + 1].setMan(Square.RED);
					board[(n - 1) / 2 + letter + 1][size - 1 - number].setMan(Square.RED);
					board[size - 1 - letter][(n - 1) / 2 - number - 1].setMan(Square.RED);
				}
			}
		}
		/* Blauer Extrastein */
		if (1 - t == 1) {
			board[(n - 1) / 2 - k - 1][(n - 1) / 2].setMan(Square.BLUE);
			board[(n - 1) / 2][(n - 1) / 2 + k + 1].setMan(Square.BLUE);
			board[(n - 1) / 2 + k + 1][(n - 1) / 2].setMan(Square.BLUE);
			board[(n - 1) / 2][(n - 1) / 2 - k - 1].setMan(Square.BLUE);
		}
		/* Roter Extrastein obere Reihe */
		if (k % 2 == 1) {
			board[k][(n - 1) / 2].setMan(Square.RED);
			board[(n - 1) / 2][size - 1 - k].setMan(Square.RED);
			board[size - 1 - k][(n - 1) / 2].setMan(Square.RED);
			board[(n - 1) / 2][k].setMan(Square.RED);
		}
		/* Rest der roten Extrasteine */
		for (int l = 0; l < (k / 2) + 1 - t; l++) {
			board[0][(n - 1) / 2 - k - l].setMan(Square.RED);
			board[0][(n - 1) / 2 + k + l].setMan(Square.RED);

			board[(n - 1) / 2 - k - l][size - 1].setMan(Square.RED);
			board[(n - 1) / 2 + k + l][size - 1].setMan(Square.RED);

			board[size - 1][(n - 1) / 2 - k - l].setMan(Square.RED);
			board[size - 1][(n - 1) / 2 + k + l].setMan(Square.RED);

			board[(n - 1) / 2 - k - l][0].setMan(Square.RED);
			board[(n - 1) / 2 + k + l][0].setMan(Square.RED);
		}
	}

	// ------------------------------------------------------------

	/**
	 * Gibt die Spielfeldgr&ouml;&szlig;e zur&uuml;ck
	 * 
	 * @return Gr&ouml;&szlig;e
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Z&auml;hlt die Anzahl der Spielfiguren auf dem Brett (Rote, Blaue und K&ouml;nig)
	 * 
	 * @return Anzahl der Spielfiguren
	 */
	public int getManCount() {
		/* Der Koenig kann nicht geschlagen werden, ist also immer auf dem Brett */
		return getManCount(Square.RED) + getManCount(Square.BLUE) + 1;
	}

	/**
	 * Gibt die Anzahl an Spielfiguren eines bestimmten Typs zur&uuml;ck <code>(Square.RED, Square.BLUE)</code>
	 * 
	 * @param man
	 *          Spielfigurtyp
	 * @return Anzahl dieser Spielfiguren auf dem Brett
	 */
	public int getManCount(int man) {
		/* Wird der falsche Typ gesucht, gibt es solche Steine nicht */
		if (man != Square.RED && man != Square.BLUE)
			return 0;
		int count = 0;
		for (int letter = 0; letter < size; letter++)
			for (int number = 0; number < size; number++)
				if (board[letter][number].getMan() == man)
					count++;
		return count;
	}

	/**
	 * Gib alle Spielsteine einer bestimmten Farbe. Der K&ouml;nig z&auml;hlt hierbei NICHT zu den blauen dazu.
	 * 
	 * @param man
	 *          Spielerfarbe
	 * @return Array von Positionen
	 */
	public Position[] getMen(int man) {
		/* Nur Rot und Blau moeglich */
		if (man != Square.RED && man != Square.BLUE && man != Square.KING)
			return null;

		Vector<Position> p = new Vector<Position>();

		for (int letter = 0; letter < size; letter++)
			for (int number = 0; number < size; number++)
				if (board[letter][number].getMan() == man)
					p.add(new Position(letter, number));

		Position[] p2 = new Position[p.size()];
		p.toArray(p2);
		return p2;
	}

	/**
	 * Pr&uuml;ft ob eine Position eine g&uuml;ltige Feldkoordinate beschreibt, das hei&szlig;t ob das Feld auf dem Brett liegt und die Dimension nicht
	 * &uuml;berschreitet.
	 * 
	 * @param p
	 *          Position
	 * @return Wahr, wenn das Feld auf dem Brett liegt
	 */
	private boolean isFieldOnBoard(Position p) {
		return p.getLetter() >= 0 && p.getLetter() < size && p.getNumber() >= 0 && p.getNumber() < size;
	}

	/**
	 * Pr&uuml;ft ob ein Feld einen bestimmten Wert hat
	 * 
	 * @param p
	 *          Position des Feldes
	 * @param man
	 *          Wert der gepr&uuml;ft werden soll
	 * @return Wahr, wenn Feld dem Wert entspricht
	 */
	private boolean isFieldMan(Position p, int man) {
		return board[p.getLetter()][p.getNumber()].getMan() == man;
	}

	/**
	 * Pr&uuml;ft ob ein Feld einen bestimmten Typ hat
	 * 
	 * @param p
	 *          Position des Feldes
	 * @param label
	 *          Typ der gepr&uuml;ft werden soll
	 * @return Wahr, wenn Feld dem Wert entspricht
	 */
	private boolean isFieldLabel(Position p, int label) {
		return board[p.getLetter()][p.getNumber()].getLabel() == label;
	}

	/**
	 * Gib ein bestimmtes Feld zur&uuml;ck
	 * 
	 * @param letter
	 *          Buchstabe
	 * @param number
	 *          Nummer
	 * @return Feld
	 */
	public Field getField(int letter, int number) {
		return board[letter][number];
	}

	/**
	 * Bestimme die direkten Nachbarn einer Position
	 * 
	 * @param p
	 *          Ausgangsposition
	 * @return Array von Nachbarn
	 */
	private Position[] getNeighbors(Position p) {
		int[] letterSearch = new int[] { 1, 0, -1, 0 };
		int[] numberSearch = new int[] { 0, -1, 0, 1 };

		Vector<Position> positions = new Vector<Position>();

		for (int k = 0; k < 4; k++) {
			Position searchPos = new Position(p.getLetter() + letterSearch[k], p.getNumber() + numberSearch[k]);

			if (isFieldOnBoard(searchPos))
				positions.add(searchPos);
		}

		Position[] p2 = new Position[positions.size()];
		positions.toArray(p2);
		return p2;
	}

	/**
	 * Gibt den aktuellen Status des Spielfeldes zur&uuml;ck
	 * 
	 * @return Status
	 */
	public Status getStatus() {
		return new Status(status);
	}

	/**
	 * Gibt den aktuellen Spieler zur&uuml;ck. In diesem Fall ob ROT an der Reihe ist.
	 * 
	 * @return ROT an der Reihe?
	 */
	public boolean isRed() {
		return currentPlayer == Square.RED;
	}

	/**
	 * Gibt alle g&uuml;ltigen Spielz&uuml;ge des aktuellen Spielers zur&uuml;ck.
	 * 
	 * @return Array der g&uuml;ltigen Z&uuml;ge
	 */
	public Move[] getPossibleMoves() {
		Vector<Move> moves = new Vector<Move>();

		/* Hole Spielsteine des aktuellen Spielers */
		Position[] positions;
		/* Falls Rot am Zug, berechne rote moegliche Zuege */
		if (isRed()) {
			positions = getMen(Square.RED);
			for (Position pStart : positions)
				for (Position pEnd : getReachableFields(pStart, false))
					moves.add(new Move(pStart, pEnd));
		}
		/* Sonst berechne blaue Zuege, diese setzen sich aus blauen Zuegen und dem Zug des Koenigs zusammen */
		else {
			positions = getMen(Square.BLUE);
			for (Position pStart : positions)
				for (Position pEnd : getReachableFields(pStart, false))
					moves.add(new Move(pStart, pEnd));
			positions = getMen(Square.KING);
			for (Position pStart : positions)
				for (Position pEnd : getReachableFields(pStart, true))
					moves.add(new Move(pStart, pEnd));
		}

		Move[] moves2 = new Move[moves.size()];
		moves.toArray(moves2);
		return moves2;
	}

	/**
	 * Gibt einen Vector von Felder (als Positionen) zur&uuml;ck, die von einer Startposition aus in einem Zug erreicht werden k&ouml;nnen.
	 * 
	 * @param start
	 *          Startposition
	 * @param isKing
	 *          Zieht der K&ouml;nig?
	 * @return Array der erreichbaren Felder
	 */
	public Position[] getReachableFields(Position start, boolean isKing) {
		int[] letterDirection = new int[] { 0, 1, 0, -1 };
		int[] numberDirection = new int[] { 1, 0, -1, 0 };

		Vector<Position> positions = new Vector<Position>();

		/* Probiere jede Richtung aus */
		for (int direction = 0; direction < 4; direction++) {
			boolean go = true;
			int steps = 1;
			/* Gehe in jede Richtung soweit moeglich */
			while (go) {
				Position p = new Position(start.getLetter() + letterDirection[direction] * steps, start.getNumber() + numberDirection[direction] * steps);
				/* Nur weiter wenn das Feld auf dem Spielbrett ist */
				if (isFieldOnBoard(p)) {
					/*
					 * Das Feld wird als gueltige Endposition hinzugefuegt wenn es leer ist und entweder ein normales Feld ist oder Thron/Burg und der Koenig
					 * zieht
					 */
					if (isFieldMan(p, Square.NONE) && (isFieldLabel(p, Square.NONE) || isKing) && isValidMove(new Move(start, p)))
						positions.add(p);

					/* Es geht genau dann weiter, wenn das Feld leer ist */
					if (isFieldMan(p, Square.NONE))
						steps++;
					else
						go = false;
				} else
					go = false;
			}
		}

		Position[] positions2 = new Position[positions.size()];
		positions.toArray(positions2);
		return positions2;
	}

	// ------------------------------------------------------------

	/**
	 * Informiere Observer &uuml;ber &Auml;nderungen des Spielbretts und &uuml;bergebe den gemachten Zug.
	 * 
	 * @param o
	 *          Observer
	 */
	@Override public void update(BoardObserver o) {
		IO.debugln("Updating Board observers @ Board.update");
		o.update(lastMoves.size() > 0 ? lastMoves.getLast() : null);
	}

	/**
	 * Gibt einen Viewer auf das Spielbrett zur&uuml;ck
	 * 
	 * @return Viewer
	 */
	@Override public Viewer<Field> viewer() {
		return new BoardViewer(this);
	}

	/**
	 * Gibt eine Kopie des Spielbretts zur&uuml;ck
	 * 
	 * @return Kopie des Bretts
	 */
	public Board clone() {
		return new Board(this);
	}

	/**
	 * Pr&uuml;fe ob die Boards gleich sind (ob alle Steine &uuml;bereinstimmen)
	 * 
	 * @return Bretter gleich
	 */
	@Override public boolean equals(Object o) {
		/* Wenn o kein Board ist, kann es nicht gleich sein */
		if ( !(o instanceof Board))
			return false;
		Board b = (Board) o;

		/* Wenn die Groessen nicht uebereinstimmen, dann sind die Boards nicht gleich */
		if (size != b.getSize())
			return false;

		/* Boards sind gleich, wenn alle Steine uebereinstimmen */
		for (int letter = 0; letter < size; letter++)
			for (int number = 0; number < size; number++)
				if ( !board[letter][number].equals(b.board[letter][number]))
					return false;
		return true;
	}

	// ------------------------------------------------------------

	/* Gewinnsituation Erkennung */
	/**
	 * Pr&uuml;fe ob eine Unentschieden Situation vorliegt. Diese liegt genau dann vor, wenn in den letzten 100 Z&uuml;gen (50 rote und 50 blaue
	 * Z&uuml;ge) kein Mann geschlagen wurde.
	 * 
	 * @return Unentschieden Situation
	 */
	private boolean isDraw() {
		IO.debugln("Checking draw situation @ Board.isDraw");
		int checkLastMovesCount = 100;

		if (kickCount >= checkLastMovesCount) {
			/* Wenn die volle Anzahl an letzten Spielzuegen geprueft wurde und kein Stein geschlagen wurde, ist unentschieden */
			IO.debugln("Draw situation! No man kicked in the last " + checkLastMovesCount + " moves @ Board.isDraw");
			return true;
		} else {
			IO.debugln("No draw situation! man kicked @ Board.isDraw");
			return false;
		}
	}

	/**
	 * Pr&uuml;fe ob der K&ouml;nig gefangen genommen wurde. Das ist dann der Fall wenn der K&ouml;nig auf einem Nachbarfeld der Endposition des Zuges
	 * ist und der K&ouml;nig nach dem Zug entweder:
	 * <ul>
	 * <li>Von 4 roten M&auml;nnern umstellt ist</li>
	 * <li>Von 3 roten M&auml;nnern am Spielfeldrand umstellt ist</li>
	 * <li>Von 3 roten M&auml;nnern und dem Thron umstellt ist</li>
	 * </ul>
	 * 
	 * @param p
	 *          Endpostion des Zuges
	 * @return K&ouml;nig gefangen genommen
	 */
	private boolean isKingInTheBox(Position p) {
		Position king = null;

		/* Pruefe der Koenig auf einem angrenzenden Feld steht */
		for (Position searchPos : getNeighbors(p)) {
			if (board[searchPos.getLetter()][searchPos.getNumber()].getMan() == Square.KING) {
				king = searchPos;
				break;
			}
		}

		if (king != null) {
			/* Wenn Koenig da, pruefe ob er gefangen wurde */
			/* dafuer muss jeder angrenzende Stein des Koenigs entweder ... */
			for (Position searchPos : getNeighbors(king)) {
				/* ... ausserhalb des Spielbretts */
				if ( !isFieldOnBoard(searchPos))
					continue;
				/* ... ein roter Stein */
				else if (isFieldMan(searchPos, Square.RED))
					continue;
				/* ... oder der Thron sein */
				else if (isFieldLabel(searchPos, Square.THRONE))
					continue;
				else
					return false;
			}
		} else
			return false;

		return true;
	}

	/**
	 * Pr&uuml;fe ob der K&ouml;nig auf eine Burg entkommen konnte. Das ist der Fall wenn die Endposition des Zuges eine Burg ist und der K&ouml;nig den
	 * Zug gemacht hat.
	 * 
	 * @param p
	 *          Endposition des Zuges
	 * @return K&ouml;nig in der Burg
	 */
	private boolean isKingInCastle(Position p) {
		Field endField = board[p.getLetter()][p.getNumber()];
		return endField.getMan() == Square.KING && endField.getLabel() == Square.CASTLE;
	}

	/* Zug Aktionen */
	/**
	 * F&uuml;hre einen Zug echt auf dem Brett aus. Setze dazu den Stein auf die Endkoordinate und entferne ihn von der Startkoordinate
	 * 
	 * @param move
	 *          Zug
	 */
	private void doMove(Move move) {
		Position start = move.getStart();
		Position end = move.getEnd();
		int man = board[start.getLetter()][start.getNumber()].getMan();
		IO.debugln("Moving " + (man == Square.RED ? "RED" : (man == Square.KING ? "KING" : "BLUE")) + " from " + start + " to " + end + " @ Board.doMove");
		board[end.getLetter()][end.getNumber()].setMan(man);
		board[start.getLetter()][start.getNumber()].setMan(Square.NONE);
	}

	/**
	 * Schlage alle m&ouml;glichen Steine, die mit der neuen Endposition des Zuges geschlagen werden k&ouml;nnen
	 * 
	 * @param p
	 *          Endposition des Steins
	 */
	private void capture(Position p) {
		int[] letterSearch = new int[] { 1, 0, -1, 0 };
		int[] numberSearch = new int[] { 0, -1, 0, 1 };

		/* Pruefe ob ein roter oder blauer Stein gesetzt wurde, Koenig kann nicht schlagen! */
		int myMan = board[p.getLetter()][p.getNumber()].getMan();
		int oppMan;

		if (myMan == Square.RED)
			oppMan = Square.BLUE;
		else if (myMan == Square.BLUE)
			oppMan = Square.RED;
		else
			return;

		boolean captured = false;
		/* Durchsuche die Nachbarschaft nach Gegnerischen Steinen */
		for (int k = 0; k < 4; k++) {
			Position searchPos = new Position(p.getLetter() + letterSearch[k], p.getNumber() + numberSearch[k]);

			/* Wenn ein gegnerischer Stein gefunden wurde, suche nach einer Schlagmoeglichkeit */
			if (isFieldOnBoard(searchPos) && isFieldMan(searchPos, oppMan)) {
				Position myKickMan = new Position(p.getLetter() + letterSearch[k] * 2, p.getNumber() + numberSearch[k] * 2);
				if (isFieldOnBoard(myKickMan)) {
					/* Schlage, wenn der gegnerische Stein von einem eigenen Stein, dem Thron oder einer Burg eingeschlossen ist */
					if (isFieldMan(myKickMan, myMan) || isFieldLabel(myKickMan, Square.CASTLE) || isFieldLabel(myKickMan, Square.THRONE)) {
						IO.debugln("Capturing man at " + p + " @ Board.capture");
						board[searchPos.getLetter()][searchPos.getNumber()].setMan(Square.NONE);
						captured = true;
					}
				}

			}
		}
		/* Wenn geschlagen, setze Schlagzaehler zurueck auf 0, sonst zaehle hoch. Damit kann eine Unentschieden Situation erkannt werden */
		if (captured)
			kickCount = 0;
		else
			kickCount++;
	}

	/* Pruefe Zug auf Gueltigkeit */
	/**
	 * Pr&uuml;ft ob ein Zug g&uuml;ltig ist, dazu m&uuml;ssen folgende Bedingungen erf&uuml;llt sein:
	 * <ul>
	 * <li>Die Startposition muss ein Spielstein des aktuellen Spielers sein</li>
	 * <li>Start und Endposition d&uuml;rfen nicht gleich sein</li>
	 * <li>Der Zug darf entweder nur vertikal oder nur horizontal sein</li>
	 * <li>Die Endposition sowie alle Positionen dazwischen m&uuml;ssen leer sein</li>
	 * <li>Nur der K&ouml;nig darf seinen Zug auf einer Burg oder dem Thron beenden</li>
	 * <li>Ein Stein darf in unmittelbar hintereinander ausgef&uuml;hrten Z&uuml;gen nicht mehr als dreimal zwischen zwei Feldern hin und her ziehen</li>
	 * </ul>
	 * 
	 * @param move
	 *          Zu pr&uuml;fender Zug
	 * @return Zug g&uuml;ltig
	 */
	public boolean isValidMove(Move move) {
		Position start = move.getStart();
		Position end = move.getEnd();

		/*
		 * Start und Endkoordinaten muessen auf dem Spielfeld sein. Falls dies gilt, sind auch alle anderen Steine dazwischen auf dem Feld, da das
		 * Spielfeld konvex ist
		 */
		if ( !isFieldOnBoard(start) || !isFieldOnBoard(end))
			return false;

		/* Start muss zum aktuellen Spieler gehoeren */
		boolean king;
		int startMan = board[start.getLetter()][start.getNumber()].getMan();
		if (currentPlayer == Square.RED) {
			if (startMan != Square.RED)
				return false;
			king = false;
		} else {
			/* Ist der Koenig am Gange? */
			if (startMan == Square.KING)
				king = true;
			else if (startMan == Square.BLUE)
				king = false;
			else
				/* Mann muss vom korrekten Spieler sein */
				return false;
		}

		/* Start und Ende muessen verschieden sein */
		if (start.getLetter() == end.getLetter() && start.getNumber() == end.getNumber())
			return false;

		/* Zug nur vertikal oder horizontal */
		if (start.getLetter() != end.getLetter() && start.getNumber() != end.getNumber())
			return false;

		/* Alle Felder bis zum Endfeld inklusive muessen leer sein */
		/* Vertikal */
		if (start.getLetter() == end.getLetter()) {
			/* Wird nach oben oder unten gegangen? */
			int direction = start.getNumber() < end.getNumber() ? 1 : -1;
			int letter = start.getLetter();
			for (int number = start.getNumber() + direction; number != end.getNumber(); number += direction)
				/* Alle Felder dazwischen muessen leer sein */
				if (board[letter][number].getMan() != Square.NONE)
					return false;
		}
		/* Horizontal */
		else {
			/* Wird nach links oder rechts gegangen? */
			int direction = start.getLetter() < end.getLetter() ? 1 : -1;
			int number = start.getNumber();
			for (int letter = start.getLetter() + direction; letter != end.getLetter(); letter += direction)
				/* Alle Felder dazwischen muessen leer sein */
				if (board[letter][number].getMan() != Square.NONE)
					return false;
		}

		/* Nicht mehr als dreimal hin und herziehen */
		// TODO
		/* Der gemachte Zug wird erst danach zur Liste hinzugefuegt!! */
		if (lastMoves.size() >= 6) {
			Position pStart, pEnd;
			Move m;
			Vector<Position> lastFields = new Vector<Position>();
			/* Fuege Zug Felder hinzu */
			lastFields.add(start);
			lastFields.add(end);
			/* Bestimme letzte 3 betretene Felder */
			for (int i = 0; i < 3; i++) {
				/* letzter Zug gehoert dem Gegner, also weg damit */
				m = lastMoves.get(lastMoves.size() - 2 - 2 * i);
				pStart = m.getStart();
				pEnd = m.getEnd();
				/* Fuege nur neue Felder hinzu */
				boolean add = true;
				for (Position p : lastFields)
					if (p.equals(pStart))
						add = false;
				if (add)
					lastFields.add(pStart);
				add = true;
				for (Position p : lastFields)
					if (p.equals(pEnd))
						add = false;
				if (add)
					lastFields.add(pEnd);
			}
			if (lastFields.size() == 2)
				/* Mann wurde mehr als Dreimal zwischen nur zwei Feldern hin und her gezogen, nicht gueltig */
				return false;
		}

		/* Endfeld leer... */
		if (isFieldMan(end, Square.NONE)) {
			/* .. und normales Feld, dann ist alles okay */
			if (isFieldLabel(end, Square.NONE))
				return true;
			/* .. und Burg oder Thron, dann darf es nur vom Koenig betreten werden */
			if (king && (isFieldLabel(end, Square.CASTLE) || isFieldLabel(end, Square.THRONE)))
				return true;
		}

		/* Alles andere ist nicht gueltig */
		return false;
	}

	/**
	 * <h1>&Uuml;bergebe einen Zug an das Spielbrett</h1>
	 * 
	 * <p>
	 * Ist der Zug g&uuml;ltig, wird er ausgef&uuml;hrt. Danach werden m&ouml;gliche Steine geschlagen und die Gewinnsituationen erkannt, falls sie
	 * vorliegen. Die Spielsituation kann nach dem Zug mit <code>getStatus()</code> erfragt werden. Folgende Situationen sind m&ouml;glich:
	 * </p>
	 * <ul>
	 * <li><code>REDWIN</code>: Der rote Spieler hat das Spiel gewonnen. Folgende Situationen f&uuml;hren zum Sieg des roten Spielers:
	 * <ul>
	 * <li>Der K&ouml;nig wurde gefangen genommen</li>
	 * <li>Es sind weniger als 2 blaue M&auml;nner (ohne den K&ouml;nig) auf dem Spielfeld</li>
	 * <li>Die blauen M&auml;nner k&ouml;nnen keinen g&uuml;ltigen Zug mehr machen</li>
	 * </ul>
	 * </li>
	 * <li><code>BLUEWIN</code>: Der blaue Spieler hat das Spiel gewonnen. Folgende Situationen f&uuml;hren zum Sieg des blauen Spielers:
	 * <ul>
	 * <li>Der K&ouml;nig wurde auf eine Burg gezogen und ist entkommen</li>
	 * <li>Es sind weniger als 4 rote M&auml;nner auf dem Spielfeld</li>
	 * <li>Die roten M&auml;nner k&ouml;nnen keinen g&uuml;ltigen Zug mehr machen</li>
	 * </ul>
	 * </li>
	 * <li><code>DRAW</code>: Das Spiel endet unentschieden. Dazu f&uuml;hrt folgende Situation:
	 * <ul>
	 * <li>In den letzten 100 Z&uuml;gen (50 rote und 50 blaue Z&uuml;ge) wurde kein Mann geschlagen</li>
	 * </ul>
	 * </li>
	 * <li><code>OK</code>: Der Zug war g&uuml;ltig und wurde ausgef&uuml;hrt. Das Spiel ist nicht beendet. Dieser Fall tritt ein falls keiner der
	 * anderen F&auml;lle eingetreten ist.</li>
	 * </ul>
	 * <p>
	 * Ist der Zug ung&uuml;ltig wird abgebrochen und das Spielbrett hat den Status <code>ILLEGAL</code> (in diesem Fall sind keine weiteren Z&uuml;ge
	 * mehr m&ouml;glich).
	 * </p>
	 * 
	 * @param move
	 *          Auszuf&uuml;hrender Zug
	 */
	public void makeMove(Move move) {
		/* Nimm Zug nur entgegen, wenn das Spiel noch laueft und keiner gewonnen hat */
		if (status != Status.OK) {
			IO.debugln("Game over, cannot make any move @ Board.makeMove");
			return;
		}

		/* Wird aufgegeben... */
		if (move == null) {
			/* ... dann gewinnt der Spieler, der den Zug nicht gemacht hat */
			IO.debugln((isRed() ? "RED" : "BLUE") + " surrenders, " + ( !isRed() ? "RED" : "BLUE") + " won @ Board.makeMove");
			status = isRed() ? Status.BLUEWIN : Status.REDWIN;
			return;
		}

		/* Wenn der Zug nicht gueltig ist, setze den Status auf ILLEGAL und breche ab */
		IO.debugln("Checking move " + move + " @ Board.makeMove");
		if ( !isValidMove(move)) {
			IO.debugln("Move " + move + " invalid! @ Board.makeMove");
			status = Status.ILLEGAL;
			return;
		}
		IO.debugln("Move " + move + " valid @ Board.makeMove");

		/* Status gueltig, also Zug ausfuehren */
		doMove(move);
		lastMoves.addLast(new Move(move));
		/* Speichere nicht mehr als 6 Zuege */
		if (lastMoves.size() > 6)
			lastMoves.removeFirst();

		/* Schlage Steine falls moeglich */
		capture(move.getEnd());

		/* Informiere alle Observer des Bretts ueber den ausgefuehrten Zug */
		IO.debugln("Updating Observers @ Board.makeMove");
		changed();

		/* Wechsle den Spieler, muss hier gemacht werden, damit geprueft werden kann ob der naechste Spieler einen Zug ausfuehren kann */
		currentPlayer = currentPlayer == Square.RED ? Square.BLUE : Square.RED;
		/* Pruefe Gewinnsituationen */
		if (currentPlayer == Square.BLUE) {
			/* Pruefe ob der Koenig gefangen wurde */
			if (isKingInTheBox(move.getEnd())) {
				IO.debugln("King was catched with " + move.getEnd() + "! RED won the game @ Board.makeMove");
				status = Status.REDWIN;
				return;
			}
			int menCount = getMen(Square.BLUE).length;
			if (menCount < 2) {
				IO.debugln("BLUE has only " + menCount + " left, RED won the game @ Board.makeMove");
				status = Status.REDWIN;
				return;
			}
			if (getPossibleMoves().length == 0) {
				IO.debugln("BLUE cannot move, RED won the game @ Board.makeMove");
				status = Status.REDWIN;
				return;
			}
		} else {
			/* Pruefe ob der Koenig entkommen ist */
			if (isKingInCastle(move.getEnd())) {
				IO.debugln("King escaped to " + move.getEnd() + "! BLUE won the game @ Board.makeMove");
				status = Status.BLUEWIN;
				return;
			}
			int menCount = getMen(Square.RED).length;
			if (menCount < 4) {
				IO.debugln("RED has only " + menCount + " left, BLUE won the game @ Board.makeMove");
				status = Status.BLUEWIN;
				return;
			}
			if (getPossibleMoves().length == 0) {
				IO.debugln("RED cannot move, BLUE won the game @ Board.makeMove");
				status = Status.BLUEWIN;
				return;
			}
		}

		/* Pruefe auf Unentschieden */
		if (isDraw()) {
			IO.debugln("DRAW @ Board.makeMove");
			status = Status.DRAW;
			return;
		}

		status = Status.OK;
		IO.debugln("Move done complete @ Board.makeMove");
	}
}
