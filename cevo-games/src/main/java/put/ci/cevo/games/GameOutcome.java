package put.ci.cevo.games;

import org.apache.commons.lang.NotImplementedException;

public class GameOutcome {

	public final double blackPlayerPoints;
	public final double whitePlayerPoints;

	public GameOutcome(double blackPlayerPoints, double whitePlayerPoints) {
		this.blackPlayerPoints = blackPlayerPoints;
		this.whitePlayerPoints = whitePlayerPoints;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GameOutcome)) {
			return false;
		}
		GameOutcome o = (GameOutcome) obj;
		return blackPlayerPoints == o.blackPlayerPoints && whitePlayerPoints == o.whitePlayerPoints;
	}

	@Override
	public int hashCode() {
		throw new NotImplementedException();
	}

	@Override
	public String toString() {
		return "o(" + blackPlayerPoints + "," + whitePlayerPoints + ")";
	}
}
