package taflPP.test;

import java.io.File;
import java.util.LinkedList;
import java.util.Random;

import taflPP.match.GameMove;
import taflPP.match.HTMLMatch;
import taflPP.preset.Move;
import taflPP.preset.Position;
import taflPP.preset.Status;

public class HTMLMatchTest {
	public static void main(String[] args) {
		LinkedList<GameMove> moves = new LinkedList<GameMove>();
		moves.add(new GameMove(new Move(new Position(7, 4), new Position(7, 5)), new Random().nextInt(10000), new Status(Status.OK)));
		moves.add(new GameMove(new Move(new Position(4, 5), new Position(1, 5)), new Random().nextInt(10000), new Status(Status.OK)));
		moves.add(new GameMove(new Move(new Position(7, 5), new Position(7, 4)), new Random().nextInt(10000), new Status(Status.OK)));
		moves.add(new GameMove(new Move(new Position(3, 4), new Position(3, 7)), new Random().nextInt(10000), new Status(Status.OK)));
		HTMLMatch.save(new File("save/Test.html"), "Erster Test: Dominick vs. PC", "Dominick", "PC", moves, 9);
	}
}
