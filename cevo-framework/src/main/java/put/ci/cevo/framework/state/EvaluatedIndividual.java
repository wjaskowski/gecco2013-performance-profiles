package put.ci.cevo.framework.state;

import static com.google.common.base.Objects.toStringHelper;

import java.io.Serializable;

public class EvaluatedIndividual<T> implements Comparable<EvaluatedIndividual<T>>, Serializable {

	private static final long serialVersionUID = 201303191025L;

	private final double fitness;
	private final T individual;

	private final int effort;
	private final int generation;

	public EvaluatedIndividual(T individual, double fitness) {
		this(individual, fitness, -1, -1);
	}

	public EvaluatedIndividual(T individual, double fitness, int generation, int effort) {
		this.individual = individual;
		this.fitness = fitness;
		this.generation = generation;
		this.effort = effort;
	}

	public T getIndividual() {
		return individual;
	}

	public double getFitness() {
		return fitness;
	}

	public int getGeneration() {
		return generation;
	}

	public int getEffort() {
		return effort;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(EvaluatedIndividual<T> evaluatedIndividual) {
		int compare = Double.compare(fitness, evaluatedIndividual.getFitness());
		if (compare == 0) {
			if (individual instanceof Comparable<?>) {
				return ((Comparable<T>) individual).compareTo(evaluatedIndividual.getIndividual());
			}
		}
		return compare;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final EvaluatedIndividual<?> that = (EvaluatedIndividual<?>) o;

		return this.fitness == that.fitness && this.effort == that.effort && this.generation == that.generation
			&& this.individual.equals(that.individual);
	}

	@Override
	public int hashCode() {
		return Double.valueOf(fitness).hashCode();
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("Fitness", fitness).add("Generation", generation).toString();
	}

	public EvaluatedIndividual<T> withObjectiveFitness(double fitness) {
		return new EvaluatedIndividual<T>(individual, fitness, generation, effort);
	}

}
