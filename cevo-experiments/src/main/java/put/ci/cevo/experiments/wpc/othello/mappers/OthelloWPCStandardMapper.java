package put.ci.cevo.experiments.wpc.othello.mappers;

import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.games.encodings.WPC;
import put.ci.cevo.games.othello.OthelloPlayer;
import put.ci.cevo.games.othello.OthelloWPCPlayer;

public class OthelloWPCStandardMapper implements GenotypePhenotypeMapper<WPC, OthelloPlayer> {
	@Override
	public OthelloWPCPlayer getPhenotype(WPC genotype) {
		return new OthelloWPCPlayer(genotype);
	}
}
