package put.ci.cevo.framework.interactions;

public class InteractionResult {

	private final double firstResult;
	private final double secondResult;

	private final int effort;

	public InteractionResult(double firstResult, double secondResult, int effort) {
		this.firstResult = firstResult;
		this.secondResult = secondResult;
		this.effort = effort;
	}

	public double firstResult() {
		return firstResult;
	}

	public double secondResult() {
		return secondResult;
	}

	public int getEffort() {
		return effort;
	}

	public static InteractionResult aggregate(InteractionResult first, InteractionResult second) {
		return new InteractionResult((first.firstResult + second.firstResult) / 2,
			(first.secondResult + second.secondResult) / 2, first.effort + second.effort);
	}
	
	public InteractionResult inverted() {
		return new InteractionResult(secondResult, firstResult, effort);
	}

	@Override
	public String toString() {
		return firstResult + ", " + secondResult + ", effort=" + effort;
	}
}
