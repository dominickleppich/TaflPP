package taflPP.player.ai;

import java.util.Random;

import taflPP.board.Board;
import taflPP.player.AbstractPlayer;
import taflPP.preset.Move;

public class RandomAI extends AbstractPlayer {

	/* Ueberschriebene Konstruktoren */
	public RandomAI(Board board) {
		super(board);
	}
	
	public RandomAI(Integer size) {
		super(size);
	}

	// ----------------------------------------------

	/**
	 * Liefere einen zuf&auml;lligen Zug aus der Menge aller m&ouml;glichen
	 * 
	 * @return Zug
	 */
	@Override public Move deliver() {
		Move[] possibleMoves = board.getPossibleMoves();

		if (possibleMoves.length == 0)
			return null;
		return possibleMoves[new Random().nextInt(possibleMoves.length)];
	}
}
