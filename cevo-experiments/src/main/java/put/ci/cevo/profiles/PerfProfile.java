package put.ci.cevo.profiles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import put.ci.cevo.framework.DeterministicThreadedWork;
import put.ci.cevo.framework.DeterministicThreadedWork.Worker;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.state.EvaluatedIndividual;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class PerfProfile {

	public static final class Bucket {

		private final double mean;
		private final double stdev;
		private final int count;

		public Bucket(double mean, double stdev, int count) {
			this.mean = mean;
			this.stdev = stdev;
			this.count = count;
		}
	}

	private static final class BucketJob<T> {
		public int bucketNo;
		public List<T> individuals;

		public BucketJob(int bucketNo, List<T> individuals) {
			this.bucketNo = bucketNo;
			this.individuals = individuals;
		}
	}

	public static class PerfProfileBuilder {

		private final Bucket[] buckets;

		public PerfProfileBuilder(int numBuckets) {
			buckets = new Bucket[numBuckets];
		}

		public synchronized void setBucketPerformance(int bucketIdx, DescriptiveStatistics stats) {
			buckets[bucketIdx] = new Bucket(stats.getMean(), stats.getStandardDeviation(), (int) stats.getN());
		}

		public PerfProfile buildPerfProfile() {
			return new PerfProfile(this);
		}

	}

	private final ImmutableList<Bucket> buckets;

	private PerfProfile(PerfProfileBuilder builder) {
		buckets = ImmutableList.copyOf(builder.buckets);
	}

	public int getNumBuckets() {
		return buckets.size();
	}

	public double getBucketPerformance(int idx) {
		return idx / (double) getNumBuckets();
	}

	public void saveAsCSV(File file) throws FileNotFoundException {
		try (PrintWriter out = new PrintWriter(file)) {
			out.println("BucketPerf,Mean,Stdev,Count");
			for (int b = 0; b < getNumBuckets(); ++b)
				out.println(String.format("%.2f,%.3f,%.3f,%d", getBucketPerformance(b), buckets.get(b).mean,
					buckets.get(b).stdev, buckets.get(b).count));
		}
	}

	/**
	 * Create a performance profile for a team of players using multiple threads
	 */
	public static <S, T> PerfProfile createForPlayerTeam(final PerfProfileDatabase<T> db,
			final InteractionDomain<S, T> interaction, final List<S> players, int numThreads) {
		final PerfProfileBuilder builder = new PerfProfileBuilder(db.getNumBuckets());

		// 1 task = 1 bucket
		Worker<BucketJob<T>> worker = new Worker<BucketJob<T>>() {
			@Override
			public void process(BucketJob<T> bucket, RandomDataGenerator random) {
				DescriptiveStatistics bucketStatistics = new DescriptiveStatistics();
				for (S player : players) {
					for (T opponent : bucket.individuals) {
						double result = interaction.interact(player, opponent, random).firstResult();
						bucketStatistics.addValue(result);
					}
				}
				builder.setBucketPerformance(bucket.bucketNo, bucketStatistics);
			}
		};

		List<BucketJob<T>> jobs = createJobs(db);

		DeterministicThreadedWork.run("PerfProfileGenerator", numThreads, worker, jobs);

		return builder.buildPerfProfile();
	}

	/**
	 * <code>numThreads</code> defaults to the number of available processors
	 * 
	 * @see PerfProfile#createForPlayerTeam(PerfProfileDatabase, InteractionDomain, List, int)
	 */
	public static <S, T> PerfProfile createForPlayerTeam(final PerfProfileDatabase<T> db,
			final InteractionDomain<S, T> interaction, final List<S> players) {
		return createForPlayerTeam(db, interaction, players, Runtime.getRuntime().availableProcessors());
	}

	private static <T> List<BucketJob<T>> createJobs(PerfProfileDatabase<T> db) {
		List<BucketJob<T>> jobs = Lists.newArrayList();
		for (int b = 0; b < db.getNumBuckets(); ++b) {
			jobs.add(new BucketJob<T>(b, toIndividuals(db.getBucket(b))));
		}
		return jobs;
	}

	private static <T> List<T> toIndividuals(List<EvaluatedIndividual<T>> evaluatedIndividuals) {
		List<T> res = Lists.newArrayList();
		for (EvaluatedIndividual<T> evaluatedIndividual : evaluatedIndividuals) {
			res.add(evaluatedIndividual.getIndividual());
		}
		return res;
	}
}
