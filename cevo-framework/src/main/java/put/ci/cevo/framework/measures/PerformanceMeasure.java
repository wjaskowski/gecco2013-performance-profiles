package put.ci.cevo.framework.measures;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

public interface PerformanceMeasure<V> {

	public StatisticalSummary measure(V subject, RandomDataGenerator random);

}
