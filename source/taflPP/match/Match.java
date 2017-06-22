package taflPP.match;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.LinkedList;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import taflPP.board.AsciiBoard;
import taflPP.board.Board;
import taflPP.board.BoardObserver;
import taflPP.preset.Move;
import taflPP.preset.Player;
import taflPP.preset.Square;
import taflPP.preset.Status;
import eu.nepster.toolkit.file.FileAccess;
import eu.nepster.toolkit.io.IO;
import eu.nepster.toolkit.lang.Language;
import eu.nepster.toolkit.time.StopWatch;

/**
 * Diese Klasse l&auml;sst ein Spiel ablaufen. Spiele werden protokolliert und k&ouml;nnen im Anschluss als XML-Datei gespeichert werden.
 * 
 * @author Dominick Leppich
 *
 */
public class Match extends Thread {
	public static final String SAVE_DIR = "save";

	public static final int MIN_TIMEOUT = 1000;

	/* Roter und Blauer Spieler Referenzen */
	private Player red, blue;
	private Player nextPlayer;

	/* Namen der Spieler */
	private String redName, blueName;

	/* Ist es der erste Zug? */
	private boolean firstMove;

	/* Spielbrett */
	private Board board;

	/* Name des Spiels zum speichern */
	private Object saveGameMonitor = new Object();

	/* Stoppuhr zum messen der Zugzeit */
	private StopWatch stopWatch;

	/* running = true: Das Spiel laueft noch, kann aber pausiert sein */
	/* running = false: Spiel beendet */
	/* active = true: Spiel wird zur Zeit ausgefuehrt */
	private boolean running, active;

	private LinkedList<GameMove> madeMoves;

	// ------------------------------------------------------------

	/**
	 * Erzeuge ein neues Spiel mit rotem und blauem Spieler
	 * 
	 * @param red
	 *          Roter Spieler
	 * @param blue
	 *          Blauer Spieler
	 * @param redName
	 *          Name des roten Spielers
	 * @param blueName
	 *          Name des blauen Spielers
	 * @param observer
	 *          Spielbrettanzeige
	 * @param size
	 *          Spielbrettgr&ouml;sse
	 * @param loadGameFile
	 *          Datei des zu ladenden Spiels
	 */
	public Match(Player red, Player blue, String redName, String blueName, BoardObserver observer, int size, File loadGameFile) {
		this.red = red;
		this.blue = blue;
		this.redName = redName;
		this.blueName = blueName;

		/* Resette Spieler */
		try {
			red.reset(Square.RED);
			blue.reset(Square.BLUE);
		} catch (RemoteException e) {
			IO.errorln("RemoteException @ Match.run");
			e.printStackTrace();
		} catch (Exception e) {
			IO.errorln("Player exception @ Match.run");
			e.printStackTrace();
		}

		nextPlayer = red;
		firstMove = true;

		board = new Board(size);
		/* Brettanzeige */
		observer.setViewer(board.viewer());
		board.addObserver(observer);

		stopWatch = new StopWatch(false);

		madeMoves = new LinkedList<GameMove>();

		/* Spiel soll zun&auml;chst an sein */
		running = true;
		/* Spiel startet nicht, bevor es nicht explizit gestartet wird */
		active = false;

		/* Startet den Thread, dieser legt sich aber sofort schlafen */
		start();
	}

	// ------------------------------------------------------------

	/**
	 * Startet das Spiel
	 */
	public synchronized void startMatch() {
		IO.debugln("Match started @ Match.startMatch");
		active = true;
		notify();
	}

	/**
	 * H&auml;lt das Spiel an
	 */
	public void pauseMatch() {
		IO.debugln("Match paused @ Match.pauseMatch");
		active = false;
	}

	/**
	 * Beendet das Spiel vorzeitig
	 */
	@SuppressWarnings("deprecation") public void stopMatch() {
		IO.debugln("Match stopped @ Match.stopMatch");
		stop();
	}

	/**
	 * Wartet bis das Spiel zu Ende ist
	 */
	public synchronized void waitMatchEnd() {
		try {
			wait();
		} catch (InterruptedException e) {
			IO.errorln("Error waiting for match @ Match.waitMatchEnd");
			e.printStackTrace();
		}

	}

	/**
	 * Weckt wartende Threads auf
	 */
	private synchronized void wakeUpWaitingThreads() {
		notify();
	}

	// ------------------------------------------------------------

	/**
	 * Speichert das Spiel (alle bisher gemachten Z&uuml;ge in einer XML Datei ab
	 * 
	 * @param saveGameFileName
	 *          Dateiname der SaveGame Datei
	 * @param gameName
	 *          Name des Spiels
	 */
	public void saveGame(String saveGameFileName, String gameName) {
		synchronized (saveGameMonitor) {
			File file = FileAccess.createFile(SAVE_DIR + System.getProperty("file.separator") + saveGameFileName + ".xml");

			IO.debugln("Saving Match with Player " + (redName != null ? redName : "NOT SET") + " and " + (blueName != null ? blueName : "NOT SET")
					+ " in file " + file.getName() + " @ Match.saveGame");

			try {
				/* Wurzelelement erzeugen */
				Element root = new Element("match");
				Document doc = new Document(root);

				Element redName = new Element("red");
				redName.setText(this.redName != null ? this.redName : Language.get("player_red"));
				Element blueName = new Element("blue");
				blueName.setText(this.blueName != null ? this.blueName : Language.get("player_blue"));
				root.addContent(redName);
				root.addContent(blueName);
				Element game = new Element("game");
				game.setText(gameName);
				root.addContent(game);
				Element moveCount = new Element("move_count");
				moveCount.setText(String.valueOf(madeMoves.size()));
				root.addContent(moveCount);

				int count = 1;
				for (GameMove gm : madeMoves) {
					Element e = new Element("move_" + count++);
					e.addContent(new Element("move").setText((gm.getMove() != null ? gm.getMove().getStart() + "-" + gm.getMove().getEnd() : "null")));
					e.addContent(new Element("time").setText(Long.toString(gm.getTime())));
					e.addContent(new Element("status").setText(gm.getStatus().toString()));
					root.addContent(e);
				}

				/* XML Datei speichern */
				FileOutputStream outStream = new FileOutputStream(file);
				XMLOutputter outToFile = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("unicode");
				outToFile.setFormat(format);
				outToFile.output(doc, outStream);
				outStream.flush();
				outStream.close();

				IO.debugln("Saving game successful @ Match.saveGame");
			} catch (IOException e) {
				IO.errorln("Error saving file " + file.getName() + " @ Match.saveGame");
			}
			HTMLMatch.save(FileAccess.createFile(SAVE_DIR + System.getProperty("file.separator") + saveGameFileName + ".html"), gameName, redName,
					blueName, madeMoves, board.getSize());
		}
	}

	/**
	 * Setze den Namen des roten Spielers, falls nicht gesetzt
	 * 
	 * @param name
	 *          Name
	 */
	public void setRedPlayerName(String name) {
		synchronized (saveGameMonitor) {
			if (redName == null)
				this.redName = name;
		}
	}

	/**
	 * Setze den Namen des blauen Spielers, falls nicht gesetzt
	 * 
	 * @param name
	 *          Name
	 */
	public void setBluePlayerName(String name) {
		synchronized (saveGameMonitor) {
			if (blueName == null)
				this.blueName = name;
		}
	}

	// ------------------------------------------------------------

	/**
	 * Hauptspielschleife
	 */
	public void run() {
		/* Nur solange das Spiel an ist */
		while (running && board.getStatus().isOK()) {
			/* Wenn das Spiel nicht pausiert ist, fuehre alle noetigen Schritte aus */
			if (active) {
				Player p1, p2;
				if (nextPlayer == red) {
					p1 = red;
					p2 = blue;
				} else {
					p1 = blue;
					p2 = red;
				}
				try {
					if (firstMove) {
						/* Zeige Brett an */
						IO.debugln(AsciiBoard.getAsciiBoard(board.viewer()));
						firstMove = false;
					}

					/* Fordere einen Zug an und messe die Zeit */
					IO.println(Language.get("game_player_move_request", p1 == red ? (redName == null ? Language.get("player_red") : redName)
							: (blueName == null ? Language.get("player_blue") : blueName)));
					stopWatch.start();
					Move move = p1.request();
					stopWatch.stop();

					/* Kleiner Timeout falls noetig */
					long waitTime = MIN_TIMEOUT - stopWatch.getMilliTime();
					if (waitTime > 0) {
						IO.debugln("Timeout for " + waitTime + "ms @ Match.run");
						Thread.sleep(waitTime);
					}

					board.makeMove(move);

					IO.println(Language.get("game_player_made_move", p1 == red ? (redName == null ? Language.get("player_red") : redName)
							: (blueName == null ? Language.get("player_blue") : blueName), (move != null ? move.toString() : Language.get("move_surrender"))));

					Status status = board.getStatus();

					/* Speichere Zug ab */
					synchronized (saveGameMonitor) {
						madeMoves.addLast(new GameMove(move, stopWatch.getMilliTime(), status));
					}

					IO.debugln("Needed " + (double) stopWatch.getMilliTime() / 1000 + "s for move @ Match.run");

					p1.confirm(status);
					p2.update(move, status);

					/* Wechsle Spieler */
					nextPlayer = p2;

					/* Zeige Brett an */
					IO.debugln(AsciiBoard.getAsciiBoard(board.viewer()));
				} catch (InterruptedException e) {
					IO.errorln("InterruptedException in Match @ Match.run");
					e.printStackTrace();
				} catch (Exception e) {
					IO.errorln("Exception in Match @ Match.run");
					e.printStackTrace();
					System.exit(0);
				}
			}
			/* Sonst lege den Thread schlafen */
			else {
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
						IO.errorln("Exception in game pause @ Match.run");
						e.printStackTrace();
					}
				}
			}
		}
		Status status = board.getStatus();
		if ( !status.isOK()) {
			IO.debugln("Match over with Status " + board.getStatus() + " @ Match.run");

			if (status.isREDWIN())
				IO.println(Language.get("game_won_normal", (redName == null ? Language.get("player_red") : redName)));
			else if (status.isBLUEWIN())
				IO.println(Language.get("game_won_normal", (blueName == null ? Language.get("player_blue") : blueName)));
			else if (status.isDRAW())
				IO.println(Language.get("game_draw"));
		}
		running = false;

		/* Wecke wartende Threads */
		wakeUpWaitingThreads();
	}
}
