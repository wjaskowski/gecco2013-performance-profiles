package put.ci.cevo.experiments.wpc.othello;

import static com.google.common.base.Objects.toStringHelper;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.games.BoardEvaluator;
import put.ci.cevo.games.Game;
import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.games.MorePieciesBoardEvaluator;
import put.ci.cevo.games.othello.Othello;
import put.ci.cevo.games.othello.OthelloPlayer;

/**
 * Describes interaction for the game of Othello. Players have to be OthelloPlayers
 * 
 * @author wojciech
 * 
 * @param <S>
 *            genotype type for the first player
 * @param <T>
 *            genotype type for the second player
 */
public class OthelloInteraction<S, T> implements InteractionDomain<S, T> {

	private final BoardEvaluator boardEvaluator;
	private final boolean playBoth;
	private GenotypePhenotypeMapper<T, OthelloPlayer> testMapper;
	private GenotypePhenotypeMapper<S, OthelloPlayer> candidateMapper;

	/**
	 * @param candidatesMapper
	 *            responsible to producing (phenotype of) player from its genotype
	 * @param testsMapper
	 *            responsible to producing (phenotype of) opponent from its genotype
	 * @param playBoth
	 *            if true, play two games with changed colors and return the average score
	 * @param boardEvaluator
	 *            how to count points when the game is finished
	 */
	public OthelloInteraction(GenotypePhenotypeMapper<S, OthelloPlayer> candidatesMapper,
			GenotypePhenotypeMapper<T, OthelloPlayer> testsMapper, boolean playBoth, BoardEvaluator boardEvaluator) {
		this.candidateMapper = candidatesMapper;
		this.testMapper = testsMapper;
		this.playBoth = playBoth;
		this.boardEvaluator = boardEvaluator;
	}

	/**
	 * <code>boardEvaluator</code> defaults to {@link MorePiecesBoardEvaluator(1, 0, 0.5)}.
	 * 
	 * @see OthelloInteraction
	 */
	public OthelloInteraction(GenotypePhenotypeMapper<S, OthelloPlayer> candidatesMapper,
			GenotypePhenotypeMapper<T, OthelloPlayer> testsMapper, boolean playBoth) {
		this(candidatesMapper, testsMapper, playBoth, new MorePieciesBoardEvaluator(1, 0, 0.5));
	}

	@Override
	public InteractionResult interact(S candidate, T test, RandomDataGenerator random) {
		OthelloPlayer player = candidateMapper.getPhenotype(candidate);
		OthelloPlayer opponent = testMapper.getPhenotype(test);

		return playBoth ? playDoubleGame(player, opponent, random) : play(player, opponent, random);
	}

	private InteractionResult playDoubleGame(OthelloPlayer player, OthelloPlayer opponent, RandomDataGenerator random) {
		InteractionResult firstResult = play(player, opponent, random);
		InteractionResult secondResult = play(opponent, player, random);
		return InteractionResult.aggregate(firstResult, secondResult.inverted());
	}

	private InteractionResult play(OthelloPlayer blackPlayer, OthelloPlayer whitePlayer, RandomDataGenerator random) {
		Game<OthelloPlayer, OthelloPlayer> othelloGame = new Othello(boardEvaluator);
		GameOutcome outcome = othelloGame.play(blackPlayer, whitePlayer, random);

		return new InteractionResult(outcome.blackPlayerPoints, outcome.whitePlayerPoints, 1);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("playBoth", playBoth).add("boardEvaluator", boardEvaluator)
			.add("testMapper", testMapper).add("candidateMapper", candidateMapper).toString();
	}
}
