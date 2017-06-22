package taflPP.player;

import taflPP.preset.Move;
import taflPP.preset.Position;
import taflPP.preset.Requestable;
import eu.nepster.toolkit.io.IO;
import eu.nepster.toolkit.lang.Language;

/**
 * Diese Klasse erm&ouml;glicht das Einlesen eines Zuges auf der Kommandozeile. Eingaben der Form X#,X# oder x# X# sind m&ouml;glich, wobei x,X
 * beliebige Buchstaben (gross oder klein) sind und # f&uuml;r eine g&uuml;ltige Zahl steht.
 * 
 * @author Dominick Leppich
 *
 */
public class IOMoveInterpreter implements Requestable {

	private int size;
	private boolean showExample;

	// ------------------------------------------------------------

	/**
	 * Erzeuge einen Move Interpreter f&uuml;r die Kommandozeile
	 * 
	 * @param size
	 *          Spielfeldgr&ouml;sse
	 */
	public IOMoveInterpreter(int size) {
		this.size = size;
		this.showExample = true;
	}

	// ------------------------------------------------------------

	/**
	 * Liefere einen Zug von der Standardeingabe
	 * 
	 * @return Zug
	 */
	@Override public Move deliver() throws Exception {
		Move move = null;
		while (move == null) {
			if (showExample) {
				for (int i = 0; i < Integer.parseInt(Language.get("console_help_count")); i++)
					IO.println(Language.get("console_help_" + (i + 1)));
				showExample = false;
			}
			IO.print(Language.get("console_make_move"));
			String s = IO.input().toLowerCase();
			/* Pruefe ob aufgegeben werden soll */
			if (s.equals("surrender") || s.equals("surr")) {
				IO.print("Do you really want to surrender [yes] [no]? ");
				String ans = IO.input();
				if (ans.equals("yes") || ans.equals("y"))
					return null;
			}
			if (validChars(s, size)) {
				int[] information = new int[4];
				int index = 0;
				int num = -1;
				for (int i = 0; i < s.length(); i++) {
					char c = s.charAt(i);
					/* Pruefe auf Buchstaben */
					if (isLetter(c, size)) {
						if (num != -1) {
							if (isNumber(num, size) && index % 2 == 1) {
								information[index++] = num - 1;
								num = -1;
							}
						}
						information[index++] = c - 'a';
					}
					/* Pruefe auf Zahlen */
					else if (isNumber(c, size)) {
						if (num == -1)
							num = c - '0';
						else {
							num *= 10;
							num += c - '0';
						}
					} else if (num != -1 && isNumber(num, size)) {
						information[index++] = num - 1;
						num = -1;
					}
				}
				/* Pruefe ob noch eine Zahl im Puffer ist */
				if (num != -1 && isNumber(num, size))
					information[index++] = num - 1;

				/* Gueltig wenn genau 4 Elemente gelesen */
				if (index == 4)
					move = new Move(new Position(information[0], information[1]), new Position(information[2], information[3]));
			}
			if (move == null) {
				showExample = true;
				// IO.clearInput();
			}
		}
		return move;
	}

	/**
	 * Parse einen String zu einem Zug
	 * 
	 * @param s
	 *          String
	 * @param size
	 *          Spielbrettgr&ouml;sse
	 * @return Zug
	 */
	public static Move parseMove(String s, int size) {
		if (s == null)
			return null;
		/* Pruefe ob aufgegeben werden soll */
		if (s.equals("surrender") || s.equals("surr")) {
			IO.print("Do you really want to surrender [yes] [no]? ");
			String ans = IO.input();
			if (ans.equals("yes") || ans.equals("y"))
				return null;
		}
		if (validChars(s, size)) {
			int[] information = new int[4];
			int index = 0;
			int num = -1;
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				/* Pruefe auf Buchstaben */
				if (isLetter(c, size)) {
					if (num != -1) {
						if (isNumber(num, size) && index % 2 == 1) {
							information[index++] = num - 1;
							num = -1;
						}
					}
					information[index++] = c - 'a';
				}
				/* Pruefe auf Zahlen */
				else if (isNumber(c, size)) {
					if (num == -1)
						num = c - '0';
					else {
						num *= 10;
						num += c - '0';
					}
				} else if (num != -1 && isNumber(num, size)) {
					information[index++] = num - 1;
					num = -1;
				}
			}
			/* Pruefe ob noch eine Zahl im Puffer ist */
			if (num != -1 && isNumber(num, size))
				information[index++] = num - 1;

			/* Gueltig wenn genau 4 Elemente gelesen */
			if (index == 4)
				return new Move(new Position(information[0], information[1]), new Position(information[2], information[3]));
			return null;
		}
		return null;
	}

	/**
	 * Enth&auml;lt die Eingabe nur g&uuml;ltige Zeichen?
	 * 
	 * @param s
	 *          Eingabe
	 * @param size
	 *          Spielbrettgr&ouml;&szlig;e
	 * @return Eingabe korrekt
	 */
	private static boolean validChars(String s, int size) {
		for (int i = 0; i < s.length(); i++)
			if ( !((s.charAt(i) >= '0' && s.charAt(i) <= '9') || (s.charAt(i) >= 'a' && s.charAt(i) <= 'a' + size) || s.charAt(i) == ' ' || s.charAt(i) == ','))
				return false;
		return true;

	}

	/**
	 * Ist das Zeichen ein g&uuml;tiger Buchstabe?
	 * 
	 * @param c
	 *          Buchstabe
	 * @param size
	 *          Spielbrettgr&ouml;&szlig;e
	 * @return G&uuml;ltiger Buchstabe
	 */
	private static boolean isLetter(char c, int size) {
		return c >= 'a' && c <= 'a' + size;
	}

	/**
	 * Ist das Zeichen eine g&uuml;ltige Zahl?
	 * 
	 * @param c
	 *          Buchstabe
	 * @param size
	 *          Spielbrettgr&ouml;&szlig;e
	 * @return G&uuml;ltige Zahl
	 */
	private static boolean isNumber(char c, int size) {
		if (size > 9) { return c >= '0' && c <= '9'; }
		return c >= '1' && c <= '0' + size;
	}

	/**
	 * Ist die Zahl g&uuml;ltig?
	 * 
	 * @param num
	 *          Zahl
	 * @param size
	 *          Spielbrettgr&ouml;&szlig;e
	 * @return G&uuml;ltige Zahl
	 */
	private static boolean isNumber(int num, int size) {
		return num >= 1 && num <= size;
	}
}
