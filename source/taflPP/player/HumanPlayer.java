package taflPP.player;

import taflPP.board.Board;
import taflPP.gui.MatchPanel;
import taflPP.preset.Move;
import taflPP.preset.Requestable;
import eu.nepster.toolkit.io.IO;

public class HumanPlayer extends AbstractPlayer {
	private Requestable request;

	// ------------------------------------------------------------

	/**
	 * Erzeuge einen neuen menschlichen Spieler mit einem Brett
	 * 
	 * @param request
	 *          Requestable Objekt
	 * @param board
	 *          Brett
	 */
	public HumanPlayer(Requestable request, Board board) {
		super(board);
		this.request = request;
	}

	/**
	 * Erzeuge einen neuen menschlichen Spieler mit einem Brett
	 * 
	 * @param request
	 *          Requestable Objekt
	 * @param size
	 *          Brettgr&ouml;&szlig;e
	 */
	public HumanPlayer(Requestable request, int size) {
		super(size);
		this.request = request;
	}

	// ------------------------------------------------------------

	/**
	 * Fordere einen Zug vom Spieler an
	 * 
	 * @return Move Zug
	 */
	@Override public Move deliver() throws Exception {
		Move move = null;
		boolean correct = false;
		Move[] possibleMoves = board.getPossibleMoves();

		/* Setze das MatchPanel Board vor einem Zug, damit es gueltige Zuege erkennen kann */
		if (request instanceof MatchPanel) 
			((MatchPanel) request).setBoard(board);
		
		while ( !correct) {
			move = request.deliver();
			if (move == null)
				return null;
			for (Move m : possibleMoves)
				if (move.equals(m))
					correct = true;
			if ( !correct)
				IO.debugln("Move " + move + " from human player incorrect, please try it again @ HumanPlayer.deliver");
		}

		return move;
	}
}
