package put.ci.cevo.experiments.wpc;

import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.games.encodings.WPC;

public class WPCPopulationFactory extends UniformRandomPopulationFactory<WPC> {

	public WPCPopulationFactory(IndividualFactory<WPC> individualFactory) {
		super(individualFactory);
	}

	public WPCPopulationFactory(double maxWeight, double minWeight, int wpcLength) {
		this(new WPCIndividualFactory(maxWeight, minWeight, wpcLength));
	}

}
