package put.ci.cevo.games.othello;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.games.Board;
import put.ci.cevo.games.BoardEvaluator;
import put.ci.cevo.games.Game;
import put.ci.cevo.games.GameOutcome;

public class Othello implements Game<OthelloPlayer, OthelloPlayer> {
	
	protected static final int NULL_MOVE = Integer.MIN_VALUE;
	protected static final int[] COLORS = new int[] { Board.BLACK, Board.WHITE };

	private final OthelloBoard board;
	private final BoardEvaluator boardEvaluator;

	public Othello(BoardEvaluator boardEvaluator) {
		this.boardEvaluator = boardEvaluator;
		this.board = new OthelloBoard();
	}

	@Override
	public GameOutcome play(OthelloPlayer blackPlayer, OthelloPlayer whitePlayer, RandomDataGenerator random) {
		getBoard().reset();
		OthelloPlayer[] players = new OthelloPlayer[] { blackPlayer, whitePlayer };

		blackPlayer.reset(board);
		whitePlayer.reset(board);

		int lastOpponentMove = NULL_MOVE;
		boolean aMoveWasPossible;
		do {
			aMoveWasPossible = false;
			for (int p = 0; p < players.length; ++p) {
				OthelloPlayer player = players[p];
				int color = COLORS[p];

				int move = player.getMove(board, color, lastOpponentMove, random);
				if (move != NULL_MOVE) {
					board.makeMove(move, color);
					aMoveWasPossible = true;
				}
				lastOpponentMove = move;
			}
		} while (aMoveWasPossible);
		return boardEvaluator.evaluate(board);
	}

	public OthelloBoard getBoard() {
		return board;
	}
}
