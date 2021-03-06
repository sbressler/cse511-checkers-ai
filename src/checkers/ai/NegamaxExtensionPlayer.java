package checkers.ai;

import java.util.List;

import checkers.model.GameState;
import checkers.model.Move;
import checkers.model.PlayerId;

/**
 * Negamax player with extensions for searching jump sequences.
 *
 * @author Andrew Duffey
 */
public class NegamaxExtensionPlayer extends AIPlayer implements Cloneable {
	private int searchDepth;

	/**
	 * Private constructor with no arguments. Disallows creation of a NegamaxPlayer
	 * if there was no depth specified. Use the NegamaxPlayer(Integer searchDepth)
	 * constructor.
	 */
	@SuppressWarnings("unused")
	private NegamaxExtensionPlayer() {
		super();
	}

	/**
	 * Constructs a Player that chooses moves based on a negamax search with a maximum
	 * depth of searchDepth.
	 * @param searchDepth
	 */
	public NegamaxExtensionPlayer(int searchDepth) {
		super();
		this.searchDepth = searchDepth;

		// statistics for each search
		this.searches = 0;
		this.evals = 0;
	}

	@Override
	public Move chooseMove(GameState state) {
		searches = 1;
		evals = 0;

		if (state.gameIsOver()) throw new IllegalArgumentException("Can't make a decision; state is terminal.");

		List<? extends Move> choices = state.possibleMoves();
		if (choices.size() == 1) {
			return choices.get(0);
		}

		// we expand the first level here so we can keep track of the best move (because
		// the negamax method doesn't keep track of moves, just values).
		Move bestChoice = null;
		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;
		for (Move choice : choices) {
			state.makeMoveUnchecked(choice);
			double util = -negamax(state, searchDepth - 1,  -beta, -alpha);
			state.undoMoveUnchecked(choice);

			if (util > alpha) {
				alpha = util;
				bestChoice = choice;
			}

			// this is sufficient for alpha-beta pruning
			if (alpha >= beta) {
				return bestChoice;
			}
		}

		return bestChoice;
	}

	private Double negamax(GameState state, int depth, double alpha, double beta) {
		searches++;

		if (state.gameIsOver()) {
			evals++;
			double util = Utils.utilityOf(state);
			return (state.playerToMove() == PlayerId.WHITE) ? util : -util;
		}

		List<? extends Move> choices = state.possibleMoves();

		// if depth <= 0, don't stop if our next move is a jump -- keep searching
		// if depth <= 0 and no jumps, stop searching
		if (depth <= 0 && !choices.get(0).isJump()) {
			evals++;
			double util = Utils.utilityOf(state);
			return (state.playerToMove() == PlayerId.WHITE) ? util : -util;
		}

		for (Move choice : choices) {
			state.makeMoveUnchecked(choice);
			double util = -negamax(state, depth - 1,  -beta, -alpha);
			state.undoMoveUnchecked(choice);

			if (util > alpha) {
				alpha = util;
			}

			// this is sufficient for alpha-beta pruning
			if (alpha >= beta) {
				return alpha;
			}
		}
		return alpha;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		NegamaxExtensionPlayer clone =  (NegamaxExtensionPlayer) super.clone();
		clone.evals = 0;
		clone.searches = 0;
		return clone;
	}

	@Override
	public String toString() {
		return "Negamax extension player with depth " + searchDepth;
	}

}
