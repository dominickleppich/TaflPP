package taflPP.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JRootPane;

import eu.nepster.toolkit.gfx.GraphicTools;
import eu.nepster.toolkit.io.IO;
import eu.nepster.toolkit.worker.Refreshable;
import eu.nepster.toolkit.worker.RefresherThread;

/**
 * Fenster des Programms
 * 
 * @author Dominick Leppich
 *
 */
public class Gui extends JFrame implements Refreshable, KeyListener, ComponentListener, FocusListener {
	private static final long serialVersionUID = 1L;
	private final int maxPanels;

	private RefresherThread rt;

	private int pos;
	private Box[] panelBoxes;
	private Vector<MatchPanel> panel;

	private boolean fullscreen = false;
	private Dimension oldDimension;
	private Point oldPos;

	// ------------------------------------------------------------

	/**
	 * Erzeuge eine neue GUI mit vier Platzhalter f&uuml;r MatchPanel. Auf diese Weise kann eine GUI bis zu vier Spiele gleichzeitig anzeigen.
	 * 
	 * @param maxPanels
	 *          Maximale Anzahl von anzuzeigenden Paneln
	 */
	public Gui(int maxPanels) {
		super("TaflPP v0.7");
		this.maxPanels = maxPanels;
		
		addComponentListener(this);
		addFocusListener(this);
		addKeyListener(this);
		setFocusable(true);

		rt = new RefresherThread(this);
		rt.setRps(60);

		pos = 0;
		panel = new Vector<MatchPanel>();
		panelBoxes = new Box[maxPanels];
		for (int i = 0; i < maxPanels; i++)
			panelBoxes[i] = Box.createHorizontalBox();

		/* Fuege Panel Gitter ein */
		// TODO sqrt(max_panels) * sqrt... raster
		int p = 0;
		Box matchPanelBox = Box.createVerticalBox();
		for (int i = 0; i < (int) Math.ceil(Math.sqrt(maxPanels)); i++) {
			Box line = Box.createHorizontalBox();
			for (int j = 0; p < maxPanels && j < (int) Math.ceil(Math.sqrt(maxPanels)); j++)
				line.add(panelBoxes[p++]);
			matchPanelBox.add(line);
		}
		add(matchPanelBox);

		/* Setze Handcursor */
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// setResizable(false);
		setVisible(true);
	}

	// ------------------------------------------------------------

	/**
	 * F&uuml;ge ein MatchPanel hinzu
	 * 
	 * @param mp
	 *          MatchPanel
	 */
	public void addPanel(MatchPanel mp) {
		if (pos >= maxPanels)
			return;

		panelBoxes[pos++].add(mp);
		panel.add(mp);

		pack();
	}

	// ------------------------------------------------------------

	/**
	 * Schalte zwischen Vollbildmodus hin und her
	 */
	private void switchFullscreen() {
		if (fullscreen) {
			getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
			setSize(oldDimension);
			setLocation(oldPos);
		} else {
			getRootPane().setWindowDecorationStyle(JRootPane.NONE);
			oldDimension = getSize();
			oldPos = getLocation();
			setSize(GraphicTools.getScreenDimension());
			setLocation(0, 0);
		}
		fullscreen = !fullscreen;
		toFront();
	}

	/**
	 * Starte Refreshen
	 */
	public void startRefresh() {
		rt.startRefresh();
	}

	/**
	 * Stoppe Refreshen
	 */
	public void stopRefresh() {
		rt.stopRefresh();
	}

	/**
	 * Refresher alle Panel
	 */
	@Override public void refresh() {
		for (MatchPanel mp : panel)
			mp.refresh();
	}

	// ------------------------------------------------------------

	@Override public void componentResized(ComponentEvent e) {
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		int contentWidth = getContentPane().getWidth();
		int contentHeight = getContentPane().getHeight();
		int borderWidth = getWidth() - contentWidth;
		int borderHeight = getHeight() - contentHeight;
		IO.debugln("Resized window to " + getWidth() + ", " + getHeight() + ". New content size " + contentWidth + ", " + contentHeight
				+ " @ Gui.componentResized");

		int newWidth, newHeight;
		newHeight = contentHeight;
		if (GraphicTools.getScreenType() == GraphicTools.SCREEN_TYPE_4x3)
			newWidth = (int) ((double) 4 / 3 * contentHeight);
		else
			newWidth = (int) ((double) 16 / 9 * contentHeight);

		// TODO Bessere Panel Verwaltung noetig
		for (MatchPanel mp : panel) {
			mp.setSize(newWidth / (int) Math.ceil(Math.sqrt(pos)), newHeight / (int) Math.ceil(Math.sqrt(pos)));
			mp.init();
		}
		setSize(newWidth + borderWidth, newHeight + borderHeight);
	}

	@Override public void componentMoved(ComponentEvent e) {

	}

	@Override public void componentShown(ComponentEvent e) {

	}

	@Override public void componentHidden(ComponentEvent e) {

	}

	@Override public void keyTyped(KeyEvent e) {

	}

	@Override public void keyPressed(KeyEvent e) {
		/* Vollbildumschalten */
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
			switchFullscreen();
		else
			for (MatchPanel mp : panel)
				mp.keyPressed(e);
	}

	@Override public void keyReleased(KeyEvent e) {

	}

	@Override public void focusGained(FocusEvent e) {
		for (MatchPanel mp : panel)
			mp.focusGained(e);
	}

	@Override public void focusLost(FocusEvent e) {
		for (MatchPanel mp : panel)
			mp.focusLost(e);
	}
}
