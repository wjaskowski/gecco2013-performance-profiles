package put.ci.cevo.profiles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import put.ci.cevo.framework.state.EvaluatedIndividual;

public class PerfProfileDatabase<T> implements Iterable<List<EvaluatedIndividual<T>>> {

	private final List<List<EvaluatedIndividual<T>>> buckets;

	public PerfProfileDatabase(int numBuckets) {
		buckets = new ArrayList<List<EvaluatedIndividual<T>>>();
		for (int i = 0; i < numBuckets; ++i)
			buckets.add(new ArrayList<EvaluatedIndividual<T>>());
	}

	// TODO: Make it immutable. Create a PerfProfileDatabaseBuilder
	public void setBucket(int idx, List<EvaluatedIndividual<T>> bucket) {
		buckets.set(idx, bucket);
	}

	public List<EvaluatedIndividual<T>> getBucket(int idx) {
		return buckets.get(idx);
	}

	public int getNumBuckets() {
		return buckets.size();
	}

	public double getBucketPerformance(int idx) {
		return idx / (double) getNumBuckets();
	}

	public int getTotalNumElements() {
		int cnt = 0;
		for (List<EvaluatedIndividual<T>> bucket : buckets)
			cnt += bucket.size();
		return cnt;
	}

	@Override
	public String toString() {
		String str = "[b=" + buckets.size() + ", s=" + getTotalNumElements() + "] ";
		for (int i = 0; i < getNumBuckets(); ++i)
			if (getBucket(i).size() > 0)
				str += String.format(" %.2f:%d,", getBucketPerformance(i), getBucket(i).size());
		return str;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PerfProfileDatabase)) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		PerfProfileDatabase that = (PerfProfileDatabase) obj;
		return this.buckets.equals(that.buckets);
	}

	@Override
	public int hashCode() {
		return buckets.hashCode();
	}

	@Override
	public Iterator<List<EvaluatedIndividual<T>>> iterator() {
		return buckets.iterator();
	}
}
