package put.ci.cevo.games;

import static com.google.common.base.Objects.toStringHelper;

/**
 * Only the number of pieces at the end of the game counts.
 * 
 * @author wojciech
 */
public class MorePieciesBoardEvaluator implements BoardEvaluator {

	private final double pointsForWin;
	private final double pointsForLose;
	private final double pointsForDraw;

	public MorePieciesBoardEvaluator(double pointsForWin, double pointsForLose, double pointsForDraw) {
		this.pointsForWin = pointsForWin;
		this.pointsForLose = pointsForLose;
		this.pointsForDraw = pointsForDraw;
	}

	@Override
	public GameOutcome evaluate(Board board) {
		final int blackPieces = board.countPieces(Board.BLACK);
		final int whitePieces = board.countPieces(Board.WHITE);

		if (blackPieces > whitePieces) {
			return new GameOutcome(pointsForWin, pointsForLose);
		} else if (blackPieces < whitePieces) {
			return new GameOutcome(pointsForLose, pointsForWin);
		}
		return new GameOutcome(pointsForDraw, pointsForDraw);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("pointsForWin", pointsForWin).add("pointsForLose", pointsForLose)
			.add("pointsForDraw", pointsForDraw).toString();
	}
}
