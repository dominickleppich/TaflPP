package taflPP.match;

import java.io.File;
import java.util.LinkedList;
import java.util.Vector;

import taflPP.board.Board;
import taflPP.preset.Square;
import eu.nepster.toolkit.file.StringFile;

/**
 * Diese Klasse bietet die M&ouml;glichkeit ein Spiel, das hei&szlig;t eine Menge von gemachten Z&uuml;gen als HTML-Datei zu speichern. Diese
 * visualisiert dann den Spielverlauf und zeigt grafisch alle Zwischenst&auml;nde an.
 * 
 * @author Dominick Leppich
 *
 */
public class HTMLMatch {

	/**
	 * Speichert ein Spiel in einer Datei ab
	 * 
	 * @param file
	 *          Datei
	 * @param gameName
	 *          Spielname
	 * @param redName
	 *          Name des roten Spielers
	 * @param blueName
	 *          Name des blauen Spielers
	 * @param moves
	 *          Z&uuml;ge
	 * @param size
	 *          Brettgr&ouml;&szlig;e
	 */
	public static void save(File file, String gameName, String redName, String blueName, LinkedList<GameMove> moves, int size) {
		Vector<String> htmlLines = new Vector<String>();
		int boardSize = 600;
		int fieldSize = boardSize / size;

		/* Anfang des HTML Dokumentes mit CSS Definitionen */
		htmlLines.add("<!DOCTYPE html>");
		htmlLines.add("<html lang=\"de\">");
		htmlLines.add("\t<head>");
		htmlLines.add("\t\t<title>" + gameName + "</title>");
		htmlLines.add("\t\t<style type=\"text/css\">");
		htmlLines.add("\t\t\tbody {");
		htmlLines.add("\t\t\t\twidth: " + (size * fieldSize + 40) + "px;");
		htmlLines.add("\t\t\t\tbackground-color: #EFEFEF;");
		htmlLines.add("\t\t\t\tcolor: #000000;");
		htmlLines.add("\t\t\t}");
		htmlLines.add("\t\t\t.board {");
		htmlLines.add("\t\t\t\tposition: relative;");
		htmlLines.add("\t\t\t\twidth: " + (size * fieldSize + 40) + "px;");
		htmlLines.add("\t\t\t\theight: " + (size * fieldSize) + "px;");
		htmlLines.add("\t\t\t}");
		htmlLines.add("\t\t\t.field {");
		htmlLines.add("\t\t\t\tposition: absolute;");
		htmlLines.add("\t\t\t}");
		htmlLines.add("\t\t\t.move_start {");
		htmlLines.add("\t\t\t\tborder-radius: 15px;");
		htmlLines.add("\t\t\t\tmargin-bottom: 10px;");
		htmlLines.add("\t\t\t\tpadding: 20px;");
		htmlLines.add("\t\t\t\tbackground-color: #AFAFAF;");
		htmlLines.add("\t\t\t}");
		htmlLines.add("\t\t\t.move_odd {");
		htmlLines.add("\t\t\t\tborder-radius: 15px;");
		htmlLines.add("\t\t\t\tmargin-bottom: 10px;");
		htmlLines.add("\t\t\t\tpadding: 20px;");
		htmlLines.add("\t\t\t\tbackground-color: #F5A9BC;");
		htmlLines.add("\t\t\t}");
		htmlLines.add("\t\t\t.move_even {");
		htmlLines.add("\t\t\t\tborder-radius: 15px;");
		htmlLines.add("\t\t\t\tmargin-bottom: 10px;");
		htmlLines.add("\t\t\t\tpadding: 20px;");
		htmlLines.add("\t\t\t\tbackground-color: #A9BCF5;");
		htmlLines.add("\t\t\t}");
		htmlLines.add("\t\t\t.move_end {");
		htmlLines.add("\t\t\t\tborder-radius: 15px;");
		htmlLines.add("\t\t\t\tmargin-bottom: 10px;");
		htmlLines.add("\t\t\t\tpadding: 20px;");
		htmlLines.add("\t\t\t\tbackground-color: #A9F5BC;");
		htmlLines.add("\t\t\t}");
		htmlLines.add("\t\t\t.stats {");
		htmlLines.add("\t\t\t\tborder-radius: 15px;");
		htmlLines.add("\t\t\t\tmargin-bottom: 10px;");
		htmlLines.add("\t\t\t\tpadding: 20px;");
		htmlLines.add("\t\t\t\tbackground-color: #FFDEAD;");
		htmlLines.add("\t\t\t}");
		htmlLines.add("\t\t\timg {");
		htmlLines.add("\t\t\t\twidth: " + fieldSize + "px;");
		htmlLines.add("\t\t\t\theight: " + fieldSize + "px;");
		htmlLines.add("\t\t\t}");
		htmlLines.add("\t\t</style>");
		htmlLines.add("\t</head>");
		htmlLines.add("\t<body>");

		/* Spielbrett auf dem die Zuege nochmal ausgefuehrt werden, um den Zwischenstand anzeigen zu koennen */
		Board board = new Board(size);
		boolean odd = true;
		int count = 1;
		int redKicked = 0;
		int blueKicked = 0;
		long redTime = 0;
		long blueTime = 0;
		int manCount = board.getManCount();
		htmlLines.add("\t\t<div class=\"move_start\">");
		htmlLines.add("\t\t\t<table border=\"0\">");
		htmlLines.add("\t\t\t\t<tr>");
		htmlLines.add("\t\t\t\t\t<td align=\"right\"><b>Spiel:</b></td>");
		htmlLines.add("\t\t\t\t\t<td align=\"left\">&nbsp;&nbsp;" + gameName + "</td>");
		htmlLines.add("\t\t\t\t</tr>");
		htmlLines.add("\t\t\t\t<tr>");
		htmlLines.add("\t\t\t\t\t<td align=\"right\"><b>Rot:</b></td>");
		htmlLines.add("\t\t\t\t\t<td align=\"left\">&nbsp;&nbsp;" + redName + "</td>");
		htmlLines.add("\t\t\t\t</tr>");
		htmlLines.add("\t\t\t\t<tr>");
		htmlLines.add("\t\t\t\t\t<td align=\"right\"><b>Blau:</b></td>");
		htmlLines.add("\t\t\t\t\t<td align=\"left\">&nbsp;&nbsp;" + blueName + "</td>");
		htmlLines.add("\t\t\t\t</tr>");
		htmlLines.add("\t\t\t</table>");
		htmlLines.add("\t\t\t<br />");
		for (String line : getBoardCode(board, fieldSize))
			htmlLines.add(line);
		htmlLines.add("\t\t</div>");

		for (GameMove gm : moves) {
			htmlLines.add("\t\t<div class=\"move_" + ((count == moves.size()) ? "end" : (odd ? "odd" : "even")) + "\">");
			htmlLines.add("\t\t\t<table border=\"0\">");
			htmlLines.add("\t\t\t\t<tr>");
			htmlLines.add("\t\t\t\t\t<td align=\"right\"><b>Zug " + count++ + ":</b></td>");
			htmlLines.add("\t\t\t\t\t<td align=\"left\">&nbsp;&nbsp;" + gm.getMove() + "</td>");
			htmlLines.add("\t\t\t\t</tr>");
			String s1, s2;
			s1 = String.valueOf(gm.getTime() / 1000);
			s2 = String.valueOf(gm.getTime() % 1000);
			
			/* Zaehle die Zeit fuer Rot oder Blau zur Gesamtzeit */
			if (odd)
				redTime += gm.getTime();
			else
				blueTime += gm.getTime();
			
			while (s2.length() < 3)
				s2 = "0" + s2;
			htmlLines.add("\t\t\t\t<tr>");
			htmlLines.add("\t\t\t\t\t<td align=\"right\"><b>Zeit:</b></td>");
			htmlLines.add("\t\t\t\t\t<td align=\"left\">&nbsp;&nbsp;" + s1 + "." + s2 + "s</td>");
			htmlLines.add("\t\t\t\t</tr>");
			htmlLines.add("\t\t\t\t<tr>");
			htmlLines.add("\t\t\t\t\t<td align=\"right\"><b>Status:</b></td>");
			htmlLines.add("\t\t\t\t\t<td align=\"left\">&nbsp;&nbsp;" + gm.getStatus() + "</td>");
			htmlLines.add("\t\t\t\t</tr>");
			htmlLines.add("\t\t\t</table>");
			htmlLines.add("\t\t\t<br />");
			
			board.makeMove(gm.getMove());
			for (String line : getBoardCode(board, fieldSize))
				htmlLines.add(line);
			htmlLines.add("\t\t</div>");
			
			/* Pruefe wie viele Steine geschlagen wurden */
			int newManCount = board.getManCount();
			if (manCount != newManCount) {
				if (odd)
					redKicked+=(manCount-newManCount);
				else
					blueKicked+=(manCount-newManCount);
			}
			manCount = newManCount;
			
			odd = !odd;
		}

		/* Gebe Statistik aus */
		htmlLines.add("\t\t<div class=\"stats\">");
		htmlLines.add("\t\t\t<table border=\"0\">");
		htmlLines.add("\t\t\t\t<tr>");
		htmlLines.add("\t\t\t\t\t<td align=\"right\"><b>Statistik</b></td>");
		htmlLines.add("\t\t\t\t\t<td>&nbsp;</td>");
		htmlLines.add("\t\t\t\t</tr>");
		htmlLines.add("\t\t\t\t<tr>");
		htmlLines.add("\t\t\t\t\t<td colspan=\"2\">&nbsp;</td>");
		htmlLines.add("\t\t\t\t</tr>");
		htmlLines.add("\t\t\t\t<tr>");
		htmlLines.add("\t\t\t\t\t<td align=\"right\"><b>" + redName + "</b></td>");
		htmlLines.add("\t\t\t\t\t<td>&nbsp;</td>");
		htmlLines.add("\t\t\t\t</tr>");
		htmlLines.add("\t\t\t\t<tr>");
		htmlLines.add("\t\t\t\t\t<td align=\"right\"><b>Gesamte Zugzeit:</b></td>");
		htmlLines.add("\t\t\t\t\t<td align=\"left\">&nbsp;&nbsp;" + String.valueOf(redTime / 1000) + "." + String.valueOf(redTime % 1000) + "s</td>");
		htmlLines.add("\t\t\t\t</tr>");
		htmlLines.add("\t\t\t\t<tr>");
		htmlLines.add("\t\t\t\t\t<td align=\"right\"><b>Geschlagene Maenner:</b></td>");
		htmlLines.add("\t\t\t\t\t<td align=\"left\">&nbsp;&nbsp;" + String.valueOf(redKicked) + "</td>");
		htmlLines.add("\t\t\t\t</tr>");
		htmlLines.add("\t\t\t\t<tr>");
		htmlLines.add("\t\t\t\t\t<td colspan=\"2\">&nbsp;</td>");
		htmlLines.add("\t\t\t\t</tr>");
		htmlLines.add("\t\t\t\t<tr>");
		htmlLines.add("\t\t\t\t\t<td align=\"right\"><b>" + blueName + "</b></td>");
		htmlLines.add("\t\t\t\t\t<td>&nbsp;</td>");
		htmlLines.add("\t\t\t\t</tr>");
		htmlLines.add("\t\t\t\t<tr>");
		htmlLines.add("\t\t\t\t\t<td align=\"right\"><b>Gesamte Zugzeit:</b></td>");
		htmlLines.add("\t\t\t\t\t<td align=\"left\">&nbsp;&nbsp;" + String.valueOf(blueTime / 1000) + "." + String.valueOf(blueTime % 1000) + "s</td>");
		htmlLines.add("\t\t\t\t</tr>");
		htmlLines.add("\t\t\t\t<tr>");
		htmlLines.add("\t\t\t\t\t<td align=\"right\"><b>Geschlagene Maenner:</b></td>");
		htmlLines.add("\t\t\t\t\t<td align=\"left\">&nbsp;&nbsp;" + String.valueOf(blueKicked) + "</td>");
		htmlLines.add("\t\t\t\t</tr>");
		htmlLines.add("\t\t\t</table>");
		htmlLines.add("\t\t</div>");
		
		htmlLines.add("\t</body>");
		htmlLines.add("</html>");

		StringFile.save(file, htmlLines);
	}

	/**
	 * Gibt den HTML Code f&uuml;r eine bestimmte Boardsituation zur&uuml;ck
	 * 
	 * @param board
	 *          Board
	 * @param fieldSize
	 *          Gr&ouml;&szlig;e eine Feldes
	 * @return HTML Code Zeilen
	 */
	private static Vector<String> getBoardCode(Board board, int fieldSize) {
		Vector<String> v = new Vector<String>();
		v.add("\t\t\t<div class=\"board\">");
		for (int letter = 0; letter < board.getSize(); letter++) {
			for (int number = 0; number < board.getSize(); number++) {
				String label = "";
				switch (board.getField(letter, number).getLabel()) {
					case Square.NONE:
						if ((letter + number) % 2 == 0)
							label = "empty_1";
						else
							label = "empty_2";
						break;
					case Square.CASTLE:
						label = "castle";
						break;
					case Square.THRONE:
						label = "throne";
						break;
				}
				v.add("\t\t\t\t<div class=\"field\" style=\"top: " + ((board.getSize() - 1 - number) * fieldSize) + "px; left: " + (letter * fieldSize)
						+ "px; width: " + fieldSize + "px; height: " + fieldSize + "px; z-index: 0;\" >");
				v.add("\t\t\t\t\t<img src=\"../gfx/field/" + label + ".png\" width=\"" + fieldSize + "\" height=\"" + fieldSize + "\" />");
				v.add("\t\t\t\t</div>");
				String man = null;
				switch (board.getField(letter, number).getMan()) {
					case Square.RED:
						man = "red";
						break;
					case Square.BLUE:
						man = "blue";
						break;
					case Square.KING:
						man = "king";
						break;
					default:
				}
				if (man != null) {
					v.add("\t\t\t\t<div class=\"field\" style=\"top: " + ((board.getSize() - 1 - number) * fieldSize) + "px; left: " + (letter * fieldSize)
							+ "px; width: " + fieldSize + "px; height: " + fieldSize + "px; z-index: 1;\" >");
					v.add("\t\t\t\t\t<img src=\"../gfx/man/" + man + ".png\" width=\"" + fieldSize + "\" height=\"" + fieldSize + "\" />");
					v.add("\t\t\t\t</div>");
				}
			}
		}
		v.add("\t\t\t</div>");
		return v;
	}
}
