package put.ci.cevo.framework.factories;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;

public interface PopulationFactory<T> {

	public List<T> createPopulation(int populationSize, RandomDataGenerator random);
	
}
