package put.ci.cevo.framework.measures;

import static com.google.common.base.Objects.toStringHelper;
import static java.lang.Thread.currentThread;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import put.ci.cevo.framework.ConcurrentRandomSupplier;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.interactions.InteractionDomain;

public class ExpectedUtility<S, T> implements PerformanceMeasure<S> {

	private final InteractionDomain<S, T> evaluator;
	
	private final List<T> sample;
	private final int sampleSize;

	public ExpectedUtility(InteractionDomain<S, T> evaluator, PopulationFactory<T> factory, int sampleSize) {
		this(evaluator, factory, sampleSize, ConcurrentRandomSupplier.forThread(currentThread()));
	}
	
	public ExpectedUtility(InteractionDomain<S, T> evaluator, PopulationFactory<T> factory, int sampleSize, RandomDataGenerator random) {
		this.evaluator = evaluator;
		this.sampleSize = sampleSize;
		this.sample = factory.createPopulation(sampleSize, random);
	}

	@Override
	public StatisticalSummary measure(S subject, RandomDataGenerator random) {
		SummaryStatistics stats = new SummaryStatistics();
		for (T opponent : sample) {
			double fitness = evaluator.interact(subject, opponent, random).firstResult();
			stats.addValue(fitness);
		}
		return stats;
	}
	
	@Override
	public String toString() {
		return toStringHelper(this).add("sample", sampleSize).toString();
	}

}
