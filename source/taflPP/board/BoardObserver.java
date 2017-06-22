package taflPP.board;

import taflPP.preset.Move;
import taflPP.preset.Viewer;

/**
 * Dieses Interface gibt einer Klasse die M&ouml;glichkeit &uuml;ber &Auml;nderungen des Spielbretts informiert zu werden.
 * 
 * @author Dominick Leppich
 *
 */
public interface BoardObserver {
	public void setViewer(Viewer<Field> viewer);
	public void update(Move move);
}
