package put.ci.cevo.framework.factories;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;

import com.google.common.base.Preconditions;

public class UniformRandomPopulationFactory<T> implements PopulationFactory<T> {

	private final IndividualFactory<T> individualFactory;

	public UniformRandomPopulationFactory(IndividualFactory<T> individualFactory) {
		this.individualFactory = individualFactory;
	}

	@Override
	public List<T> createPopulation(int populationSize, RandomDataGenerator random) {
		Preconditions.checkArgument(populationSize > 0, "Invalid population size: " + populationSize);

		final List<T> population = new ArrayList<T>(populationSize);
		for (int i = 0; i < populationSize; i++) {
			population.add(individualFactory.createRandomIndividual(random));
		}
		return population;
	}

}
