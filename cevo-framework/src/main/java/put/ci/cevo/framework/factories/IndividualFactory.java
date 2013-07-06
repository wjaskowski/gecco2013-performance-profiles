package put.ci.cevo.framework.factories;

import org.apache.commons.math3.random.RandomDataGenerator;

public interface IndividualFactory<T> {

	public T createRandomIndividual(RandomDataGenerator random);

}
