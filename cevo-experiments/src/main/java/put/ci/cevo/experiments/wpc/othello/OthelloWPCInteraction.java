package put.ci.cevo.experiments.wpc.othello;

import put.ci.cevo.experiments.wpc.othello.mappers.OthelloWPCStandardMapper;
import put.ci.cevo.games.BoardEvaluator;
import put.ci.cevo.games.MorePieciesBoardEvaluator;
import put.ci.cevo.games.encodings.WPC;

public class OthelloWPCInteraction extends OthelloInteraction<WPC, WPC> {

	public OthelloWPCInteraction(boolean playBoth) {
		this(new MorePieciesBoardEvaluator(1, 0, 0.5), playBoth);
	}

	public OthelloWPCInteraction(BoardEvaluator board, boolean playBoth) {
		super(new OthelloWPCStandardMapper(), new OthelloWPCStandardMapper(), playBoth, board);
	}
}
