package put.ci.cevo.framework.measures;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;

import put.ci.cevo.framework.DeterministicThreadedWork;
import put.ci.cevo.framework.DeterministicThreadedWork.Worker;

public class PerformanceMeasureUtils {
	/**
	 * Returns performance measure as an average measure performance of a team
	 */
	public static <S> StatisticalSummary measurePerformanceForTeam(List<S> team, final PerformanceMeasure<S> measure,
			@SuppressWarnings("unused") RandomDataGenerator random) {
		// TODO Random should be used, but this requires rework how random works in our framework
		// TODO Auto-generated method stub

		final SynchronizedDescriptiveStatistics stats = new SynchronizedDescriptiveStatistics();

		Worker<S> worker = new Worker<S>() {
			@Override
			public void process(S player, RandomDataGenerator random) {
				StatisticalSummary perf = measure.measure(player, random);
				stats.addValue(perf.getMean());
				// TODO: Watch out - we threat mean as the real value, but in fact this is just an approximation!
			}
		};
		DeterministicThreadedWork.run("PerfProfileGenerator", worker, team);

		return stats;
	}
}
