package put.ci.cevo.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.inference.TestUtils;

import com.carrotsearch.hppc.DoubleArrayList;
import com.carrotsearch.hppc.procedures.DoubleProcedure;

public class Statistics {

	private final DoubleArrayList observations;

	private Statistics(DoubleArrayList observations) {
		this.observations = observations;
	}

	public double[] getSample() {
		return observations.toArray();
	}

	public StatisticalSummary asStatisticalSummary() {
		final SummaryStatistics statistics = new SummaryStatistics();
		observations.forEach(new DoubleProcedure() {
			@Override
			public void apply(double value) {
				statistics.addValue(value);
			}
		});
		return statistics;
	}

	public String meanWithConfidence() {
		StatisticalSummary statistics = asStatisticalSummary();
		return format(statistics.getMean()) + " ± " + format(getConfidenceIntervalDelta(statistics, 0.05));
	}
	public double getConfidenceInterval() {
		return getConfidenceIntervalDelta(asStatisticalSummary(), 0.05);
	}

	public double ttest(Statistics other) {
		return TestUtils.t(other.getSample(), this.getSample());
	}

	public double pvalue(Statistics other) {
		return TestUtils.homoscedasticTTest(other.getSample(), this.getSample()) / 2;
	}

	public static String meanWithCofidenceInterval(StatisticalSummary statistics, double significance) {
		return format(statistics.getMean()) + " ± " + format(getConfidenceIntervalDelta(statistics, significance));
	}
	
	/** Computes Half of the significance interval width */
	public static double getConfidenceIntervalDelta(StatisticalSummary stats, double significance) {
		TDistribution tDist = new TDistribution(stats.getN() - 1);
		double a = tDist.inverseCumulativeProbability(1.0 - significance / 2);
		return a * stats.getStandardDeviation() / Math.sqrt(stats.getN());
	}

	public static Statistics create(DoubleArrayList observations) {
		return new Statistics(observations);
	}

	public static Statistics create(Iterable<Double> observations) {
		DoubleArrayList doubles = new DoubleArrayList();
		for (Double value : observations) {
			doubles.add(value);
		}
		return new Statistics(doubles);
	}

	public static String format(double value) {
		return format(value, 4);
	}

	public static String format(double value, int digits) {
		if (digits < 0) {
			throw new IllegalArgumentException("Digits: " + digits);
		}
		if (digits == 0) {
			return Integer.toString((int) value);
		}
		return StringUtils.replace(String.format("%." + digits + "f", value), ",", ".");
	}
}