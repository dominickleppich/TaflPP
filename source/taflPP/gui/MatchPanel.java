package taflPP.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import taflPP.board.Board;
import taflPP.board.BoardObserver;
import taflPP.board.Field;
import taflPP.preset.Move;
import taflPP.preset.Position;
import taflPP.preset.Requestable;
import taflPP.preset.Square;
import taflPP.preset.Viewer;
import eu.nepster.toolkit.gfx.GraphicTools;
import eu.nepster.toolkit.gfx.objects.AnimatedGraphic;
import eu.nepster.toolkit.gfx.objects.Graphic;
import eu.nepster.toolkit.gfx.objects.GraphicContainer;
import eu.nepster.toolkit.gfx.objects.SimpleText;
import eu.nepster.toolkit.gfx.objects.StaticGraphic;
import eu.nepster.toolkit.gui.RenderedPanel;
import eu.nepster.toolkit.io.IO;
import eu.nepster.toolkit.io.Outputable;
import eu.nepster.toolkit.settings.Settings;

/**
 * Panel auf dem das Spiel zu sehen ist
 * 
 * @author Dominick Leppich
 *
 */
public class MatchPanel extends RenderedPanel implements Outputable, MouseListener, MouseMotionListener, BoardObserver, Requestable {
	private static final long serialVersionUID = 1L;
	public static final char IDLE = 0;
	public static final char REQUEST = 1;
	public static final char BARREL_ROLL = 2;

	private char status = IDLE;
	private char oldStatus;

	/* Anzeige des Spielbretts */
	private Board board;
	private Viewer<Field> viewer;
	private Position start, end;
	private Move move;

	private GraphicContainer mainContainer;
	private GraphicContainer boardContainer;
	private StaticGraphic background;
	private float requestOverlayAlpha = 0.0f;
	private boolean requestOverlayUp = true;
	private StaticGraphic requestOverlay;
	private AnimatedGraphic[][] boardLabel;
	private AnimatedGraphic[][] boardMove;
	private AnimatedGraphic[][] boardMan;
	private Shape[][] boardFields;

	/* Cheats */
	private String text = "";
	private int barrelCount = 0;

	// ------------------------------------------------------------

	/**
	 * Erzeuge ein neues Panel, welches ein Spielbrett &uuml;ber den Viewer anzeigt
	 * 
	 */
	public MatchPanel() {
		super(false, 1.0);
		addMouseListener(this);
		addMouseMotionListener(this);
		setFocusable(true);

		setSize(400, 300);

		setFps(60);
		setRenderQuality(ANTI_ALIASING_SIMPLE);

		mainContainer = new GraphicContainer();
	}

	// ------------------------------------------------------------

	/**
	 * Setze das Spielbrett des MatchPanels. Das ist n&ouml;tig damit das Panel g&uuml;ltige Z&uuml;ge erkennen kann
	 * 
	 * @param board
	 *          Spielbrett
	 */
	public void setBoard(Board board) {
		this.board = board;
		IO.debugln("Set MatchPanel board @ MatchPanel.setBoard");
	}

	/**
	 * Setze den Viewer des MatchPanels. Hierbei werden alle Komponenten des Panels zur&uuml;ckgesetzt
	 * 
	 * @param viewer
	 *          Viewer des Spielbretts
	 */
	@Override public synchronized void setViewer(Viewer<Field> viewer) {
		this.viewer = viewer;
		IO.debugln("Set MatchPanel viewer @ MatchPanel.setViewer");
		init();
	}

	/**
	 * Setze alle Grafiken an die richtige Stelle und passe die Gr&ouml;&szlig;e an die Gr&ouml;&szlig;e des Panels an. Setze die boardField Shapes zur
	 * Felderkennung neu.
	 */
	public synchronized void init() {
		double mainScale = (double) getPreferredSize().getHeight() / Const.PANEL_HEIGHT;
		mainContainer.setToScale(mainScale, mainScale);

		boardContainer = new GraphicContainer();
		GraphicContainer boardBorderContainer = new GraphicContainer();
		GraphicContainer boardLabelContainer = new GraphicContainer();
		GraphicContainer boardMoveContainer = new GraphicContainer();
		GraphicContainer boardManContainer = new GraphicContainer();

		// TODO Richtige Groessenberechnung
		/* Lade Hintergrund */
		if (GraphicTools.getScreenType() == GraphicTools.SCREEN_TYPE_4x3) {
			background = new StaticGraphic(Graphic.GFX.get("background.4x3"));
			requestOverlay = new StaticGraphic(Graphic.GFX.get("overlay.active_4x3"));
		} else {
			background = new StaticGraphic(Graphic.GFX.get("background.16x9"));
			requestOverlay = new StaticGraphic(Graphic.GFX.get("overlay.active_16x9"));
		}
		double backgroundScale = (double) Const.PANEL_HEIGHT / background.getImage().getHeight(null);
		background.scale(backgroundScale, backgroundScale);
		requestOverlay.scale(backgroundScale, backgroundScale);

		int boardSize = Const.FIELD_SIZE * viewer.getSize();

		boardLabel = new AnimatedGraphic[viewer.getSize()][viewer.getSize()];
		boardMove = new AnimatedGraphic[viewer.getSize()][viewer.getSize()];
		boardMan = new AnimatedGraphic[viewer.getSize()][viewer.getSize()];
		boardFields = new Rectangle2D[viewer.getSize()][viewer.getSize()];

		/* Erzeuge Rahmen */
		Font borderFont = new Font("Sans Serif", Font.BOLD, 55);
		Color borderFontColor = new Color(218, 165, 32);
		/* Vertical */
		for (int number = 0; number < viewer.getSize(); number++) {
			StaticGraphic s = new StaticGraphic(Graphic.GFX.get("border.vertical"));
			s.translate( -Const.FIELD_SIZE / 2, Const.FIELD_SIZE + (viewer.getSize() - 2 - number) * Const.FIELD_SIZE);
			boardBorderContainer.add(s);

			s = new StaticGraphic(Graphic.GFX.get("border.vertical"));
			s.translate(viewer.getSize() * Const.FIELD_SIZE, Const.FIELD_SIZE + (viewer.getSize() - 2 - number) * Const.FIELD_SIZE);
			boardBorderContainer.add(s);

			SimpleText st = new SimpleText( -Const.FIELD_SIZE / 4, Const.FIELD_SIZE / 2 + (viewer.getSize() - 1 - number) * Const.FIELD_SIZE,
					borderFontColor, null, borderFont, String.valueOf((number > 8 ? String.valueOf((char) ('0' + (number + 1) / 10)) : "")
							+ (char) ('0' + (number + 1) % 10)));
			boardBorderContainer.add(st);
			st = new SimpleText(viewer.getSize() * Const.FIELD_SIZE + Const.FIELD_SIZE / 4, Const.FIELD_SIZE / 2 + (viewer.getSize() - 1 - number)
					* Const.FIELD_SIZE, borderFontColor, null, borderFont, String.valueOf((number > 8 ? String.valueOf((char) ('0' + (number + 1) / 10)) : "")
					+ (char) ('0' + (number + 1) % 10)));
			boardBorderContainer.add(st);
		}
		/* Horizontal */
		for (int letter = 0; letter < viewer.getSize(); letter++) {
			StaticGraphic s = new StaticGraphic(Graphic.GFX.get("border.horizontal"));
			s.translate(Const.FIELD_SIZE * letter, -Const.FIELD_SIZE / 2);
			boardBorderContainer.add(s);

			s = new StaticGraphic(Graphic.GFX.get("border.horizontal"));
			s.translate(Const.FIELD_SIZE * letter, viewer.getSize() * Const.FIELD_SIZE);
			boardBorderContainer.add(s);

			SimpleText st = new SimpleText(Const.FIELD_SIZE / 2 + letter * Const.FIELD_SIZE, -Const.FIELD_SIZE / 4, borderFontColor, null, borderFont,
					String.valueOf((char) ('A' + letter)));
			boardBorderContainer.add(st);
			st = new SimpleText(Const.FIELD_SIZE / 2 + letter * Const.FIELD_SIZE, viewer.getSize() * Const.FIELD_SIZE + Const.FIELD_SIZE / 4,
					borderFontColor, null, borderFont, String.valueOf((char) ('A' + letter)));
			boardBorderContainer.add(st);
		}
		/* und die vier Ecken */
		StaticGraphic s;
		s = new StaticGraphic(Graphic.GFX.get("border.top_left"));
		s.translate( -Const.FIELD_SIZE / 2, -Const.FIELD_SIZE / 2);
		boardBorderContainer.add(s);
		s = new StaticGraphic(Graphic.GFX.get("border.top_right"));
		s.translate(viewer.getSize() * Const.FIELD_SIZE, -Const.FIELD_SIZE / 2);
		boardBorderContainer.add(s);
		s = new StaticGraphic(Graphic.GFX.get("border.bottom_left"));
		s.translate( -Const.FIELD_SIZE / 2, viewer.getSize() * Const.FIELD_SIZE);
		boardBorderContainer.add(s);
		s = new StaticGraphic(Graphic.GFX.get("border.bottom_right"));
		s.translate(viewer.getSize() * Const.FIELD_SIZE, viewer.getSize() * Const.FIELD_SIZE);
		boardBorderContainer.add(s);

		/* Erzeuge Board */
		for (int number = 0; number < viewer.getSize(); number++) {
			for (int letter = 0; letter < viewer.getSize(); letter++) {
				boardLabel[letter][number] = new AnimatedGraphic();
				boardLabel[letter][number].translate(letter * Const.FIELD_SIZE, Const.FIELD_SIZE + (viewer.getSize() - 2 - number) * Const.FIELD_SIZE);
				boardMove[letter][number] = new AnimatedGraphic();
				boardMove[letter][number].translate(letter * Const.FIELD_SIZE, Const.FIELD_SIZE + (viewer.getSize() - 2 - number) * Const.FIELD_SIZE);
				boardMan[letter][number] = new AnimatedGraphic();
				boardMan[letter][number].translate(letter * Const.FIELD_SIZE, Const.FIELD_SIZE + (viewer.getSize() - 2 - number) * Const.FIELD_SIZE);

				boardLabel[letter][number].addAnimation("none", Graphic.GFX.get((letter + number) % 2 == 0 ? "field.empty_1" : "field.empty_2"), 1, false);
				boardLabel[letter][number].addAnimation("castle", Graphic.GFX.get("field.castle"), 1, false);
				boardLabel[letter][number].addAnimation("throne", Graphic.GFX.get("field.throne"), 1, false);

				boardMove[letter][number].addAnimation("mid_v", Graphic.GFX.get("move.mid_vertical"), 1, false);
				boardMove[letter][number].addAnimation("mid_h", Graphic.GFX.get("move.mid_horizontal"), 1, false);
				boardMove[letter][number].addAnimation("end_t", Graphic.GFX.get("move.end_top"), 1, false);
				boardMove[letter][number].addAnimation("end_l", Graphic.GFX.get("move.end_left"), 1, false);
				boardMove[letter][number].addAnimation("end_r", Graphic.GFX.get("move.end_right"), 1, false);
				boardMove[letter][number].addAnimation("end_b", Graphic.GFX.get("move.end_bottom"), 1, false);
				boardMove[letter][number].setVisible(false);

				boardMan[letter][number].addAnimation("none", Graphic.GFX.get("man.none"), 1, false);
				boardMan[letter][number].addAnimation("red", Graphic.GFX.get("man.red"), 1, false);
				boardMan[letter][number].addAnimation("blue", Graphic.GFX.get("man.blue"), 1, false);
				boardMan[letter][number].addAnimation("king", Graphic.GFX.get("man.king"), 1, false);

				boardLabelContainer.add(boardLabel[letter][number]);
				boardMoveContainer.add(boardMove[letter][number]);
				boardManContainer.add(boardMan[letter][number]);
			}
		}
		boardMoveContainer.setAlpha(0.6f);
		boardContainer.add(boardBorderContainer);
		boardContainer.add(boardLabelContainer);
		boardContainer.add(boardMoveContainer);
		boardContainer.add(boardManContainer);

		int startX = (int) (background.getScaleX() * background.getImage().getWidth() / 2 - boardSize * (double) 9 / viewer.getSize() / 2);
		int startY = (int) (background.getScaleY() * background.getImage().getHeight() / 2 - boardSize * (double) 9 / viewer.getSize() / 2);
		boardContainer.translate(startX, startY);
		boardContainer.scale((double) 9 / viewer.getSize(), (double) 9 / viewer.getSize());

		/* Setze Felderkennungs Shapes */
		for (int number = 0; number < viewer.getSize(); number++)
			for (int letter = 0; letter < viewer.getSize(); letter++)
				boardFields[letter][number] = new Rectangle2D.Double(startX * mainContainer.getScaleX() + letter * Const.FIELD_SIZE
						* mainContainer.getScaleX() * boardContainer.getScaleX(), startY * mainContainer.getScaleY() + (viewer.getSize() - 1 - number)
						* Const.FIELD_SIZE * mainContainer.getScaleY() * boardContainer.getScaleY(), Const.FIELD_SIZE * mainContainer.getScaleX()
						* boardContainer.getScaleX(), Const.FIELD_SIZE * mainContainer.getScaleY() * boardContainer.getScaleY());

		if ( !mainContainer.isEmpty())
			mainContainer.clear();
		mainContainer.add(background);
		mainContainer.add(requestOverlay);
		mainContainer.add(boardContainer);

		/* Setze Steine */
		updateMatchPanel();
	}

	/**
	 * Informiere Panel &uuml;ber &Auml;nderungen des Spielbretts
	 * 
	 * @param move
	 *          Gemachter Zug
	 */
	@Override public void update(Move move) {
		IO.debugln("MatchPanel updated @ MatchPanel.update");
		updateMatchPanel();

		if (board != null && move != null) {
			IO.debugln("MatchPanel got move " + move + " @ MatchPanel.update");
			// board.makeMove(move);
		}
	}

	/**
	 * Aktualisiere die angezeigte Spielsituation
	 */
	public synchronized void updateMatchPanel() {
		/* Falls noch kein Viewer gesetzt ist, kann nichts gemacht werden */
		if (viewer == null)
			return;

		/* Aktualisiere Steine */
		for (int letter = 0; letter < viewer.getSize(); letter++) {
			for (int number = 0; number < viewer.getSize(); number++) {
				String s;
				switch (viewer.getSquare(letter, number).getLabel()) {
					case Square.CASTLE:
						s = "castle";
						break;
					case Square.THRONE:
						s = "throne";
						break;
					case Square.NONE:
					default:
						s = "none";
				}
				boardLabel[letter][number].setAnimation(s);
				switch (viewer.getSquare(letter, number).getMan()) {
					case Square.RED:
						s = "red";
						break;
					case Square.BLUE:
						s = "blue";
						break;
					case Square.KING:
						s = "king";
						break;
					case Square.NONE:
					default:
						s = "none";
				}
				boardMan[letter][number].setAnimation(s);
			}
		}
	}

	// ------------------------------------------------------------

	/**
	 * Fordere einen Zug an
	 * 
	 * @return Zug
	 */
	@Override public synchronized Move deliver() throws Exception {
		move = null;
		start = null;
		end = null;
		status = REQUEST;
		/* Pochendes Overlay abschalten */
		requestOverlay.setVisible(true);
		wait();
		return move;
	}

	/**
	 * Wecke wartenden Thread auf
	 */
	private synchronized void moveDone() {
		/* Resette die Moveanzeige */
		for (AnimatedGraphic[] line : boardMove)
			for (AnimatedGraphic ag : line)
				ag.setVisible(false);
		status = IDLE;
		requestOverlay.setVisible(false);
		notify();
	}

	/**
	 * Bestimme die Boardposition an den Mauskoordinaten. <code>null</code> falls es kein Feld ist.
	 * 
	 * @param x
	 *          X-Koordinate
	 * @param y
	 *          Y-Koordinate
	 * @return Position
	 */
	private Position getPosition(int x, int y) {
		for (int letter = 0; letter < viewer.getSize(); letter++)
			for (int number = 0; number < viewer.getSize(); number++)
				if (boardFields[letter][number].contains(x, y))
					return new Position(letter, number);

		return null;
	}

	// ------------------------------------------------------------
	
	/**
	 * <h1>Zeichne das MatchPanel</h1>
	 * 
	 * <h2>Spielbrett</h2> Zun&auml;chst wird das Hintegrundbild gezeichnet. Darauf kommt das Spielbrett mit der aktuellen Spielsituation. Das
	 * hei&szlig;t es werden alle Felder gezeichnet, dar&uuml;ber alle gesetzten Steine. Wird gerade vom Panel ein Zug angefordert und die Maus wird mit
	 * gedr&uuml;ckter linker Maustaste &uuml;ber die Felder bewegt, wird der aktuell ausgew&auml;hlte Zug gr&uuml;n hervorgehoben. Diese Hervorhebung
	 * liegt &uuml;ber den Felder aber unter den Spielsteinen.
	 * 
	 * TODO <h2>Men&uuml;</h2> Befindet sich das Panel gerade im Men&uuml;, wird ein Men&uuml; angezeigt und bietet die M&ouml;glichkeit Einstellungen
	 * vorzunehmen.
	 */
	@Override public synchronized void draw(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		if (status == BARREL_ROLL) {
			if (barrelCount++ < 360) {
				boardContainer.translate(128 * viewer.getSize(), 128 * viewer.getSize());
				boardContainer.rotate( -Math.toRadians(1));
				boardContainer.translate( -128 * viewer.getSize(), -128 * viewer.getSize());

				for (int letter = 0; letter < boardLabel.length; letter++) {
					for (int number = 0; number < boardLabel.length; number++) {
						boardLabel[letter][number].translate(128, 128);
						boardLabel[letter][number].rotate(Math.toRadians(2));
						boardLabel[letter][number].translate( -128, -128);
						boardMan[letter][number].translate(128, 128);
						boardMan[letter][number].rotate(Math.toRadians(2));
						boardMan[letter][number].translate( -128, -128);
					}
				}
			} else {
				barrelCount = 0;
				status = oldStatus;
			}
		}

		if (status == REQUEST)
			requestOverlay.setAlpha(requestOverlayAlpha);
			
		if (requestOverlayUp) {
			requestOverlayAlpha += 0.03;
			if (requestOverlayAlpha >= 1.0f)
				requestOverlayUp = false;
		} else {
			requestOverlayAlpha -= 0.03;
			if (requestOverlayAlpha <= 0.0f)
				requestOverlayUp = true;
		}

		mainContainer.draw(g);

		/* Zeige rote Felderkennungsrechtecke */
		if (Settings.CFG.is("debug")) {
			g.setTransform(new AffineTransform());
			g.setColor(Color.RED);
			for (int letter = 0; letter < viewer.getSize(); letter++)
				for (int number = 0; number < viewer.getSize(); number++)
					g.draw(boardFields[letter][number]);
		}

		if (Settings.CFG.is("debug")) {
			g.setFont(new Font("Arial", Font.PLAIN, 12));
			g.setTransform(new AffineTransform());
			g.setColor(Color.WHITE);
			g.drawString("Frame: " + ++count, 20, 20);
			g.drawString("Text: " + text, 20, 40);
		}
	}

	private long count = 0;

	// ------------------------------------------------------------

	@Override public void output(String s) {
		// TODO Auto-generated method stub

	}

	public void keyPressed(KeyEvent e) {
		text += e.getKeyChar();

		/* Pruefe Cheat Codes */
		if (text.contains("barrelroll")) {
			if (status != BARREL_ROLL) {
				barrelCount = 0;
				oldStatus = status;
				status = BARREL_ROLL;
				text = "";
			}
		}
	}

	@Override public void mousePressed(MouseEvent e) {
		if (status == REQUEST)
			start = getPosition(e.getX(), e.getY());
	}

	@Override public void mouseClicked(MouseEvent e) {
	}

	@Override public void mouseReleased(MouseEvent e) {
		if (status == REQUEST && start != null) {
			end = getPosition(e.getX(), e.getY());
			if (end != null) {
				move = new Move(start, end);
				if (board.isValidMove(move))
					moveDone();
				else
					move = null;
			}
		}
	}

	@Override public void mouseEntered(MouseEvent e) {
	}

	@Override public void mouseExited(MouseEvent e) {
	}

	@Override public void mouseDragged(MouseEvent e) {
		if (status == REQUEST) {
			Position current = getPosition(e.getX(), e.getY());
			/* Pruefe ob Maus auf einem gueltigen Endfeld ist */
			if (current != null && start != null && !current.equals(start) && board.isValidMove(new Move(start, current))) {

				int startLetter = start.getLetter();
				int startNumber = start.getNumber();

				int currentLetter = current.getLetter();
				int currentNumber = current.getNumber();

				/* Resette die Moveanzeige */
				for (AnimatedGraphic[] line : boardMove)
					for (AnimatedGraphic ag : line)
						ag.setVisible(false);

				/* Vertikaler Zug */
				if (currentLetter == startLetter) {
					if (currentNumber < startNumber) {
						boardMove[startLetter][startNumber].setAnimation("end_t");
						boardMove[startLetter][startNumber].setVisible(true);
						for (int number = startNumber - 1; number > currentNumber; number--) {
							boardMove[currentLetter][number].setAnimation("mid_v");
							boardMove[currentLetter][number].setVisible(true);
						}
						boardMove[currentLetter][currentNumber].setAnimation("end_b");
						boardMove[currentLetter][currentNumber].setVisible(true);
					} else {
						boardMove[startLetter][startNumber].setAnimation("end_b");
						boardMove[startLetter][startNumber].setVisible(true);
						for (int number = startNumber + 1; number < currentNumber; number++) {
							boardMove[currentLetter][number].setAnimation("mid_v");
							boardMove[currentLetter][number].setVisible(true);
						}
						boardMove[currentLetter][currentNumber].setAnimation("end_t");
						boardMove[currentLetter][currentNumber].setVisible(true);
					}
				} else {
					if (currentLetter < startLetter) {
						boardMove[startLetter][startNumber].setAnimation("end_r");
						boardMove[startLetter][startNumber].setVisible(true);
						for (int letter = startLetter - 1; letter > currentLetter; letter--) {
							boardMove[letter][currentNumber].setAnimation("mid_h");
							boardMove[letter][currentNumber].setVisible(true);
						}
						boardMove[currentLetter][currentNumber].setAnimation("end_l");
						boardMove[currentLetter][currentNumber].setVisible(true);
					} else {
						boardMove[startLetter][startNumber].setAnimation("end_l");
						boardMove[startLetter][startNumber].setVisible(true);
						for (int letter = startLetter + 1; letter < currentLetter; letter++) {
							boardMove[letter][currentNumber].setAnimation("mid_h");
							boardMove[letter][currentNumber].setVisible(true);
						}
						boardMove[currentLetter][currentNumber].setAnimation("end_r");
						boardMove[currentLetter][currentNumber].setVisible(true);
					}
				}
				return;
			}
		}
		/* Resette die Moveanzeige */
		for (AnimatedGraphic[] line : boardMove)
			for (AnimatedGraphic ag : line)
				ag.setVisible(false);
	}

	@Override public void mouseMoved(MouseEvent e) {
	}
}
