package put.ci.cevo.games.othello;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.games.encodings.WPC;
import put.ci.cevo.util.ArrayUtils;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntDoubleLinkedSet;
import com.google.common.base.Preconditions;

public class OthelloWPCPlayer implements OthelloPlayer {

	private static final double INVALID_MOVE = Double.NEGATIVE_INFINITY;

	public static int NUM_WEIGHTS = OthelloBoard.SIZE * OthelloBoard.SIZE;

	private final OthelloWPC wpc;

	// A set of currently possible moves. Contains all valid moves, but some possible moves may be invalid.
	private final IntDoubleLinkedSet possibleMoves = new IntDoubleLinkedSet(OthelloBoard.BUFFER_SIZE,
		OthelloBoard.BUFFER_SIZE);

	private final double randomMoveProbability;

	/**
	 * Creates a new OthelloWPCPlayer.
	 * 
	 * @param randomMoveProbability
	 *            probability that instead of using move utility a move will be issued at random
	 */
	public OthelloWPCPlayer(WPC wpc, double randomMoveProbability) {
		Preconditions.checkArgument(0.0 <= randomMoveProbability && randomMoveProbability <= 1.0);

		this.wpc = new OthelloWPC(wpc);
		this.randomMoveProbability = randomMoveProbability;
	}

	/**
	 * Creates a new OthelloWPCPlayer. Random is used only when two moves have identical utility
	 */
	public OthelloWPCPlayer(WPC wpc) {
		this(wpc, 0.0);
	}

	/**
	 * Reset internal state, so it is ready to play on the given board
	 */
	@Override
	public void reset(OthelloBoard board) {
		possibleMoves.clear();
		for (int pos = 0; pos < board.buffer.length; pos++) {
			if (board.isEmpty(pos) || board.isWall(pos))
				continue;
			for (int dir : OthelloBoard.DIRS) {
				int neighbour = pos + dir;
				if (board.isEmpty(neighbour))
					possibleMoves.add(neighbour);
			}
		}
	}

	private void updatePossibleMoves(OthelloBoard board, int lastMove) {
		if (lastMove == Othello.NULL_MOVE)
			return;
		possibleMoves.remove(lastMove);
		for (int dir : OthelloBoard.DIRS) {
			int neighbour = lastMove + dir;
			if (board.isEmpty(neighbour))
				possibleMoves.add(neighbour);
		}
	}

	@Override
	public int getMove(OthelloBoard board, int player, int opponentLastMove, RandomDataGenerator random) {
		// Update possible moves with opponentLastMove
		updatePossibleMoves(board, opponentLastMove);

		int move = getMoveImpl(board, player, random);

		updatePossibleMoves(board, move);
		return move;
	}

	private int getMoveImpl(OthelloBoard board, int player, RandomDataGenerator random) {
		// Should I make a random move?
		boolean chooseRandomMove = false;
		if (randomMoveProbability > 0)
			if (random.nextUniform(0.0, 1.0) < randomMoveProbability)
				chooseRandomMove = true;

		double bestEval = INVALID_MOVE;
		final IntArrayList bestMoves = new IntArrayList();
		for (int i = 0; i < possibleMoves.elementsCount; ++i) {
			int move = possibleMoves.dense[i];

			double eval = deltaEvaluateMove(board, move, player);
			if (eval == INVALID_MOVE)
				continue;

			// bestMoves == all valid moves if chooseRandomMove
			if (chooseRandomMove || eval == bestEval) {
				bestMoves.add(move);
			} else if (bestEval < eval) {
				bestEval = eval;
				bestMoves.clear();
				bestMoves.add(move);
			}
		}

		if (bestEval == INVALID_MOVE)
			return Othello.NULL_MOVE;
		return ArrayUtils.getRandomElement(bestMoves, random);
	}

	/**
	 * Return the utility of a move for player number for the given board.
	 * 
	 * @return move utility or Double.NEGATIVE_INFINITY if move is invalid
	 */
	private double deltaEvaluateMove(OthelloBoard board, final int move, final int player) {
		assert board.isEmpty(move);

		double eval = 0;
		boolean moveIsValid = false;
		for (int dir : OthelloBoard.DIRS) {
			int pos = move + dir;
			if (board.buffer[pos] != -player)
				continue;

			double tmpEval = 0;

			do {
				tmpEval += 2 * wpc.buffer[pos];
				pos += dir;
			} while (board.buffer[pos] == -player);

			if (board.buffer[pos] == player) {
				eval += tmpEval;
				moveIsValid = true;
			}
		}

		if (!moveIsValid)
			return INVALID_MOVE;
		eval += wpc.buffer[move];
		return eval;
	}

	@SuppressWarnings("unused")
	private double evaluateDeltaBoard(OthelloBoard board, IntArrayList positions, int color) {
		double result = 0;
		for (int pos : positions.buffer) {
			double weight = wpc.buffer[pos];
			result += evaluateBoardField(color - board.buffer[pos], weight, color);
		}
		return result;
	}

	@SuppressWarnings("unused")
	private double evaluateBoard(OthelloBoard board, int color) {
		double result = 0;
		for (int pos = 0; pos <= board.buffer.length; pos++) {
			result += evaluateBoardField(board.buffer[pos], wpc.buffer[pos], color);
		}
		return result;
	}

	private double evaluateBoardField(int value, double weight, int color) {
		return value * weight * color;
	}
}
