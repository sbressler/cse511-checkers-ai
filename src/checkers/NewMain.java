package checkers;

import checkers.Display;
import checkers.Game;
import checkers.Player;
import checkers.ai.RandomPlayer;
import checkers.ascii.AsciiDisplay;
import checkers.ascii.AsciiPlayer;

/**
 * Meant to provide a unified main program, to execute a checkers game for any
 * kind of AI or human player.
 *
 * @author Kurt Glastetter
 */
class NewMain {
	public static void main(String args[]) {
		Player playerForBlack = new RandomPlayer();
		Player playerForWhite = new AsciiPlayer();

		Display display = new AsciiDisplay();

		Game game = new Game(playerForBlack, playerForWhite);

		game.registerDisplay(display);

		while (!game.isOver()) {
			game.makeMove(game.getPlayerToMove().chooseMove(game.getState()));
		}
	}
}
