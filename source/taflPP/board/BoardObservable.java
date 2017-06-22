package taflPP.board;

import java.util.Vector;

/**
 * Kann von einem BoardObserver beobachtet werden, bei &Auml;nderungen wird jeder Observer informiert und bekommt den letzten gemachten Zug
 * &uuml;bergeben.
 * 
 * @author Dominick Leppich
 *
 */
public abstract class BoardObservable {
	/* Hier werden alle Observer des Boards gespeichert */
	private Vector<BoardObserver> observer;

	// ------------------------------------------------------------

	/**
	 * Erzeuge ein neues Objekt, welches von einem BoardObserver beobachtet werden kann
	 */
	public BoardObservable() {
		observer = new Vector<BoardObserver>();
	}

	// ------------------------------------------------------------

	/**
	 * F&uuml;ge einen Board-Observer hinzu
	 * 
	 * @param observer
	 *          Observer
	 */
	public void addObserver(BoardObserver observer) {
		if (this.observer.contains(observer))
			return;
		this.observer.add(observer);
		changed();
	}

	/**
	 * Entferne einen Board-Observer
	 * 
	 * @param observer
	 *          Observer
	 */
	public void removeObserver(BoardObserver observer) {
		if ( !this.observer.contains(observer))
			return;
		this.observer.remove(observer);
	}

	/**
	 * Etwas wurde ver&auml;ndert, jetzt werden alle Observer informiert
	 */
	public void changed() {
		for (BoardObserver o : observer)
			update(o);
	}

	// ------------------------------------------------------------

	/**
	 * Update die Observer
	 * 
	 * @param o
	 *          Observer des Boards
	 */
	public abstract void update(BoardObserver o);
}
