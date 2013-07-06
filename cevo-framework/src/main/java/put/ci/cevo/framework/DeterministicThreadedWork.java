package put.ci.cevo.framework;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.partition;
import static java.lang.Runtime.getRuntime;
import static org.apache.commons.lang.StringUtils.substringBefore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;

public class DeterministicThreadedWork<T> {

	private static final Logger logger = Logger.getLogger(DeterministicThreadedWork.class);

	public static abstract class Worker<T> {

		/** Process one piece of the work. This method will be called from multiple threads. */
		public abstract void process(T piece, RandomDataGenerator random);

		/** Returns the description of the work piece, may be <code>null</code>. */
		public String getDescription(T piece) {
			return null;
		}
	}

	private class WorkingThread extends Thread implements Comparable<WorkingThread> {

		private final int threadNum;
		private final String baseName;

		private final RandomDataGenerator random;
		private final List<T> work;

		public WorkingThread(int threadNum, String baseName, List<T> work, RandomDataGenerator random) {
			super(baseName);
			this.threadNum = threadNum;
			this.baseName = baseName;
			this.work = work;
			this.random = random;
		}

		@Override
		public void run() {
			try {
				for (T elem : work) {
					setName(elem);
					worker.process(elem, random);
				}
			} catch (Throwable e) {
				logger.error("Working thread exception!", e);
			}
		}

		private void setName(T piece) {
			String description = worker.getDescription(piece);
			setName(description == null ? baseName : baseName + ":" + description);
		}

		@Override
		public int compareTo(WorkingThread o) {
			return Integer.valueOf(threadNum).compareTo(o.threadNum);
		}

		@Override
		public String toString() {
			return getName();
		}

	}

	private final String workName;
	private final int numThreads;

	private final Worker<T> worker;
	private final List<List<T>> work;

	private DeterministicThreadedWork(String workName, int numThreads, Worker<T> worker, List<List<T>> work) {
		Preconditions.checkArgument(numThreads > 0, "Invalid numThreads: " + numThreads);
		Preconditions.checkArgument(numThreads >= work.size(), "Unable to handle more partitions than threads!");
		this.workName = workName;
		this.worker = worker;
		this.work = work;
		this.numThreads = numThreads;
	}

	public void run() {
		final Map<String, RandomDataGenerator> pool = ConcurrentRandomSupplier.getPool();
		final List<WorkingThread> threads = new ArrayList<WorkingThread>(numThreads);

		for (int i = 0; i < work.size(); i++) {
			String threadName = substringBefore(workName, " ") + "Thread-" + i;
			WorkingThread thread = new WorkingThread(i, threadName, work.get(i), pool.get(threadName));
			threads.add(thread);
			thread.start();
		}
		try {
			for (WorkingThread thread : threads) {
				thread.join();
			}
		} catch (InterruptedException e) {
			logger.warn("Interrupted waiting for threads");
			for (WorkingThread thread : threads) {
				thread.interrupt();
			}
			return;
		}

	}

	public static <T> void run(String workName, Worker<T> worker, Iterable<T> work) {
		run(workName, getRuntime().availableProcessors(), worker, work);
	}

	public static <T> void run(String workName, int numThreads, Worker<T> worker, Iterable<T> work) {
		List<T> newArrayList = newArrayList(work);
		int partitionSize = (int) Math.ceil(newArrayList.size() / numThreads);
		run(workName, numThreads, worker, partition(newArrayList, partitionSize), false);
	}

	public static <T> void runWithInfo(String workName, int numThreads, Worker<T> worker, Iterable<T> work) {
		List<T> newArrayList = newArrayList(work);
		int partitionSize = (int) Math.ceil(newArrayList.size() / numThreads);
		run(workName, numThreads, worker, partition(newArrayList, partitionSize), true);
	}

	public static <T> void run(String workName, int numThreads, Worker<T> worker, List<List<T>> work, boolean info) {
		new DeterministicThreadedWork<T>(workName, numThreads, worker, work).run();
	}

}
