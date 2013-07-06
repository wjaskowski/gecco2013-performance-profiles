package put.ci.cevo.games.othello;

import org.apache.commons.math3.random.RandomDataGenerator;

public interface OthelloPlayer {

	/**
	 * The board is unchanged when returns (but might be written temporarily)
	 */
	public int getMove(OthelloBoard board, int color, int opponentLastMove, RandomDataGenerator random);

	/**
	 * Do something before games starts
	 */
	public void reset(OthelloBoard board);
	
}
