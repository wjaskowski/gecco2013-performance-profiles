package put.ci.cevo.experiments.wpc;

import static com.google.common.base.Objects.toStringHelper;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.games.encodings.WPC;

import com.google.common.base.Preconditions;

public class WPCIndividualFactory implements IndividualFactory<WPC> {

	protected final int wpcLength;

	protected final double minWeight;
	protected final double maxWeight;

	public WPCIndividualFactory(double maxWeight, double minWeight, int wpcLength) {
		Preconditions.checkArgument(minWeight < maxWeight, "maxWeight must be greater than minWeight!");

		this.wpcLength = wpcLength;

		this.minWeight = minWeight;
		this.maxWeight = maxWeight;
	}

	@Override
	public WPC createRandomIndividual(RandomDataGenerator random) {
		WPC wpc = new WPC(wpcLength);
		for (int i = 0; i < wpcLength; ++i) {
			wpc.set(i, random.nextUniform(minWeight, maxWeight));
		}
		return wpc;
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("wpcLength", wpcLength).add("minWeight", minWeight).add("maxWeight", maxWeight)
			.toString();
	}

}
