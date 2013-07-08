package put.ci.cevo.util;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.math3.random.RandomDataGenerator;

public class RandomUtils {

	/**
	 * Returns a sample without replacement. Complexity: O(mlog(m)). This is an iterative implementation of the Floyd's
	 * random sampling algorithm (cf. http://dl.acm.org/citation.cfm?id=315746&dl=ACM&coll=DL). It's much faster than
	 * the other implementations, especially for small <code>m</code> and large <code>Lists</code>.
	 */
	public static <T> List<T> sample(List<T> items, int m, RandomDataGenerator random) {
		final SortedSet<T> res = new TreeSet<T>();
		final int n = items.size();
		for (int i = n - m; i < n; i++) {
			int pos = random.nextInt(0, i);
			T item = items.get(pos);
			if (res.contains(item)) {
				res.add(items.get(i));
			} else {
				res.add(item);
			}
		}
		return new ArrayList<T>(res);
	}

	/**
	 * Shuffles a list. An implementation of a variant of Fisher-Yates-Knuth algorithm (cf.
	 * http://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle)
	 */
	public static <T> void shuffle(List<T> items, RandomDataGenerator random) {
		shuffle(items, items.size() - 1, random);
	}

	/**
	 * Shuffles only first numFirstElements. The rest of the array also may be changed, but we don't care how
	 */
	private static <T> void shuffle(List<T> items, int numFirstElements, RandomDataGenerator random) {
		numFirstElements = Math.min(numFirstElements, items.size() - 1);
		for (int i = 0; i < numFirstElements; ++i) {
			int nextPos = nextInt(i, items.size() - 1, random);
			T tmp = items.get(nextPos);
			items.set(nextPos, items.get(i));
			items.set(i, tmp);
		}

	}

	/**
	 * Get a sample without replacement. Complexity: O(n), but with a low constant. There is slightly quicker algorithm
	 * by Knuth (Algorithm 3.4.2S of Knuth's book Seminumeric Algorithms), implemented here
	 * http://www.javamex.com/tutorials/random_numbers/random_sample.shtml
	 */
	public static <T> List<T> randomSample(List<T> items, int samplesSize, RandomDataGenerator random) {
		List<T> copy = new ArrayList<T>(items);
		shuffle(copy, samplesSize, random);
		return copy.subList(0, samplesSize);
	}

	/** Picks a random item from a list */
	public static <T> T pickRandom(List<T> items, RandomDataGenerator random) {
		return items.get(nextInt(0, items.size() - 1, random));
	}
	
	/** Same as random.nextInt(lower, upper), but does not throw exception when lower == upper */
	public static int nextInt(int lower, int upper, RandomDataGenerator random) {
		return lower == upper ? lower : random.nextInt(lower, upper);
	}

}